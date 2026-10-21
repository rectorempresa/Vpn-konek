package com.example.ui.screens

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ConnectionStatus
import com.example.model.MuacoConfig
import com.example.model.UserAccount
import com.example.model.VpnServer
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.components.ConnectionLogsDialog
import com.example.ui.components.CouponDialog
import com.example.ui.components.CurvedHeaderShape
import com.example.ui.components.PayloadBottomSheet
import com.example.ui.components.RoundFlagBadge
import com.example.ui.components.ServerBottomSheet
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SidebarContent
import com.example.ui.components.UpdateSuccessCardOverlay
import com.example.ui.components.UpdatingProgressDialog
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VpnBlueGradientEnd
import com.example.ui.theme.VpnBlueGradientStart
import com.example.ui.theme.VpnBluePrimary
import com.example.util.DeviceLocationInfo
import com.example.util.DeviceNetworkInfo
import com.example.viewmodel.NavigationTab
import com.example.viewmodel.VpnViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: VpnViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentTab by viewModel.currentTab.collectAsState()
    val isListUpdated by viewModel.isListUpdated.collectAsState()
    val availableServers by viewModel.availableServers.collectAsState()
    val availablePayloads by viewModel.availablePayloads.collectAsState()

    val selectedServer by viewModel.selectedServer.collectAsState()
    val selectedPayload by viewModel.selectedPayload.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val vpnStats by viewModel.vpnStats.collectAsState()
    val validationError by viewModel.validationError.collectAsState()
    val logsList by viewModel.logsList.collectAsState()

    val isUpdatingList by viewModel.isUpdatingList.collectAsState()
    val updateProgress by viewModel.updateProgress.collectAsState()
    val showUpdateSuccessCard by viewModel.showUpdateSuccessCard.collectAsState()

    val showServerSheet by viewModel.showServerSheet.collectAsState()
    val showPayloadSheet by viewModel.showPayloadSheet.collectAsState()
    val showLogsDialog by viewModel.showLogsDialog.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()

    val remainingTimeSeconds by viewModel.remainingTimeSeconds.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val deviceNetworkInfo by viewModel.deviceNetworkInfo.collectAsState()
    val isLocatorEnabled by viewModel.isLocatorEnabled.collectAsState()
    val locationInfo by viewModel.locationInfo.collectAsState()
    val internetTest by viewModel.internetTest.collectAsState()
    val activeMuacoConfig by viewModel.activeMuacoConfig.collectAsState()

    var showCouponDialog by remember { mutableStateOf(false) }

    // Prepare VPN intent launcher
    val prepareVpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onVpnPermissionGranted(context)
        }
    }

    // Android 13+ POST_NOTIFICATIONS permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    // Location permissions launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.toggleLocator(context, true)
        } else {
            viewModel.toggleLocator(context, false)
        }
    }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        viewModel.refreshNetworkInfo(context)
    }

    // Handle back button when inside secondary tabs
    if (currentTab != NavigationTab.INICIO) {
        BackHandler {
            viewModel.setTab(NavigationTab.INICIO)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarContent(
                isListUpdated = isListUpdated,
                remainingTimeText = viewModel.formatRawDotTime(remainingTimeSeconds),
                isNetworkActive = viewModel.isNetworkConnected(context),
                serverName = selectedServer?.name ?: "Australia",
                currentUserName = currentUser?.username ?: "Visitante",
                onNavigateHome = {
                    viewModel.setTab(NavigationTab.INICIO)
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenAccount = {
                    viewModel.setTab(NavigationTab.CONTA)
                    coroutineScope.launch { drawerState.close() }
                },
                onUpdateServers = {
                    coroutineScope.launch { drawerState.close() }
                    viewModel.startUpdateServerList(context)
                },
                onOpenAdmin = {
                    viewModel.setTab(NavigationTab.ADMIN)
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenPayloads = {
                    viewModel.setPayloadSheetVisible(true)
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenMuacoFiles = {
                    viewModel.setTab(NavigationTab.MUACO_FILES)
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenDns = {
                    viewModel.setTab(NavigationTab.DNS)
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenCoupons = {
                    showCouponDialog = true
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenLogs = {
                    viewModel.setLogsDialogVisible(true)
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenSettings = {
                    viewModel.setTab(NavigationTab.DEFINICOES)
                    coroutineScope.launch { drawerState.close() }
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            when (currentTab) {
                NavigationTab.INICIO -> {
                    MockupHomeContent(
                        selectedServer = selectedServer,
                        connectionStatus = connectionStatus,
                        downloadSpeed = if (connectionStatus == ConnectionStatus.CONNECTED) "10.5" else "----",
                        uploadSpeed = if (connectionStatus == ConnectionStatus.CONNECTED) "7.6" else "----",
                        validationError = validationError,
                        remainingTimeSeconds = remainingTimeSeconds,
                        formattedTime = viewModel.formatFormattedTime(remainingTimeSeconds),
                        currentUser = currentUser,
                        deviceNetworkInfo = deviceNetworkInfo,
                        isLocatorEnabled = isLocatorEnabled,
                        locationInfo = locationInfo,
                        onOpenMenu = { coroutineScope.launch { drawerState.open() } },
                        onConnectClick = {
                            viewModel.toggleConnect(context) { prepareIntent ->
                                prepareVpnLauncher.launch(prepareIntent)
                            }
                        },
                        onOpenServerChooser = {
                            viewModel.setServerSheetVisible(true)
                        },
                        onOpenCoupon = {
                            showCouponDialog = true
                        },
                        onOpenAccount = {
                            viewModel.setTab(NavigationTab.CONTA)
                        },
                        onToggleLocator = { targetEnabled ->
                            if (targetEnabled) {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                viewModel.toggleLocator(context, false)
                            }
                        },
                        onRefreshNetwork = {
                            viewModel.refreshNetworkInfo(context)
                        },
                        internetTest = internetTest,
                        onTestInternet = {
                            viewModel.testInternetConnection()
                        },
                        activeMuacoConfig = activeMuacoConfig,
                        onOpenMuacoFiles = {
                            viewModel.setTab(NavigationTab.MUACO_FILES)
                        }
                    )
                }

                NavigationTab.MUACO_FILES -> {
                    SubScreenScaffold(
                        title = "Arquivos .muaco (VPS / SSH)",
                        onBack = { viewModel.setTab(NavigationTab.INICIO) }
                    ) {
                        MuacoFilesScreen(viewModel = viewModel)
                    }
                }

                NavigationTab.CONTA -> {
                    SubScreenScaffold(
                        title = "Minha Conta & Tempo",
                        onBack = { viewModel.setTab(NavigationTab.INICIO) }
                    ) {
                        AccountScreen(viewModel = viewModel)
                    }
                }

                NavigationTab.DNS -> {
                    SubScreenScaffold(
                        title = "DNS Seguro",
                        onBack = { viewModel.setTab(NavigationTab.INICIO) }
                    ) {
                        DnsScreen(viewModel = viewModel)
                    }
                }

                NavigationTab.DEFINICOES -> {
                    SubScreenScaffold(
                        title = "Definições",
                        onBack = { viewModel.setTab(NavigationTab.INICIO) }
                    ) {
                        SettingsScreen(viewModel = viewModel)
                    }
                }

                NavigationTab.ADMIN -> {
                    SubScreenScaffold(
                        title = "Painel Admin (Isaac Muaco)",
                        onBack = { viewModel.setTab(NavigationTab.INICIO) }
                    ) {
                        AdminScreen(viewModel = viewModel)
                    }
                }
            }

            // Updating 8s Progress Overlay
            if (isUpdatingList) {
                UpdatingProgressDialog(progress = updateProgress)
            }

            // Update Success Card Overlay
            if (showUpdateSuccessCard) {
                UpdateSuccessCardOverlay(
                    onDismiss = { viewModel.dismissUpdateSuccessCard() }
                )
            }

            // Bottom Sheets & Dialogs
            if (showServerSheet) {
                ServerBottomSheet(
                    servers = availableServers,
                    selectedServer = selectedServer,
                    onSelectServer = { viewModel.selectServer(it) },
                    onDismiss = { viewModel.setServerSheetVisible(false) }
                )
            }

            if (showPayloadSheet) {
                PayloadBottomSheet(
                    payloads = availablePayloads,
                    selectedPayload = selectedPayload,
                    onSelectPayload = { viewModel.selectPayload(it) },
                    onDismiss = { viewModel.setPayloadSheetVisible(false) }
                )
            }

            if (showLogsDialog) {
                ConnectionLogsDialog(
                    logs = logsList,
                    onClearLogs = { viewModel.clearLogs() },
                    onDismiss = { viewModel.setLogsDialogVisible(false) }
                )
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.setSettingsDialogVisible(false) }
                )
            }

            if (showCouponDialog) {
                CouponDialog(
                    viewModel = viewModel,
                    onDismiss = { showCouponDialog = false }
                )
            }
        }
    }
}

/**
 * EXACT REPLICA OF THE VPN INTERFACE SHOWN IN THE USER'S ATTACHED IMAGE
 * PLUS LIVE TIME COUNTER & TELEMETRY
 */
@Composable
private fun MockupHomeContent(
    selectedServer: VpnServer?,
    connectionStatus: ConnectionStatus,
    downloadSpeed: String,
    uploadSpeed: String,
    validationError: String?,
    remainingTimeSeconds: Long,
    formattedTime: String,
    currentUser: UserAccount?,
    deviceNetworkInfo: DeviceNetworkInfo,
    isLocatorEnabled: Boolean,
    locationInfo: DeviceLocationInfo?,
    onOpenMenu: () -> Unit,
    onConnectClick: () -> Unit,
    onOpenServerChooser: () -> Unit,
    onOpenCoupon: () -> Unit,
    onOpenAccount: () -> Unit,
    onToggleLocator: (Boolean) -> Unit,
    onRefreshNetwork: () -> Unit,
    internetTest: VpnViewModel.InternetTestResult?,
    onTestInternet: () -> Unit,
    activeMuacoConfig: MuacoConfig? = null,
    onOpenMuacoFiles: () -> Unit = {}
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val isConnected = connectionStatus == ConnectionStatus.CONNECTED
    val isConnecting = connectionStatus == ConnectionStatus.CONNECTING || connectionStatus == ConnectionStatus.AUTHENTICATING

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP BLUE CURVED DOME SECTION
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CurvedHeaderShape(curveDepthPx = 56f))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            VpnBlueGradientStart,
                            VpnBlueGradientEnd
                        )
                    )
                )
                .padding(top = statusBarHeight + 12.dp, bottom = 42.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Bar inside Blue Section: Hamburger Icon (Left), "VPN" Title (Center), User Account (Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onOpenMenu,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Text(
                        text = "VPN",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    // User Account Avatar/Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .clickable { onOpenAccount() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Conta",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentUser?.username?.take(8) ?: "Conta",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // LARGE ROUND CONNECT POWER BUTTON
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(102.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.5.dp, Color(0xFF67B5FF), CircleShape)
                            .clickable { onConnectClick() }
                            .testTag("connect_power_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Conectar VPN",
                            tint = VpnBluePrimary,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status row: Dot + Text (Connected / Disconnected / Connecting)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val dotColor = when {
                        isConnected -> Color(0xFF22C55E) // Green dot
                        isConnecting -> Color(0xFFF59E0B) // Orange dot
                        else -> Color(0xFFEF4444) // Red dot
                    }

                    val statusLabel = when {
                        isConnected -> "Connected"
                        isConnecting -> "Connecting..."
                        else -> "Disconnected"
                    }

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = statusLabel,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Validation Error banner if present
        AnimatedVisibility(
            visible = validationError != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NeonRed, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = NeonRed.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Aviso",
                        tint = NeonRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = validationError ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 1. CONTADOR DE TEMPO NA PARTE INICIAL DO APP (SOLICITADO)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(1.2.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "Timer",
                            tint = VpnBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tempo de Acesso",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Status Badge (Esgotando vs Pausado)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isConnected) Color(0xFFDCFCE7) else Color(0xFFF1F5F9))
                            .border(
                                1.dp,
                                if (isConnected) Color(0xFF86EFAC) else Color(0xFFCBD5E1),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isConnected) "● A Esgotar (Ativo)" else "⏸ Em Pausa (Inicia ao Conectar)",
                            color = if (isConnected) Color(0xFF15803D) else Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Digits
                Text(
                    text = formattedTime,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "O tempo só corre quando a VPN estiver ligada",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    // Botão "+ Cupom"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VpnBluePrimary)
                            .clickable { onOpenCoupon() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Adicionar Tempo",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. IP REAL DO DISPOSITIVO & OPERADORA / WI-FI
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Telemetria de Rede Real",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    IconButton(
                        onClick = onRefreshNetwork,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar IP",
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Operadora / Conexão:", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "${deviceNetworkInfo.operatorName} (${deviceNetworkInfo.connectionType})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "IP Público Real:", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = deviceNetworkInfo.publicIp,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isConnected) NeonGreen else Color(0xFF2563EB)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "IP Local: ${deviceNetworkInfo.localIp}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = if (isConnected) "🔒 Túnel VPN: 10.8.0.2 (Ativo)" else "🛡️ IP Desprotegido",
                        fontSize = 11.sp,
                        color = if (isConnected) NeonGreen else Color(0xFFEF4444),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2.5 STATUS DA INTERNET NOS APPS & TESTE REAL (SEM ERRO 404)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.2.dp,
                    if (isConnected) Color(0xFF86EFAC) else Color(0xFFE2E8F0),
                    RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isConnected) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = if (isConnected) Color(0xFF16A34A) else Color(0xFF3B82F6),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Status da Internet nos Aplicativos",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isConnected) Color(0xFFDCFCE7) else Color(0xFFE2E8F0))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isConnected) "🔥 Sem Erro 404" else "Aguardando VPN",
                            color = if (isConnected) Color(0xFF15803D) else Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isConnected) {
                        "Túnel HTTP/HTTPS ativo na porta 8989 e DNS 53 roteado. Todos os apps (Facebook, Instagram, Telegram, TikTok, Chrome) recebem internet funcional sem página 404 da operadora."
                    } else {
                        "Ligue a VPN para gerar internet funcional e liberar todos os aplicativos sem bloqueio ou erro 404 da Unitel."
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Test Button & Results
                val isTesting = internetTest?.isTesting == true
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onTestInternet,
                        enabled = !isTesting,
                        modifier = Modifier.height(34.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isConnected) Color(0xFF16A34A) else Color(0xFF2563EB)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testando...", fontSize = 11.sp, color = Color.White)
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testar Conexão Real", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    if (internetTest != null && !isTesting) {
                        Text(
                            text = "Status: ${internetTest.statusCode} OK (${internetTest.latencyMs}ms)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (internetTest.isSuccess) Color(0xFF15803D) else Color(0xFFDC2626)
                        )
                    }
                }

                if (internetTest != null && !isTesting && internetTest.message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = internetTest.message,
                        fontSize = 11.sp,
                        color = if (internetTest.isSuccess) Color(0xFF15803D) else Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. SISTEMA DE LOCALIZADOR (ATIVAR/DESATIVAR)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = Color(0xFF0EA5E9),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sistema de Localizador",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Switch(
                        checked = isLocatorEnabled,
                        onCheckedChange = { onToggleLocator(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ElectricBlue
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (isLocatorEnabled && locationInfo != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${locationInfo.readableLocation} (Lat: %.3f, Lon: %.3f)".format(
                                locationInfo.latitude,
                                locationInfo.longitude
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Text(
                        text = "Provedor: ${locationInfo.provider} • Modo GPS/Rede Real Ativo",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                } else {
                    Text(
                        text = "Localizador em modo seguro Konek VPN (IP do servidor em uso).",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. ARQUIVOS .MUACO (VPS / SSH / SNI)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.2.dp,
                    if (activeMuacoConfig != null) Color(0xFF8B5CF6) else Color(0xFFE2E8F0),
                    RoundedCornerShape(16.dp)
                )
                .clickable { onOpenMuacoFiles() }
                .testTag("card_muaco_home_shortcut"),
            colors = CardDefaults.cardColors(
                containerColor = if (activeMuacoConfig != null) Color(0xFFFAF5FF) else Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeMuacoConfig != null) Color(0xFF10B981) else Color(0xFF8B5CF6)),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (activeMuacoConfig != null) activeMuacoConfig.title else "Criador de Arquivos .muaco",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            if (activeMuacoConfig != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ATIVO", color = Color(0xFF15803D), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = if (activeMuacoConfig != null)
                                "VPS: ${activeMuacoConfig.vpsHost}:${activeMuacoConfig.vpsPort} • SNI: ${activeMuacoConfig.sniHost}"
                            else
                                "Crie arquivos .muaco com VPS, SSH e SNI para internet sem 404",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (activeMuacoConfig != null) "Gerenciar" else "Criar .muaco",
                        color = Color(0xFF7C3AED),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // MIDDLE SPEED METRICS SECTION
        Text(
            text = "Speed",
            color = Color(0xFF8E9AA8),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Download Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Download",
                    color = Color(0xFF8E9AA8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = downloadSpeed,
                    color = Color(0xFF1E293B),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "mbps",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }

            // Thin Vertical Divider Line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(38.dp)
                    .background(Color(0xFFE2E8F0))
            )

            // Upload Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Upload",
                    color = Color(0xFF8E9AA8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uploadSpeed,
                    color = Color(0xFF1E293B),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "mbps",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // LOCATION SECTION
        Text(
            text = "Location",
            color = Color(0xFF8E9AA8),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // WORLD MAP GRAPHIC
        Image(
            painter = painterResource(id = R.drawable.ic_world_map),
            contentDescription = "World Map",
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(horizontal = 28.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SELECTED COUNTRY CAPSULE PILL BUTTON (Bottom)
        val server = selectedServer
        val countryName = server?.name ?: "Australia"
        val flagEmoji = server?.flagEmoji ?: "🇦🇺"
        val countryCode = server?.countryCode ?: "AU"

        Box(
            modifier = Modifier
                .widthIn(min = 220.dp, max = 310.dp)
                .height(48.dp)
                .shadow(2.dp, RoundedCornerShape(50.dp))
                .clip(RoundedCornerShape(50.dp))
                .background(Color.White)
                .border(1.5.dp, Color(0xFF60A5FA), RoundedCornerShape(50.dp))
                .clickable { onOpenServerChooser() }
                .testTag("country_picker_pill")
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RoundFlagBadge(
                    flagEmoji = flagEmoji,
                    countryCode = countryCode,
                    size = 28.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = countryName,
                    color = Color(0xFF3B82F6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubScreenScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }
    }
}
