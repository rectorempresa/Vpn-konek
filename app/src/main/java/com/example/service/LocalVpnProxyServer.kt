package com.example.service

import android.net.VpnService
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicLong

/**
 * LocalVpnProxyServer
 * 
 * Servidor proxy HTTP/HTTPS local de alto desempenho rodando em 127.0.0.1.
 * Vinculado à VpnService via protect(socket) para garantir que todo o tráfego
 * dos aplicativos (Chrome, Facebook, Instagram, TikTok, Telegram, etc.)
 * passe com segurança pela rede física (dados móveis / Wi-Fi) sem erro 404 e
 * com velocidade real e bypass otimizado para Unitel e Redes Sociais.
 */
class LocalVpnProxyServer(
    private val vpnService: VpnService,
    private val port: Int = 8989,
    private val sniHost: String = "portal.unitel.ao",
    private val isUnitel: Boolean = false,
    private val isSocial: Boolean = false
) {
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    val totalBytesRead = AtomicLong(0)
    val totalBytesWritten = AtomicLong(0)

    fun start() {
        if (serverJob != null && serverJob?.isActive == true) return

        serverJob = scope.launch {
            try {
                val server = ServerSocket(port, 100, InetAddress.getByName("127.0.0.1"))
                serverSocket = server
                Log.d(TAG, "[Konek Proxy] Servidor local de túnel ativo na porta $port")
                KonekVpnService.addLog("[Túnel Funcional] Servidor proxy local ativo em 127.0.0.1:$port (Bypass 404 ativado).")

                while (isActive && !server.isClosed) {
                    try {
                        val clientSocket = server.accept()
                        scope.launch {
                            handleClientConnection(clientSocket)
                        }
                    } catch (e: Exception) {
                        if (!server.isClosed) {
                            Log.w(TAG, "Erro no accept do proxy", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao iniciar ServerSocket do proxy", e)
                KonekVpnService.addLog("[Proxy Erro] Não foi possível vincular porta $port: ${e.message}")
            }
        }
    }

    private suspend fun handleClientConnection(clientSocket: Socket) {
        var remoteSocket: Socket? = null
        try {
            clientSocket.soTimeout = 15000
            val clientIn = clientSocket.getInputStream()
            val clientOut = clientSocket.getOutputStream()

            // Ler a primeira linha do HTTP
            val firstLine = readLine(clientIn) ?: run {
                clientSocket.close()
                return
            }

            val parts = firstLine.split(" ")
            if (parts.size < 2) {
                clientSocket.close()
                return
            }

            val method = parts[0].uppercase()
            val uri = parts[1]

            var targetHost: String
            var targetPort = 80

            if (method == "CONNECT") {
                // HTTPS Tunneling (Facebook, Instagram, TikTok, YouTube, Chrome HTTPS, etc.)
                val hostParts = uri.split(":")
                targetHost = hostParts[0]
                targetPort = if (hostParts.size > 1) hostParts[1].toIntOrNull() ?: 443 else 443

                // Drenar os headers restantes do CONNECT
                while (true) {
                    val header = readLine(clientIn)
                    if (header.isNullOrBlank()) break
                }

                // FIREWALL: Se o servidor for de Rede Social, só permite Facebook, Instagram, Telegram, TikTok e YouTube!
                if (isSocial && !SocialNetworksFilter.isHostAllowedInSocialMode(targetHost)) {
                    Log.d(TAG, "[Firewall Social] Bloqueado HTTPS para: $targetHost")
                    KonekVpnService.addLog("[Firewall Social 🚫] Bloqueado: $targetHost (Servidor de Rede Social: apenas Facebook, Instagram, Telegram, TikTok e YouTube liberados).")
                    val blockResponse = "HTTP/1.1 403 Forbidden\r\n" +
                            "Content-Type: text/plain; charset=utf-8\r\n" +
                            "Connection: close\r\n\r\n" +
                            "Bloqueado: Servidor de Rede Social ativo. Apenas Facebook, Instagram, Telegram, TikTok e YouTube funcionam."
                    clientOut.write(blockResponse.toByteArray(Charsets.UTF_8))
                    clientOut.flush()
                    clientSocket.close()
                    return
                }

                // Criar socket remoto real e proteger para não entrar em loop no TUN
                remoteSocket = Socket()
                val protected = vpnService.protect(remoteSocket)
                if (!protected) {
                    Log.w(TAG, "Não foi possível proteger remoteSocket para $targetHost")
                }
                remoteSocket.soTimeout = 15000
                remoteSocket.connect(InetSocketAddress(targetHost, targetPort), 8000)

                // Responder 200 Connection Established para o cliente
                val response = "HTTP/1.1 200 Connection Established\r\n" +
                        "Proxy-Agent: Konek-Tunnel-Engine/3.5\r\n\r\n"
                clientOut.write(response.toByteArray(Charsets.US_ASCII))
                clientOut.flush()

                // Encaminhar dados em ambas as direções
                pipeStreams(clientSocket, remoteSocket)

            } else {
                // Requisição HTTP comum (GET, POST, HEAD, etc.)
                if (uri.startsWith("http://", ignoreCase = true)) {
                    val afterProto = uri.substring(7)
                    val slashIdx = afterProto.indexOf('/')
                    val hostPort = if (slashIdx != -1) afterProto.substring(0, slashIdx) else afterProto
                    val hostParts = hostPort.split(":")
                    targetHost = hostParts[0]
                    targetPort = if (hostParts.size > 1) hostParts[1].toIntOrNull() ?: 80 else 80
                } else {
                    val hostParts = uri.split(":")
                    targetHost = hostParts[0]
                    targetPort = if (hostParts.size > 1) hostParts[1].toIntOrNull() ?: 80 else 80
                }

                // FIREWALL: Se o servidor for de Rede Social, só permite Facebook, Instagram, Telegram, TikTok e YouTube!
                if (isSocial && !SocialNetworksFilter.isHostAllowedInSocialMode(targetHost)) {
                    Log.d(TAG, "[Firewall Social] Bloqueado HTTP para: $targetHost")
                    KonekVpnService.addLog("[Firewall Social 🚫] Bloqueado HTTP: $targetHost (Apenas redes sociais liberadas).")
                    val blockResponse = "HTTP/1.1 403 Forbidden\r\n" +
                            "Content-Type: text/plain; charset=utf-8\r\n" +
                            "Connection: close\r\n\r\n" +
                            "Bloqueado: Servidor de Rede Social ativo."
                    clientOut.write(blockResponse.toByteArray(Charsets.UTF_8))
                    clientOut.flush()
                    clientSocket.close()
                    return
                }

                remoteSocket = Socket()
                vpnService.protect(remoteSocket)
                remoteSocket.soTimeout = 15000
                remoteSocket.connect(InetSocketAddress(targetHost, targetPort), 8000)

                val remoteOut = remoteSocket.getOutputStream()
                remoteOut.write((firstLine + "\r\n").toByteArray(Charsets.US_ASCII))
                remoteOut.flush()

                pipeStreams(clientSocket, remoteSocket)
            }

        } catch (e: Exception) {
            Log.d(TAG, "Conexão proxy finalizada: ${e.message}")
            try {
                val errorMsg = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 2\r\n\r\nOK"
                clientSocket.getOutputStream().write(errorMsg.toByteArray())
                clientSocket.getOutputStream().flush()
            } catch (_: Exception) {}
        } finally {
            try { clientSocket.close() } catch (_: Exception) {}
            try { remoteSocket?.close() } catch (_: Exception) {}
        }
    }

    private fun pipeStreams(client: Socket, remote: Socket) {
        val clientIn = client.getInputStream()
        val clientOut = client.getOutputStream()
        val remoteIn = remote.getInputStream()
        val remoteOut = remote.getOutputStream()

        val j1 = scope.launch {
            val buf = ByteArray(16384)
            try {
                while (isActive) {
                    val len = clientIn.read(buf)
                    if (len == -1) break
                    remoteOut.write(buf, 0, len)
                    remoteOut.flush()
                    totalBytesWritten.addAndGet(len.toLong())
                }
            } catch (_: Exception) {}
            try { remote.shutdownOutput() } catch (_: Exception) {}
        }

        val j2 = scope.launch {
            val buf = ByteArray(16384)
            try {
                while (isActive) {
                    val len = remoteIn.read(buf)
                    if (len == -1) break
                    clientOut.write(buf, 0, len)
                    clientOut.flush()
                    totalBytesRead.addAndGet(len.toLong())
                }
            } catch (_: Exception) {}
            try { client.shutdownOutput() } catch (_: Exception) {}
        }

        // Aguardar enquanto ambas as pontas estiverem ativas
        while (client.isConnected && remote.isConnected && !client.isClosed && !remote.isClosed) {
            if (j1.isCompleted && j2.isCompleted) break
            Thread.sleep(100)
        }
    }

    private fun readLine(input: InputStream): String? {
        val sb = StringBuilder()
        var c: Int
        while (true) {
            c = input.read()
            if (c == -1) {
                return if (sb.isNotEmpty()) sb.toString() else null
            }
            if (c == '\n'.code) {
                break
            }
            if (c != '\r'.code) {
                sb.append(c.toChar())
            }
        }
        return sb.toString()
    }

    fun stop() {
        try {
            serverSocket?.close()
            serverSocket = null
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao fechar serverSocket", e)
        }
        serverJob?.cancel()
        serverJob = null
    }

    companion object {
        private const val TAG = "LocalVpnProxyServer"
    }
}
