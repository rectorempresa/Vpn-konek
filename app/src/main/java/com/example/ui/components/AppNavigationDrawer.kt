package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AfricellPurple
import com.example.ui.theme.UnitelOrange
import com.example.ui.theme.VpnBlueDark
import com.example.ui.theme.VpnBlueGradientEnd
import com.example.ui.theme.VpnBlueGradientStart
import com.example.ui.theme.VpnBluePrimary

@Composable
fun SidebarContent(
    isListUpdated: Boolean,
    remainingTimeText: String = "00.00.00.00",
    isNetworkActive: Boolean = true,
    serverName: String = "Australia",
    currentUserName: String = "Visitante",
    onNavigateHome: () -> Unit = {},
    onOpenAccount: () -> Unit = {},
    onUpdateServers: () -> Unit,
    onOpenAdmin: () -> Unit,
    onOpenPayloads: () -> Unit,
    onOpenMuacoFiles: () -> Unit = {},
    onOpenDns: () -> Unit,
    onOpenCoupons: () -> Unit,
    onOpenLogs: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Drawer Header with Logo & Developer Details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.konek_vpn_icon_1784974114210),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(2.dp, VpnBluePrimary.copy(alpha = 0.3f), CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Konek VPN Pro",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Isaac Muaco Programador 🔥",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = VpnBluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            IconButton(
                onClick = onCloseDrawer,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar Menu",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Network & Time Overview Card (Electric Blue Styling)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(VpnBlueGradientStart, VpnBlueGradientEnd)
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isNetworkActive) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = "Rede",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isNetworkActive) "Dados Móveis / Wi-Fi ON" else "Sem Rede Conectada",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isListUpdated) "Online" else "Sync",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Tempo",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tempo Restante:",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = remainingTimeText,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Divider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "RECURSOS & GESTÃO DA VPN",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 1. INÍCIO (TÚNEL PRINCIPAL)
        ModernDrawerItem(
            icon = Icons.Default.Home,
            title = "Início (VPN Principal)",
            subtitle = "Interface e mapa de conexão",
            badgeColor = VpnBluePrimary,
            onClick = {
                onNavigateHome()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. MINHA CONTA (LOGIN / CADASTRO)
        ModernDrawerItem(
            icon = Icons.Default.Person,
            title = "Minha Conta ($currentUserName)",
            subtitle = "Login, cadastro e sincronização",
            badgeColor = Color(0xFF2563EB),
            onClick = {
                onOpenAccount()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. REDES ANGOLA & REDES SOCIAIS (PAYLOADS)
        ModernDrawerItem(
            icon = Icons.Default.NetworkCheck,
            title = "Redes Angola (Payloads)",
            subtitle = "Unitel Sem Saldo / Prazo & Africell",
            badgeColor = UnitelOrange,
            onClick = {
                onOpenPayloads()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ARQUIVOS .MUACO (VPS / SSH / SNI)
        ModernDrawerItem(
            icon = Icons.Default.Description,
            title = "Arquivos .muaco (VPS & SSH)",
            subtitle = "Criar, importar e rodar arquivos .muaco",
            badgeColor = Color(0xFF8B5CF6),
            onClick = {
                onOpenMuacoFiles()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. ADMIN PANEL (ISAAC MUACO)
        ModernDrawerItem(
            icon = Icons.Default.AdminPanelSettings,
            title = "Painel Admin Isaac Muaco",
            subtitle = "Gerar códigos de 13 dígitos & Nuvem",
            badgeColor = Color(0xFF0F172A),
            onClick = {
                onOpenAdmin()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 4. TEMPO & RESGATAR CUPOM
        ModernDrawerItem(
            icon = Icons.Default.ConfirmationNumber,
            title = "Tempo & Resgatar Cupom",
            subtitle = "Inserir códigos de 13 letras",
            badgeColor = Color(0xFF10B981),
            onClick = {
                onOpenCoupons()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 5. DNS SEGURO
        ModernDrawerItem(
            icon = Icons.Default.Dns,
            title = "DNS Seguro e Criptografia",
            subtitle = "Cloudflare, Google, AdGuard",
            badgeColor = Color(0xFF6366F1),
            onClick = {
                onOpenDns()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 6. ATUALIZAR SERVIDORES (8s)
        ModernDrawerItem(
            icon = Icons.Default.Refresh,
            title = "Atualizar Servidores (8s)",
            subtitle = "Sincronizar servidores e payloads",
            badgeColor = Color(0xFFF59E0B),
            onClick = {
                onUpdateServers()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 7. DEFINIÇÕES
        ModernDrawerItem(
            icon = Icons.Default.Settings,
            title = "Definições do Túnel",
            subtitle = "Anti-queda, Kill Switch e rede",
            badgeColor = Color(0xFF0284C7),
            onClick = {
                onOpenSettings()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 8. LOGS DO TERMINAL
        ModernDrawerItem(
            icon = Icons.Default.Terminal,
            title = "Terminal de Logs",
            subtitle = "Visualizar tráfego em tempo real",
            badgeColor = Color(0xFF475569),
            onClick = {
                onOpenLogs()
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Angola Operators Branding
        Text(
            text = "OPERADORAS ANGOLA",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(UnitelOrange.copy(alpha = 0.08f))
                    .border(1.dp, UnitelOrange.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.unitel_logo_badge_1784974143756),
                    contentDescription = "Unitel",
                    modifier = Modifier.size(24.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Unitel 🇦🇴",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Sem Prazo",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = UnitelOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AfricellPurple.copy(alpha = 0.08f))
                    .border(1.dp, AfricellPurple.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.africell_logo_badge_1784974128798),
                    contentDescription = "Africell",
                    modifier = Modifier.size(24.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Africell 🇦🇴",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Congelar",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AfricellPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Desenvolvido com excelência por:\nIsaac Muaco Programador 🔥",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = VpnBlueDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun ModernDrawerItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(badgeColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = badgeColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
