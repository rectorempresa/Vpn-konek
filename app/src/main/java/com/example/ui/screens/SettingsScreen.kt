package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AfricellPurple
import com.example.ui.theme.UnitelOrange
import com.example.ui.theme.VpnBlueDark
import com.example.ui.theme.VpnBluePrimary
import com.example.viewmodel.NavigationTab
import com.example.viewmodel.VpnViewModel

@Composable
fun SettingsScreen(
    viewModel: VpnViewModel
) {
    val context = LocalContext.current
    val autoConnect by viewModel.autoConnectOnBoot.collectAsState()
    val killSwitch by viewModel.killSwitchEnabled.collectAsState()
    val isOnline = viewModel.isNetworkConnected(context)

    var requireNetwork by remember { mutableStateOf(true) }
    var unitelBypassEnabled by remember { mutableStateOf(true) }
    var dataCompression by remember { mutableStateOf(true) }
    var bypassLan by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Modern Header with Back Arrow
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.setTab(NavigationTab.INICIO) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar ao Início",
                    tint = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Definições do Túnel",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "Konek VPN Pro • Configurações Avançadas",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Network Status Alert Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOnline) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isOnline) Color(0xFFBBF7D0) else Color(0xFFFECACA)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isOnline) Icons.Default.CheckCircle else Icons.Default.NetworkCheck,
                        contentDescription = "Status",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isOnline) "Rede Ativa: Dados Móveis / Wi-Fi Ligados" else "Atenção: Nenhuma Conexão Ativa",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = if (isOnline) Color(0xFF166534) else Color(0xFF991B1B),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = if (isOnline)
                            "Pronto para conectar com rota direta ou bypass Unitel Angola."
                        else
                            "Ligue os dados móveis da Unitel ou o Wi-Fi para que a VPN possa conectar.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isOnline) Color(0xFF15803D) else Color(0xFFB91C1C),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 1: Redes Angola & Regras de Conexão
        Text(
            text = "REDES ANGOLA & REGRAS DE CONEXÃO",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF64748B),
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Switch: Unitel Bypass Sem Saldo
                SettingToggleRow(
                    icon = Icons.Default.Speed,
                    iconTint = UnitelOrange,
                    title = "Unitel Gerar Internet Sem Saldo",
                    subtitle = "Injeta SNI portal.unitel.ao para navegar mesmo sem prazo ou saldo ativo",
                    checked = unitelBypassEnabled,
                    onCheckedChange = { unitelBypassEnabled = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Switch: Exigir Dados Móveis ou Wi-Fi
                SettingToggleRow(
                    icon = Icons.Default.NetworkCheck,
                    iconTint = VpnBluePrimary,
                    title = "Exigir Dados Móveis ou Wi-Fi Ligados",
                    subtitle = "Impede conexões fantasmas quando a rede móvel estiver desativada",
                    checked = requireNetwork,
                    onCheckedChange = { requireNetwork = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Switch: Kill Switch
                SettingToggleRow(
                    icon = Icons.Default.Shield,
                    iconTint = Color(0xFFEF4444),
                    title = "Bloqueio de Vazamento (Kill Switch)",
                    subtitle = "Corta todo o tráfego se a conexão da VPN for interrompida",
                    checked = killSwitch,
                    onCheckedChange = { viewModel.killSwitchEnabled.value = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Switch: Auto Connect on Boot
                SettingToggleRow(
                    icon = Icons.Default.PowerSettingsNew,
                    iconTint = Color(0xFF10B981),
                    title = "Auto Conectar ao Ligar o Aparelho",
                    subtitle = "Inicia o túnel automaticamente ao reiniciar o smartphone",
                    checked = autoConnect,
                    onCheckedChange = { viewModel.autoConnectOnBoot.value = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Estabilidade e Servidores Anti-Queda
        Text(
            text = "ESTABILIDADE & ANTI-QUEDA",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF64748B),
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Battery Optimization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(VpnBluePrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BatterySaver,
                                contentDescription = "Bateria",
                                tint = VpnBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Modo Ininterrupto (Anti-Queda)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color(0xFF1E293B),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Isenta economia de bateria do Android para servidores nunca caírem",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF64748B)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.requestIgnoreBatteryOptimizations(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = VpnBluePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("verify_battery_button")
                    ) {
                        Text(
                            text = "Garantir",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Switch: Compressão de Dados
                SettingToggleRow(
                    icon = Icons.Default.Tune,
                    iconTint = Color(0xFF6366F1),
                    title = "Compressão de Pacotes (LZO / Snappy)",
                    subtitle = "Aumenta a fluidez de navegação e reduz consumo de bateria",
                    checked = dataCompression,
                    onCheckedChange = { dataCompression = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Switch: Bypass LAN
                SettingToggleRow(
                    icon = Icons.Default.Security,
                    iconTint = Color(0xFF0284C7),
                    title = "Bypass de Rede Local (LAN)",
                    subtitle = "Permite acesso direto a dispositivos locais e impressoras na rede",
                    checked = bypassLan,
                    onCheckedChange = { bypassLan = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Protocolos & Criptografia
        Text(
            text = "PROTOCOLO E CRIPTOGRAFIA DO TÚNEL",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF64748B),
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoParamRow(label = "Protocolo Ativo", value = "WireGuard TLS / SSH Websocket Angola")
                InfoParamRow(label = "Cifra Criptográfica", value = "AES-256-GCM + ChaCha20-Poly1305")
                InfoParamRow(label = "MTU da Interface TUN", value = "1400 (Otimizado 3G/4G/5G)")
                InfoParamRow(label = "Host Bug Unitel", value = "portal.unitel.ao (Porta 443)")
                InfoParamRow(label = "Bypass DNS Angola", value = "197.231.0.12 (Porta 53)")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 4: Diagnóstico e Terminal
        Text(
            text = "DIAGNÓSTICO E LOGS EM TEMPO REAL",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF64748B),
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.setLogsDialogVisible(true) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("open_terminal_logs_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Logs",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Abrir Terminal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            OutlinedButton(
                onClick = { viewModel.clearLogs() },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Limpar",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Limpar Logs",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 5: Developer Badge & Operators Branding
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
            border = androidx.compose.foundation.BorderStroke(1.dp, VpnBluePrimary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.konek_vpn_icon_1784974114210),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Konek VPN Pro • Angola 🇦🇴",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "Versão 3.5.0 Pro Native • Build Especial",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VpnBluePrimary.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Desenvolvido por: Isaac Muaco Programador 🔥",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = VpnBlueDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AfricellPurple.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.africell_logo_badge_1784974128798),
                            contentDescription = "Africell",
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Africell 🟣",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = AfricellPurple
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(UnitelOrange.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.unitel_logo_badge_1784974143756),
                            contentDescription = "Unitel",
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Unitel 🟠",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = UnitelOrange
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VpnBluePrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Composable
private fun InfoParamRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
        )
    }
}
