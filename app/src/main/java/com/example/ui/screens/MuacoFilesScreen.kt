package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.model.MuacoConfig
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.UnitelOrange
import com.example.ui.theme.VpnBlueDark
import com.example.ui.theme.VpnBluePrimary
import com.example.viewmodel.NavigationTab
import com.example.viewmodel.VpnViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MuacoFilesScreen(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val savedConfigs by viewModel.savedMuacoConfigs.collectAsState()
    val activeConfig by viewModel.activeMuacoConfig.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Meus Arquivos (${savedConfigs.size})", "Criar Novo .muaco")

    // State for Editor Tab
    var editingConfigId by remember { mutableStateOf<String?>(null) }
    var fileNameInput by remember { mutableStateOf("") }
    var titleInput by remember { mutableStateOf("") }
    var vpsHostInput by remember { mutableStateOf("102.130.112.5") }
    var vpsPortInput by remember { mutableStateOf("443") }
    var connectionModeInput by remember { mutableStateOf(MuacoConfig.MODE_SSH_SSL_SNI) }
    var sniHostInput by remember { mutableStateOf("portal.unitel.ao") }
    var sshUserInput by remember { mutableStateOf("muaco_user") }
    var sshPasswordInput by remember { mutableStateOf("muaco2026") }
    var customPayloadInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("Criado por Isaac Muaco para internet real sem 404.") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLockedInput by remember { mutableStateOf(false) }

    // Dialog state for Import
    var showImportDialog by remember { mutableStateOf(false) }
    var importTextInput by remember { mutableStateOf("") }
    var deleteCandidateId by remember { mutableStateOf<String?>(null) }

    fun populateEditor(config: MuacoConfig) {
        editingConfigId = config.id
        fileNameInput = config.fileName.removeSuffix(MuacoConfig.EXTENSION)
        titleInput = config.title
        vpsHostInput = config.vpsHost
        vpsPortInput = config.vpsPort.toString()
        connectionModeInput = config.connectionMode
        sniHostInput = config.sniHost
        sshUserInput = config.sshUser
        sshPasswordInput = config.sshPassword
        customPayloadInput = config.customPayload
        descriptionInput = config.description
        isLockedInput = config.isLocked
        selectedTabIndex = 1
    }

    fun clearEditor() {
        editingConfigId = null
        fileNameInput = ""
        titleInput = ""
        vpsHostInput = "102.130.112.5"
        vpsPortInput = "443"
        connectionModeInput = MuacoConfig.MODE_SSH_SSL_SNI
        sniHostInput = "portal.unitel.ao"
        sshUserInput = "muaco_user"
        sshPasswordInput = "muaco2026"
        customPayloadInput = ""
        descriptionInput = "Criado por Isaac Muaco para internet sem 404."
        isLockedInput = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF8B5CF6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ".muaco",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Arquivos .muaco",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Criador de VPS, SSH & SNI",
                                color = Color(0xFFA78BFA),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Import Button
                    OutlinedButton(
                        onClick = { showImportDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF64748B)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("btn_import_muaco_top")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Importar", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Arquivos .muaco são a extensão proprietária do Konek VPN Pro para empacotar credenciais de VPS, túneis SSH e SNI de operadoras para internet de verdade sem erro 404.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        // Tab Selector
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = VpnBluePrimary,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        if (index == 1 && editingConfigId == null && titleInput.isEmpty()) {
                            clearEditor()
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        // TAB CONTENT
        if (selectedTabIndex == 0) {
            // TAB 1: LIST OF MUACO FILES
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Active Config Banner (if any)
                if (activeConfig != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0F172A)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGreen)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(NeonGreen)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "CONFIGURAÇÃO .MUACO ATIVA",
                                            color = NeonGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    Text(
                                        text = if (connectionStatus == ConnectionStatus.CONNECTED) "CONECTADO" else "PRONTO",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = activeConfig!!.title,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Arquivo: ${activeConfig!!.getNormalizedFileName()}",
                                    color = Color(0xFFA78BFA),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("VPS Host", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                        Text("${activeConfig!!.vpsHost}:${activeConfig!!.vpsPort}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column {
                                        Text("SNI Bypass", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                        Text(activeConfig!!.sniHost, color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column {
                                        Text("Usuário SSH", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                        Text(activeConfig!!.sshUser, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.deactivateMuacoConfig()
                                            Toast.makeText(context, "Modo .muaco desativado. Padrão restaurado.", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFFEF4444)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Desativar .muaco", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Action Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ARQUIVOS SALVOS NO DISPOSITIVO",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        TextButton(
                            onClick = {
                                clearEditor()
                                selectedTabIndex = 1
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = VpnBluePrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Criar Novo", color = VpnBluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (savedConfigs.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Nenhum arquivo .muaco encontrado",
                                    color = Color(0xFF1E293B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Crie seu primeiro arquivo com VPS, SSH e SNI para Unitel ou redes sociais.",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        clearEditor()
                                        selectedTabIndex = 1
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VpnBluePrimary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Criar Primeiro Arquivo .muaco")
                                }
                            }
                        }
                    }
                } else {
                    items(savedConfigs, key = { it.id }) { config ->
                        MuacoConfigCard(
                            config = config,
                            isActive = (activeConfig?.id == config.id),
                            onActivate = {
                                viewModel.activateMuacoConfig(config, context)
                                Toast.makeText(context, "🔥 Configuração ${config.title} ativada no motor VPN!", Toast.LENGTH_SHORT).show()
                            },
                            onShare = {
                                viewModel.shareMuacoConfig(config, context)
                            },
                            onCopyPayload = {
                                clipboardManager.setText(AnnotatedString(config.toExportableMuacoPayload()))
                                Toast.makeText(context, "Código .muaco copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
                            },
                            onEdit = {
                                populateEditor(config)
                            },
                            onDelete = {
                                deleteCandidateId = config.id
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        } else {
            // TAB 2: CREATE / EDIT MUACO CONFIG FORM
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                item {
                    if (editingConfigId != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "✏️ Editando arquivo existente",
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            TextButton(onClick = { clearEditor() }) {
                                Text("Cancelar Edição", color = Color(0xFFB45309), fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Card 1: Identificação do Arquivo
                    SectionCard(title = "1. Nome & Identificação do Arquivo .muaco") {
                        OutlinedTextField(
                            value = fileNameInput,
                            onValueChange = { fileNameInput = it },
                            label = { Text("Nome do Arquivo (sem extensão)") },
                            placeholder = { Text("unitel_zero_gratis") },
                            trailingIcon = {
                                Text(
                                    text = ".muaco",
                                    color = Color(0xFF8B5CF6),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_muaco_filename"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VpnBluePrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Título da Configuração") },
                            placeholder = { Text("Unitel Zero Data VIP 2026") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_muaco_title"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = descriptionInput,
                            onValueChange = { descriptionInput = it },
                            label = { Text("Mensagem / Créditos do Criador") },
                            placeholder = { Text("Configuração criada por Isaac Muaco") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Card 2: VPS (Servidor Privado)
                    SectionCard(title = "2. VPS (Servidor Remoto Privado)") {
                        OutlinedTextField(
                            value = vpsHostInput,
                            onValueChange = { vpsHostInput = it },
                            label = { Text("Host do VPS (IP ou Domínio)") },
                            placeholder = { Text("102.130.112.5 ou vps.konek.ao") },
                            leadingIcon = {
                                Icon(Icons.Default.Storage, contentDescription = null, tint = VpnBluePrimary)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_muaco_vps_host"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = vpsPortInput,
                            onValueChange = { vpsPortInput = it.filter { char -> char.isDigit() } },
                            label = { Text("Porta do Servidor / VPS") },
                            placeholder = { Text("443") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_muaco_vps_port"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Portas comuns:", color = Color(0xFF64748B), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("443", "22", "80", "8080", "8443", "3128").forEach { port ->
                                QuickChip(
                                    label = port,
                                    isSelected = vpsPortInput == port,
                                    onClick = { vpsPortInput = port }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Card 3: Protocolo & SNI
                    SectionCard(title = "3. Protocolo & SNI (Bypass de Operadora)") {
                        Text("Modo de Conexão:", color = Color(0xFF475569), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            MuacoConfig.ALL_MODES.forEach { mode ->
                                QuickChip(
                                    label = mode,
                                    isSelected = connectionModeInput == mode,
                                    onClick = { connectionModeInput = mode }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = sniHostInput,
                            onValueChange = { sniHostInput = it },
                            label = { Text("SNI (Server Name Indication)") },
                            placeholder = { Text("portal.unitel.ao") },
                            leadingIcon = {
                                Icon(Icons.Default.Dns, contentDescription = null, tint = Color(0xFF8B5CF6))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_muaco_sni"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sugestões de SNI para Angola & Redes Sociais:", color = Color(0xFF64748B), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val sniPresets = listOf(
                                "portal.unitel.ao" to "🇦🇴 Unitel Zero",
                                "internetgratis.unitel.ao" to "🇦🇴 Unitel WS",
                                "graph.facebook.com" to "🌐 Facebook Free",
                                "instagram.com" to "📸 Instagram VIP",
                                "api.tiktokv.com" to "🎵 TikTok Free",
                                "t.me" to "💬 Telegram",
                                "cloudflare.com" to "☁️ Cloudflare"
                            )
                            sniPresets.forEach { (host, label) ->
                                QuickChip(
                                    label = label,
                                    isSelected = sniHostInput == host,
                                    onClick = { sniHostInput = host }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Card 4: SSH Authentication
                    SectionCard(title = "4. Autenticação SSH") {
                        OutlinedTextField(
                            value = sshUserInput,
                            onValueChange = { sshUserInput = it },
                            label = { Text("Usuário SSH") },
                            placeholder = { Text("muaco_user") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_muaco_ssh_user"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = sshPasswordInput,
                            onValueChange = { sshPasswordInput = it },
                            label = { Text("Senha SSH") },
                            placeholder = { Text("Senha do VPS") },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Ver Senha"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_muaco_ssh_pass"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Card 5: Custom Payload & Headers
                    SectionCard(title = "5. Payload & Injeção de Cabeçalhos (Opcional)") {
                        OutlinedTextField(
                            value = customPayloadInput,
                            onValueChange = { customPayloadInput = it },
                            label = { Text("Payload Customizado") },
                            placeholder = { Text("SSL / TLS Direct SNI ou GET / HTTP/1.1...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_muaco_payload"),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                customPayloadInput = "SSL / TLS SNI Direto ($sniHostInput) • Host: $sniHostInput • SSH: $sshUserInput"
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = VpnBluePrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gerar Payload Automático Anti-404")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Card 6: Lock & Protection
                    SectionCard(title = "6. Proteção do Arquivo") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Bloquear Edição do Arquivo", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Protege seus dados de VPS e SSH contra visualização ao compartilhar.", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                            Switch(
                                checked = isLockedInput,
                                onCheckedChange = { isLockedInput = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VpnBluePrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ACTION BUTTONS IN FOOTER
                    val saveAction: (Boolean) -> Unit = { shouldActivate ->
                        val cleanFileName = if (fileNameInput.isNotBlank()) fileNameInput.trim() else "config_${System.currentTimeMillis() % 10000}"
                        val cleanTitle = if (titleInput.isNotBlank()) titleInput.trim() else "VPS SNI $sniHostInput"
                        val port = vpsPortInput.toIntOrNull() ?: 443

                        val newConfig = MuacoConfig(
                            id = editingConfigId ?: java.util.UUID.randomUUID().toString(),
                            fileName = cleanFileName,
                            title = cleanTitle,
                            description = descriptionInput,
                            vpsHost = vpsHostInput.ifEmpty { "102.130.112.5" },
                            vpsPort = port,
                            connectionMode = connectionModeInput,
                            sniHost = sniHostInput.ifEmpty { "portal.unitel.ao" },
                            sshUser = sshUserInput.ifEmpty { "muaco_user" },
                            sshPassword = sshPasswordInput,
                            customPayload = customPayloadInput,
                            isLocked = isLockedInput,
                            createdAt = System.currentTimeMillis()
                        )

                        val saved = viewModel.saveMuacoConfig(newConfig, context)
                        if (saved) {
                            if (shouldActivate) {
                                viewModel.activateMuacoConfig(newConfig, context)
                                Toast.makeText(context, "🔥 Arquivo .muaco salvo e ATIVADO no app!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Arquivo ${newConfig.getNormalizedFileName()} salvo com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                            clearEditor()
                            selectedTabIndex = 0
                        } else {
                            Toast.makeText(context, "Erro ao salvar arquivo .muaco", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Button 1: Salvar & Ativar Agora
                    Button(
                        onClick = { saveAction(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_save_activate_muaco"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VpnBluePrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "⚡ Salvar & Ativar Agora no App",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button 2: Salvar e Compartilhar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { saveAction(false) },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Apenas Salvar", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                val cleanFileName = if (fileNameInput.isNotBlank()) fileNameInput.trim() else "config_${System.currentTimeMillis() % 10000}"
                                val cleanTitle = if (titleInput.isNotBlank()) titleInput.trim() else "VPS SNI $sniHostInput"
                                val port = vpsPortInput.toIntOrNull() ?: 443

                                val cfg = MuacoConfig(
                                    id = editingConfigId ?: java.util.UUID.randomUUID().toString(),
                                    fileName = cleanFileName,
                                    title = cleanTitle,
                                    description = descriptionInput,
                                    vpsHost = vpsHostInput.ifEmpty { "102.130.112.5" },
                                    vpsPort = port,
                                    connectionMode = connectionModeInput,
                                    sniHost = sniHostInput.ifEmpty { "portal.unitel.ao" },
                                    sshUser = sshUserInput.ifEmpty { "muaco_user" },
                                    sshPassword = sshPasswordInput,
                                    customPayload = customPayloadInput,
                                    isLocked = isLockedInput,
                                    createdAt = System.currentTimeMillis()
                                )
                                viewModel.saveMuacoConfig(cfg, context)
                                viewModel.shareMuacoConfig(cfg, context)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Exportar .muaco", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // DIALOG: IMPORT MUACO CONFIG
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color(0xFF8B5CF6))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importar Arquivo .muaco", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Cole abaixo o código de configuração .muaco (JSON ou código Base64 iniciado por MUACO#):",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = importTextInput,
                        onValueChange = { importTextInput = it },
                        placeholder = { Text("MUACO#eyJhcHAiOiJLb25layBWUE4...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("input_import_muaco_text"),
                        maxLines = 8
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importTextInput.isNotBlank()) {
                            val imported = viewModel.importMuacoText(importTextInput, context)
                            if (imported != null) {
                                Toast.makeText(context, "✅ Arquivo ${imported.getNormalizedFileName()} importado com sucesso!", Toast.LENGTH_LONG).show()
                                showImportDialog = false
                                importTextInput = ""
                            } else {
                                Toast.makeText(context, "Formato .muaco inválido. Verifique o código copiado.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Text("Importar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG: DELETE CONFIRMATION
    if (deleteCandidateId != null) {
        AlertDialog(
            onDismissRequest = { deleteCandidateId = null },
            title = { Text("Excluir Arquivo .muaco?", fontWeight = FontWeight.Bold) },
            text = { Text("Tem certeza que deseja apagar este arquivo de configuração do seu dispositivo?") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteCandidateId?.let { id ->
                            viewModel.deleteMuacoConfig(id, context)
                            Toast.makeText(context, "Arquivo excluído", Toast.LENGTH_SHORT).show()
                        }
                        deleteCandidateId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidateId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = Color(0xFF1E293B),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun QuickChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) VpnBluePrimary else Color(0xFFF1F5F9))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color(0xFF334155),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun MuacoConfigCard(
    config: MuacoConfig,
    isActive: Boolean,
    onActivate: () -> Unit,
    onShare: () -> Unit,
    onCopyPayload: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("muaco_item_${config.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isActive) 2.dp else 1.dp,
            color = if (isActive) NeonGreen else Color(0xFFE2E8F0)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) Color(0xFF10B981) else Color(0xFF8B5CF6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ".muaco",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = config.title,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = config.getNormalizedFileName(),
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                if (isActive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🔥 ATIVO NO APP",
                            color = Color(0xFF15803D),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("VPS: ${config.vpsHost}:${config.vpsPort}", color = Color(0xFF334155), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("SSH: ${config.sshUser}", color = Color(0xFF475569), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("SNI: ${config.sniHost}", color = Color(0xFF0284C7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Modo: ${config.connectionMode}", color = Color(0xFF64748B), fontSize = 11.sp)
                    }

                    if (config.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📝 ${config.description}",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Compartilhar", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onCopyPayload, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Código", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }

                Button(
                    onClick = onActivate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) Color(0xFF10B981) else VpnBluePrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isActive) "Reativar" else "Ativar no App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
