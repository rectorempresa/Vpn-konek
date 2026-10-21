package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed

@Composable
fun ThreeDConnectButton(
    status: ConnectionStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val isConnected = status == ConnectionStatus.CONNECTED
    val isConnecting = status == ConnectionStatus.CONNECTING || status == ConnectionStatus.AUTHENTICATING

    // Infinite pulse transition for connected state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // 3D elevation offsets
    val currentElevation: Dp by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 12.dp,
        animationSpec = tween(100),
        label = "elevation"
    )

    val currentYOffset: Dp by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        animationSpec = tween(100),
        label = "yOffset"
    )

    val primaryGlowColor by animateColorAsState(
        targetValue = when (status) {
            ConnectionStatus.CONNECTED -> NeonGreen
            ConnectionStatus.CONNECTING, ConnectionStatus.AUTHENTICATING -> NeonOrange
            ConnectionStatus.ERROR -> NeonRed
            else -> NeonCyan
        },
        animationSpec = tween(400),
        label = "glowColor"
    )

    val buttonGradient = when (status) {
        ConnectionStatus.CONNECTED -> Brush.verticalGradient(
            colors = listOf(Color(0xFF00E676), Color(0xFF00A050))
        )
        ConnectionStatus.CONNECTING, ConnectionStatus.AUTHENTICATING -> Brush.verticalGradient(
            colors = listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
        )
        ConnectionStatus.ERROR -> Brush.verticalGradient(
            colors = listOf(Color(0xFFFF5252), Color(0xFFC62828))
        )
        else -> Brush.verticalGradient(
            colors = listOf(Color(0xFF00F2FE), Color(0xFF0077FF))
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Active Pulse Wave Background when Connected
        if (isConnected) {
            Box(
                modifier = Modifier
                    .size(195.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(NeonGreen.copy(alpha = pulseAlpha))
            )
            Box(
                modifier = Modifier
                    .size(215.dp)
                    .scale(pulseScale * 1.1f)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = pulseAlpha * 0.5f))
            )
        }

        // 3D Base Shadow Cylinder
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .background(primaryGlowColor.copy(alpha = 0.25f))
                .border(2.dp, primaryGlowColor.copy(alpha = 0.4f), CircleShape)
        )

        // 3D Pressable Button Body
        Box(
            modifier = Modifier
                .offset(y = currentYOffset)
                .size(160.dp)
                .shadow(
                    elevation = currentElevation,
                    shape = CircleShape,
                    ambientColor = primaryGlowColor,
                    spotColor = primaryGlowColor
                )
                .clip(CircleShape)
                .background(buttonGradient)
                .border(
                    width = 4.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.85f),
                            primaryGlowColor,
                            Color.Black.copy(alpha = 0.6f)
                        )
                    ),
                    shape = CircleShape
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                            onClick()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Android Robot Design Badge Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = "Android Engine",
                        tint = if (isConnected) NeonGreen else Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ANDROID VPN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(42.dp),
                        color = Color.White,
                        strokeWidth = 4.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Botão Conectar Android 3D",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (status) {
                        ConnectionStatus.CONNECTED -> "CONECTADO"
                        ConnectionStatus.CONNECTING -> "CONECTANDO..."
                        ConnectionStatus.AUTHENTICATING -> "INJETANDO..."
                        ConnectionStatus.DISCONNECTING -> "PARANDO..."
                        ConnectionStatus.ERROR -> "REAGENDAR"
                        else -> "CONECTAR"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        fontSize = 15.sp
                    )
                )

                if (isConnected) {
                    Text(
                        text = "Toque p/ Desconectar",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
