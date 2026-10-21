package com.example.service

import android.net.VpnService
import android.util.Log
import java.io.OutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * TunPacketRouter
 * 
 * Roteador de pacotes nativo para interface TUN do Android.
 * Processa pacotes IPv4 recebidos do descritor de arquivo:
 * 1. Responde requisições ICMP Echo (Ping) para simulação de conectividade ativa com baixa latência.
 * 2. Resolve consultas DNS (UDP porta 53) redirecionando para servidores DNS reais através de DatagramSocket protegido.
 * Isso garante que todos os navegadores e aplicativos resolvam nomes de domínio reais sem erro 404 e sem falhas de rede.
 */
class TunPacketRouter(
    private val vpnService: VpnService,
    private val primaryDns: String = "1.1.1.1",
    private val isSocial: Boolean = false
) {
    private var dnsSocket: DatagramSocket? = null

    init {
        try {
            val sock = DatagramSocket()
            vpnService.protect(sock)
            sock.soTimeout = 3000
            dnsSocket = sock
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao inicializar DatagramSocket protegido para DNS", e)
        }
    }

    fun handlePacket(packet: ByteArray, length: Int, out: OutputStream) {
        if (length < 20) return
        val versionAndIhl = packet[0].toInt() and 0xFF
        if (versionAndIhl ushr 4 != 4) return // Apenas IPv4
        val ihl = (versionAndIhl and 0x0F) * 4
        if (length < ihl) return

        val protocol = packet[9].toInt() and 0xFF

        when (protocol) {
            1 -> handleIcmp(packet, length, ihl, out)
            17 -> handleUdp(packet, length, ihl, out)
        }
    }

    private fun handleIcmp(packet: ByteArray, length: Int, ihl: Int, out: OutputStream) {
        if (length < ihl + 8) return
        val type = packet[ihl].toInt() and 0xFF
        if (type == 8) { // Echo Request (Ping)
            // Transformar em Echo Reply (Type 0)
            packet[ihl] = 0

            // Trocar IP Origem e Destino
            for (i in 0..3) {
                val tmp = packet[12 + i]
                packet[12 + i] = packet[16 + i]
                packet[16 + i] = tmp
            }

            // Recalcular Checksum ICMP
            packet[ihl + 2] = 0
            packet[ihl + 3] = 0
            val icmpLen = length - ihl
            val icmpCksum = calculateChecksum(packet, ihl, icmpLen)
            packet[ihl + 2] = (icmpCksum ushr 8).toByte()
            packet[ihl + 3] = (icmpCksum and 0xFF).toByte()

            // Recalcular Checksum IP
            packet[10] = 0
            packet[11] = 0
            val ipCksum = calculateChecksum(packet, 0, ihl)
            packet[10] = (ipCksum ushr 8).toByte()
            packet[11] = (ipCksum and 0xFF).toByte()

            try {
                synchronized(out) {
                    out.write(packet, 0, length)
                    out.flush()
                }
            } catch (_: Exception) {}
        }
    }

    private fun handleUdp(packet: ByteArray, length: Int, ihl: Int, out: OutputStream) {
        if (length < ihl + 8) return
        val srcPort = ((packet[ihl].toInt() and 0xFF) shl 8) or (packet[ihl + 1].toInt() and 0xFF)
        val dstPort = ((packet[ihl + 2].toInt() and 0xFF) shl 8) or (packet[ihl + 3].toInt() and 0xFF)

        if (dstPort == 53) { // Consulta DNS
            val udpLen = ((packet[ihl + 4].toInt() and 0xFF) shl 8) or (packet[ihl + 5].toInt() and 0xFF)
            val payloadLen = udpLen - 8
            if (payloadLen > 0 && ihl + 8 + payloadLen <= length) {
                val dnsQuery = packet.copyOfRange(ihl + 8, ihl + 8 + payloadLen)
                forwardDnsQuery(dnsQuery, packet, ihl, srcPort, out)
            }
        }
    }

    private fun forwardDnsQuery(
        dnsQuery: ByteArray,
        origPacket: ByteArray,
        ihl: Int,
        clientPort: Int,
        out: OutputStream
    ) {
        if (isSocial) {
            val qDomain = SocialNetworksFilter.extractDnsQueryDomain(dnsQuery)
            if (qDomain != null && !SocialNetworksFilter.isHostAllowedInSocialMode(qDomain)) {
                Log.d(TAG, "[DNS Firewall] Bloqueando domínio não-social: $qDomain")
                sendDnsBlockedResponse(dnsQuery, origPacket, ihl, clientPort, out)
                return
            }
        }

        val socket = dnsSocket ?: return
        try {
            val targetIp = try {
                val ipBytes = byteArrayOf(
                    origPacket[16], origPacket[17], origPacket[18], origPacket[19]
                )
                InetAddress.getByAddress(ipBytes)
            } catch (_: Exception) {
                InetAddress.getByName(primaryDns)
            }

            val sendPacket = DatagramPacket(dnsQuery, dnsQuery.size, targetIp, 53)
            socket.send(sendPacket)

            val receiveBuf = ByteArray(2048)
            val recvPacket = DatagramPacket(receiveBuf, receiveBuf.size)
            socket.receive(recvPacket)

            val responsePayloadLen = recvPacket.length
            val newUdpLen = 8 + responsePayloadLen
            val newIpTotalLen = ihl + newUdpLen

            val respPacket = ByteArray(newIpTotalLen)
            // Copiar cabeçalho IP original
            System.arraycopy(origPacket, 0, respPacket, 0, ihl)

            // Inverter IPs: Destino vira Origem, Origem vira Destino
            for (i in 0..3) {
                respPacket[12 + i] = origPacket[16 + i]
                respPacket[16 + i] = origPacket[12 + i]
            }

            // Atualizar tamanho total do IP
            respPacket[2] = (newIpTotalLen ushr 8).toByte()
            respPacket[3] = (newIpTotalLen and 0xFF).toByte()

            // Recalcular Checksum IP
            respPacket[10] = 0
            respPacket[11] = 0
            val ipCksum = calculateChecksum(respPacket, 0, ihl)
            respPacket[10] = (ipCksum ushr 8).toByte()
            respPacket[11] = (ipCksum and 0xFF).toByte()

            // Cabeçalho UDP: Porta Origem = 53, Porta Destino = clientPort
            respPacket[ihl] = 0
            respPacket[ihl + 1] = 53
            respPacket[ihl + 2] = (clientPort ushr 8).toByte()
            respPacket[ihl + 3] = (clientPort and 0xFF).toByte()
            respPacket[ihl + 4] = (newUdpLen ushr 8).toByte()
            respPacket[ihl + 5] = (newUdpLen and 0xFF).toByte()
            respPacket[ihl + 6] = 0
            respPacket[ihl + 7] = 0

            // Copiar resposta DNS
            System.arraycopy(recvPacket.data, 0, respPacket, ihl + 8, responsePayloadLen)

            synchronized(out) {
                out.write(respPacket, 0, newIpTotalLen)
                out.flush()
            }
        } catch (_: Exception) {}
    }

    private fun sendDnsBlockedResponse(
        dnsQuery: ByteArray,
        origPacket: ByteArray,
        ihl: Int,
        clientPort: Int,
        out: OutputStream
    ) {
        try {
            val dnsResp = dnsQuery.copyOf()
            // Set flags: Response (0x81), NXDOMAIN (0x83)
            dnsResp[2] = 0x81.toByte()
            dnsResp[3] = 0x83.toByte()
            dnsResp[6] = 0
            dnsResp[7] = 0
            dnsResp[8] = 0
            dnsResp[9] = 0
            dnsResp[10] = 0
            dnsResp[11] = 0

            val responsePayloadLen = dnsResp.size
            val newUdpLen = 8 + responsePayloadLen
            val newIpTotalLen = ihl + newUdpLen

            val respPacket = ByteArray(newIpTotalLen)
            System.arraycopy(origPacket, 0, respPacket, 0, ihl)

            for (i in 0..3) {
                respPacket[12 + i] = origPacket[16 + i]
                respPacket[16 + i] = origPacket[12 + i]
            }

            respPacket[2] = (newIpTotalLen ushr 8).toByte()
            respPacket[3] = (newIpTotalLen and 0xFF).toByte()

            respPacket[10] = 0
            respPacket[11] = 0
            val ipCksum = calculateChecksum(respPacket, 0, ihl)
            respPacket[10] = (ipCksum ushr 8).toByte()
            respPacket[11] = (ipCksum and 0xFF).toByte()

            respPacket[ihl] = 0
            respPacket[ihl + 1] = 53
            respPacket[ihl + 2] = (clientPort ushr 8).toByte()
            respPacket[ihl + 3] = (clientPort and 0xFF).toByte()
            respPacket[ihl + 4] = (newUdpLen ushr 8).toByte()
            respPacket[ihl + 5] = (newUdpLen and 0xFF).toByte()
            respPacket[ihl + 6] = 0
            respPacket[ihl + 7] = 0

            System.arraycopy(dnsResp, 0, respPacket, ihl + 8, responsePayloadLen)

            synchronized(out) {
                out.write(respPacket, 0, newIpTotalLen)
                out.flush()
            }
        } catch (_: Exception) {}
    }

    private fun calculateChecksum(data: ByteArray, offset: Int, length: Int): Int {
        var sum = 0
        var i = offset
        while (i < offset + length - 1) {
            val word = ((data[i].toInt() and 0xFF) shl 8) or (data[i + 1].toInt() and 0xFF)
            sum += word
            i += 2
        }
        if (i < offset + length) {
            sum += (data[i].toInt() and 0xFF) shl 8
        }
        while (sum ushr 16 > 0) {
            sum = (sum and 0xFFFF) + (sum ushr 16)
        }
        return sum.inv() and 0xFFFF
    }

    fun close() {
        try {
            dnsSocket?.close()
            dnsSocket = null
        } catch (_: Exception) {}
    }

    companion object {
        private const val TAG = "TunPacketRouter"
    }
}
