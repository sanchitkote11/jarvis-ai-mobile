package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BackgroundSettingsDialog(
    isBackgroundActive: Boolean,
    onToggleBackground: (Boolean) -> Unit,
    onTestAudio: () -> Unit,
    onOptimizeMemory: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    BorderStroke(1.5.dp, if (isBackgroundActive) NeonCyan else CyberCardBorder),
                    RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            color = CyberBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isBackgroundActive) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant)
                                .border(1.dp, if (isBackgroundActive) NeonCyan else TextSecondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (isBackgroundActive) NeonCyan else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "BACKGROUND PROTOCOL",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "SCREEN-OFF PERSISTENCE & HUD",
                                color = if (isBackgroundActive) NeonGreen else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_background_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Master Toggle Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("background_master_toggle_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBackgroundActive) CyberSurface.copy(alpha = 0.95f) else CyberSurfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (isBackgroundActive) NeonGreen.copy(alpha = pulseAlpha) else CyberCardBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isBackgroundActive) NeonGreen else NeonAmber)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBackgroundActive) "CORE SERVICE: RUNNING" else "CORE SERVICE: STANDBY",
                                    color = if (isBackgroundActive) NeonGreen else NeonAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBackgroundActive)
                                    "JARVIS maintains active CPU WakeLock & telemetry while screen is turned off."
                                else
                                    "Tap toggle to enable continuous background operation and lockscreen controls.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Switch(
                            checked = isBackgroundActive,
                            onCheckedChange = { onToggleBackground(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = NeonGreen,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = CyberBackground
                            ),
                            modifier = Modifier.testTag("background_mode_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Capabilities Matrix
                Text(
                    text = "ACTIVE ARCHITECTURE & FEATURES",
                    color = TextCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FeatureStatusRow(
                        icon = Icons.Default.PowerSettingsNew,
                        title = "Screen-Off CPU WakeLock",
                        description = "Acquires PARTIAL_WAKE_LOCK to prevent CPU sleep when phone screen turns off.",
                        active = isBackgroundActive,
                        badgeText = if (isBackgroundActive) "ARMED" else "OFF"
                    )

                    FeatureStatusRow(
                        icon = Icons.Default.Lock,
                        title = "Lock Screen Quick Controls",
                        description = "Direct lockscreen actions for Voice Wake, Flashlight, and RAM Optimization.",
                        active = isBackgroundActive,
                        badgeText = if (isBackgroundActive) "ACTIVE" else "STANDBY"
                    )

                    FeatureStatusRow(
                        icon = Icons.Default.GraphicEq,
                        title = "Live Telemetry Notification",
                        description = "Ongoing persistent notification with real-time CPU, RAM, and Battery updates.",
                        active = isBackgroundActive,
                        badgeText = if (isBackgroundActive) "STREAMING" else "IDLE"
                    )

                    FeatureStatusRow(
                        icon = Icons.Default.VolumeUp,
                        title = "Background Audio & Voice",
                        description = "Text-to-speech feedback and voice queries respond seamlessly without unlocking.",
                        active = isBackgroundActive,
                        badgeText = if (isBackgroundActive) "READY" else "OFF"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Battery Whitelist & Optimization Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CyberCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BatteryAlert,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OEM Battery Optimization",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "To ensure Android or phone manufacturers (Samsung, Xiaomi, OnePlus) don't pause JARVIS after 10+ minutes screen off, allow 'Unrestricted' battery usage.",
                            color = TextSecondary,
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent().apply {
                                        action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    try {
                                        val intent = Intent().apply {
                                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                            data = Uri.fromParts("package", context.packageName, null)
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("battery_optimization_settings_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Open Battery Whitelist Settings",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Diagnostic & Testing Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onTestAudio,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_background_audio_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Test Voice Alert",
                            color = NeonCyanLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onOptimizeMemory,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_optimize_ram_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Optimize RAM",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dismiss_background_dialog_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "DONE",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureStatusRow(
    icon: ImageVector,
    title: String,
    description: String,
    active: Boolean,
    badgeText: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CyberSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CyberCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (active) NeonCyan.copy(alpha = 0.15f) else CyberSurfaceVariant)
                    .border(1.dp, if (active) NeonCyan.copy(alpha = 0.5f) else Color.Transparent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (active) NeonCyan else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 9.5.sp,
                    lineHeight = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = if (active) NeonGreen.copy(alpha = 0.2f) else CyberSurfaceVariant,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(
                    1.dp,
                    if (active) NeonGreen.copy(alpha = 0.6f) else TextSecondary.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = badgeText,
                    color = if (active) NeonGreen else TextSecondary,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
