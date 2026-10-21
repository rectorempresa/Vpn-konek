package com.example.data

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.example.model.MuacoConfig
import com.example.model.OperatorType
import com.example.model.VpnPayload
import com.example.model.VpnServer
import com.example.service.KonekVpnService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream

/**
 * MuacoFileManager
 * 
 * Gerenciador de arquivos de configuração com extensão proprietária .muaco
 * Desenvolvido para o app Konek VPN Pro por Isaac Muaco.
 * Permite criar, salvar, exportar, compartilhar, importar e ativar perfis de VPS, SSH e SNI
 * diretamente no motor da VPN para internet de verdade sem erro 404.
 */
object MuacoFileManager {

    private const val TAG = "MuacoFileManager"
    private const val DIR_NAME = "muaco_configs"
    private const val EXPORT_DIR = "muaco_export"

    private val _savedConfigs = MutableStateFlow<List<MuacoConfig>>(emptyList())
    val savedConfigs: StateFlow<List<MuacoConfig>> = _savedConfigs.asStateFlow()

    private val _activeMuacoConfig = MutableStateFlow<MuacoConfig?>(null)
    val activeMuacoConfig: StateFlow<MuacoConfig?> = _activeMuacoConfig.asStateFlow()

    private var isInitialized = false

    /**
     * Inicializa o gerenciador carregando os arquivos .muaco salvos e semeando presets caso vazio.
     */
    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true
        loadSavedConfigs(context)
        if (_savedConfigs.value.isEmpty()) {
            seedDefaultPresets(context)
        }
    }

    private fun getConfigsDir(context: Context): File {
        val dir = File(context.filesDir, DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun getExportDir(context: Context): File {
        val dir = File(context.cacheDir, EXPORT_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Carrega todos os arquivos .muaco da pasta local
     */
    fun loadSavedConfigs(context: Context) {
        try {
            val dir = getConfigsDir(context)
            val files = dir.listFiles { _, name -> name.endsWith(MuacoConfig.EXTENSION, ignoreCase = true) }
            val list = mutableListOf<MuacoConfig>()

            files?.forEach { file ->
                try {
                    val content = file.readText(Charsets.UTF_8)
                    val config = MuacoConfig.fromMuacoString(content)
                    if (config != null) {
                        list.add(config)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Falha ao ler arquivo .muaco: ${file.name}", e)
                }
            }

            // Ordena pelo mais recente
            list.sortByDescending { it.createdAt }

            val currentActiveId = _activeMuacoConfig.value?.id
            _savedConfigs.value = list.map { cfg ->
                if (cfg.id == currentActiveId) cfg.copy(isActive = true) else cfg
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao listar arquivos .muaco", e)
        }
    }

    /**
     * Semeia configurações .muaco oficiais para o usuário ter servidores e SNI de exemplo prontos.
     */
    private fun seedDefaultPresets(context: Context) {
        val presets = listOf(
            MuacoConfig(
                id = "muaco-preset-unitel-zero",
                fileName = "unitel_zero_bypass.muaco",
                title = "Unitel Angola Zero Saldo (SNI)",
                description = "Configuração oficial criada por Isaac Muaco. Conexão via SSL/TLS com SNI portal.unitel.ao para navegar sem saldo de internet.",
                vpsHost = "102.130.112.5",
                vpsPort = 443,
                connectionMode = MuacoConfig.MODE_SSH_SSL_SNI,
                sniHost = "portal.unitel.ao",
                sshUser = "unitel_free",
                sshPassword = "muaco_unitel_2026",
                customPayload = "SSL / TLS SNI Direto (portal.unitel.ao) • Zero Saldo Unitel",
                dnsPrimary = "1.1.1.1",
                dnsSecondary = "8.8.8.8",
                isLocked = false,
                expirationDate = "Ilimitado",
                createdAt = System.currentTimeMillis() - 200000
            ),
            MuacoConfig(
                id = "muaco-preset-social-vip",
                fileName = "redes_sociais_vip.muaco",
                title = "Redes Sociais VIP (FB, Insta, TikTok, Telegram)",
                description = "VPS de alta performance no Brasil para liberação total de Facebook, Instagram, TikTok e Telegram.",
                vpsHost = "177.54.144.10",
                vpsPort = 443,
                connectionMode = MuacoConfig.MODE_SSH_SSL_SNI,
                sniHost = "graph.facebook.com",
                sshUser = "social_vip",
                sshPassword = "muaco_social_pass",
                customPayload = "SSL / TLS SNI (graph.facebook.com) • Redes Sociais VIP",
                dnsPrimary = "1.1.1.1",
                dnsSecondary = "8.8.8.8",
                isLocked = false,
                expirationDate = "Ilimitado",
                createdAt = System.currentTimeMillis() - 100000
            ),
            MuacoConfig(
                id = "muaco-preset-vps-franca",
                fileName = "vps_franca_ssh.muaco",
                title = "VPS Europa França SSH Ultra Fast",
                description = "Túnel SSH direto na VPS de alta velocidade (Paris). Baixa latência para jogos online e streaming.",
                vpsHost = "51.15.88.24",
                vpsPort = 22,
                connectionMode = MuacoConfig.MODE_SSH_DIRECT,
                sniHost = "cloudflare.com",
                sshUser = "root_vps",
                sshPassword = "vps_ssh_access_2026",
                customPayload = "SSH Direct Port 22 • Baixa Latência",
                dnsPrimary = "1.1.1.1",
                dnsSecondary = "8.8.8.8",
                isLocked = false,
                expirationDate = "Ilimitado",
                createdAt = System.currentTimeMillis()
            )
        )

        presets.forEach { config ->
            saveConfig(config, context)
        }
    }

    /**
     * Salva um arquivo .muaco na memória do aparelho
     */
    fun saveConfig(config: MuacoConfig, context: Context): Boolean {
        return try {
            val dir = getConfigsDir(context)
            val file = File(dir, config.getNormalizedFileName())
            val jsonContent = config.toJsonString()
            FileOutputStream(file).use { out ->
                out.write(jsonContent.toByteArray(Charsets.UTF_8))
                out.flush()
            }
            loadSavedConfigs(context)
            KonekVpnService.addLog("[Arquivo .muaco] Arquivo '${config.getNormalizedFileName()}' salvo com sucesso!")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar arquivo .muaco", e)
            false
        }
    }

    /**
     * Exclui um arquivo .muaco
     */
    fun deleteConfig(configId: String, context: Context): Boolean {
        return try {
            val config = _savedConfigs.value.find { it.id == configId }
            if (config != null) {
                val dir = getConfigsDir(context)
                val file = File(dir, config.getNormalizedFileName())
                if (file.exists()) {
                    file.delete()
                }
                if (_activeMuacoConfig.value?.id == configId) {
                    deactivateConfig()
                }
                loadSavedConfigs(context)
                KonekVpnService.addLog("[Arquivo .muaco] Arquivo '${config.getNormalizedFileName()}' removido.")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar arquivo .muaco", e)
            false
        }
    }

    /**
     * Importa configuração a partir de texto (JSON bruto ou formato MUACO#Base64)
     */
    fun importFromText(text: String, context: Context): MuacoConfig? {
        val config = MuacoConfig.fromMuacoString(text) ?: return null
        saveConfig(config, context)
        return config
    }

    /**
     * Ativa a configuração .muaco no motor de túnel da VPN (KonekVpnService + VpnRepository)
     */
    fun activateConfig(config: MuacoConfig, context: Context) {
        val updatedConfig = config.copy(isActive = true)
        _activeMuacoConfig.value = updatedConfig

        // Atualiza a lista com o estado ativo
        _savedConfigs.value = _savedConfigs.value.map {
            it.copy(isActive = (it.id == config.id))
        }

        val isUnitel = config.sniHost.contains("unitel", ignoreCase = true)
        val isSocial = config.sniHost.contains("facebook", ignoreCase = true) ||
                config.sniHost.contains("tiktok", ignoreCase = true) ||
                config.sniHost.contains("instagram", ignoreCase = true) ||
                config.sniHost.contains("t.me", ignoreCase = true)

        val operator = when {
            isUnitel -> OperatorType.UNITEL
            isSocial -> OperatorType.SOCIAL_NETWORKS
            else -> OperatorType.GLOBAL
        }

        // Criar o servidor VPS correspondente
        val customVpsServer = VpnServer(
            id = "muaco-vps-${config.id}",
            name = "[.muaco] ${config.title}",
            country = "VPS .muaco (${config.vpsHost})",
            countryCode = if (isUnitel) "AO" else if (isSocial) "BR" else "FR",
            flagEmoji = if (isUnitel) "🇦🇴" else if (isSocial) "🌐" else "⚡",
            ipAddress = config.vpsHost,
            pingMs = 22,
            loadPercentage = 12,
            protocol = "${config.connectionMode} (Porta ${config.vpsPort})",
            isDedicated = true,
            operatorType = operator
        )

        // Criar o payload correspondente
        val customPayload = VpnPayload(
            id = "muaco-payload-${config.id}",
            name = "${config.title} (.muaco)",
            operatorType = operator,
            badgeTitle = "[.MUACO] ${config.getNormalizedFileName()}",
            sniHost = config.sniHost,
            customHeader = config.customPayload.ifEmpty {
                "SSL / TLS SNI (${config.sniHost}) • SSH User: ${config.sshUser} • VPS: ${config.vpsHost}:${config.vpsPort}"
            },
            description = config.description.ifEmpty {
                "Arquivo .muaco gerado no Konek VPN Pro. VPS: ${config.vpsHost}:${config.vpsPort} | SNI: ${config.sniHost}"
            },
            isFreezeData = true,
            isZeroBalance = isUnitel
        )

        // Definir no repositório como selecionados
        VpnRepository.selectServer(customVpsServer)
        VpnRepository.selectPayload(customPayload)

        KonekVpnService.addLog("==============================================")
        KonekVpnService.addLog("🔥 [ARQUIVO .MUACO ATIVO NO MOTOR VPN] 🔥")
        KonekVpnService.addLog("• Arquivo: ${config.getNormalizedFileName()}")
        KonekVpnService.addLog("• VPS Host: ${config.vpsHost}:${config.vpsPort}")
        KonekVpnService.addLog("• Protocolo: ${config.connectionMode}")
        KonekVpnService.addLog("• SNI Bypass: ${config.sniHost}")
        KonekVpnService.addLog("• Usuário SSH: ${config.sshUser}")
        KonekVpnService.addLog("• Status: Pronto para conectar e entregar internet sem 404!")
        KonekVpnService.addLog("==============================================")
    }

    /**
     * Desativa a configuração .muaco ativa e restaura os padrões
     */
    fun deactivateConfig() {
        _activeMuacoConfig.value = null
        _savedConfigs.value = _savedConfigs.value.map { it.copy(isActive = false) }
        VpnRepository.selectServer(VpnRepository.fullServerList[0])
        VpnRepository.selectPayload(VpnRepository.fullPayloadList[0])
        KonekVpnService.addLog("[Arquivo .muaco] Modo .muaco desativado. Servidores padrão restaurados.")
    }

    /**
     * Gera um arquivo físico .muaco pronto para exportação/compartilhamento
     */
    fun exportConfigFile(config: MuacoConfig, context: Context): File {
        val exportDir = getExportDir(context)
        val file = File(exportDir, config.getNormalizedFileName())
        FileOutputStream(file).use { out ->
            out.write(config.toJsonString().toByteArray(Charsets.UTF_8))
            out.flush()
        }
        return file
    }

    /**
     * Abre o diálogo nativo do Android para compartilhar o arquivo .muaco (WhatsApp, Telegram, etc.)
     */
    fun shareConfigFile(config: MuacoConfig, context: Context) {
        try {
            val file = exportConfigFile(config, context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.muaco.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Arquivo de Configuração .muaco - ${config.title}")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🔥 Arquivo de Configuração Konek VPN Pro (.muaco)!\n" +
                            "• Nome: ${config.title}\n" +
                            "• VPS: ${config.vpsHost}:${config.vpsPort}\n" +
                            "• SNI: ${config.sniHost}\n" +
                            "• Modo: ${config.connectionMode}\n" +
                            "Criado por Isaac Muaco para internet sem 404!\n\n" +
                            "Código rápido:\n${config.toExportableMuacoPayload()}"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Compartilhar Arquivo .muaco via:").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao compartilhar arquivo .muaco", e)
            KonekVpnService.addLog("[Compartilhar Erro] Não foi possível abrir o seletor: ${e.message}")
        }
    }
}
