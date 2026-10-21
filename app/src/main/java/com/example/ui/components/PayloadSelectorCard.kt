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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.OperatorType
import com.example.model.VpnPayload
import com.example.ui.theme.AfricellPurple
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UnitelOrange

@Composable
fun PayloadSelectorCard(
    payload: VpnPayload?,
    isUpdated: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when (payload?.operatorType) {
        OperatorType.AFRICELL -> AfricellPurple
        OperatorType.UNITEL -> UnitelOrange
        else -> NeonPurple
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = if (payload != null && isUpdated) Brush.horizontalGradient(
                    colors = listOf(borderColor, CyberBorder)
                ) else Brush.linearGradient(colors = listOf(CyberBorder, CyberBorder)),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Operator Icon / Badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (!isUpdated) NeonOrange.copy(alpha = 0.2f)
                            else when (payload?.operatorType) {
                                OperatorType.AFRICELL -> AfricellPurple
                                OperatorType.UNITEL -> UnitelOrange
                                else -> CyberSurfaceVariant
                            }
                        )
                        .border(1.dp, if (!isUpdated) NeonOrange else borderColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isUpdated) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Indisponível",
                            tint = NeonOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    } else when (payload?.operatorType) {
                        OperatorType.AFRICELL -> Image(
                            painter = painterResource(id = R.drawable.africell_logo_badge_1784974128798),
                            contentDescription = "Africell Icon",
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                        )
                        OperatorType.UNITEL -> Image(
                            painter = painterResource(id = R.drawable.unitel_logo_badge_1784974143756),
                            contentDescription = "Unitel Icon",
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                        )
                        else -> Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Payload Icon",
                            tint = NeonPurple,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "2. REDE / TWEAK PAYLOAD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isUpdated) NeonPurple else NeonOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (!isUpdated) "Indisponível (Atualize a lista no Menu)"
                        else payload?.name ?: "Toque para escolher a Rede (Africell, Unitel)",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isUpdated && payload != null) TextPrimary else TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    if (isUpdated && payload != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = payload.badgeTitle + " • SNI: " + payload.sniHost,
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    } else if (!isUpdated) {
                        Text(
                            text = "Requer internet para baixar payloads",
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonOrange)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (!isUpdated) NeonPurple.copy(alpha = 0.15f) else CyberSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isUpdated) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar",
                            tint = NeonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (!isUpdated) "Atualizar" else if (payload != null) "Alterar" else "Escolher",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = NeonPurple,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Abrir",
                        tint = NeonPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
