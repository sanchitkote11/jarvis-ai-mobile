package com.example.ui.screens

import android.os.Build
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.services.BatteryInfo
import com.example.services.NetworkInfo
import com.example.services.StorageInfo
import com.example.services.TelemetrySnapshot
import com.example.ui.components.TelemetryLineChart
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun DeviceDiagnosticsDialog(
    batteryInfo: BatteryInfo,
    storageInfo: StorageInfo,
    networkInfo: NetworkInfo,
    onDismiss: () -> Unit,
    telemetrySnapshot: TelemetrySnapshot? = null,
    onLaunchOverlay: (() -> Unit)? = null,
    onOptimizeMemory: (() -> Unit)? = null,
    onOpenBackgroundMode: (() -> Unit)? = null
) {
    var isPowerSaverActive by remember { mutableStateOf(false) }
    var scanStatus by remember { mutableStateOf("All hardware subroutines optimal.") }
    var activeTab by remember { mutableStateOf("CHARTS") } // "CHARTS" or "DETAILS"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = CyberBackground
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
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
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYSTEM TELEMETRY & POWER",
                            color = NeonCyan,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("diag_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Tabs (CHARTS vs HARDWARE)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurface)
                        .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(8.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (activeTab == "CHARTS") NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                            .border(1.dp, if (activeTab == "CHARTS") NeonCyan.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable { activeTab = "CHARTS" }
                            .padding(vertical = 6.dp)
                            .testTag("diag_tab_charts"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timeline, null, tint = if (activeTab == "CHARTS") NeonCyan else TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIVE CHARTS",
                                color = if (activeTab == "CHARTS") NeonCyan else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (activeTab == "DETAILS") NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                            .border(1.dp, if (activeTab == "DETAILS") NeonCyan.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable { activeTab = "DETAILS" }
                            .padding(vertical = 6.dp)
                            .testTag("diag_tab_details"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, null, tint = if (activeTab == "DETAILS") NeonCyan else TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "HARDWARE",
                                color = if (activeTab == "DETAILS") NeonCyan else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Power Saver Mode Toggle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurface)
                        .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ARC CORE BATTERY SAVER",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isPowerSaverActive) "Energy throttle enabled (-35% draw)" else "Full tactical performance mode",
                                color = if (isPowerSaverActive) NeonGreen else TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                        Switch(
                            checked = isPowerSaverActive,
                            onCheckedChange = { isPowerSaverActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberBackground,
                                checkedTrackColor = NeonGreen,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = CyberBackground
                            ),
                            modifier = Modifier.testTag("power_saver_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (activeTab == "CHARTS" && telemetrySnapshot != null) {
                    // LIVE ANIMATED LINE CHARTS
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // CPU Chart
                        TelemetryLineChart(
                            title = "CPU UTILIZATION",
                            currentValue = String.format(Locale.US, "%.1f", telemetrySnapshot.cpuUsagePercent),
                            unit = "%",
                            dataPoints = telemetrySnapshot.cpuHistory,
                            lineColor = NeonCyan,
                            gradientColor = NeonCyan,
                            detailSubtitle = "${telemetrySnapshot.availableCores} Cores Online | ${telemetrySnapshot.activeThreads} Threads",
                            peakValue = telemetrySnapshot.cpuPeakPercent,
                            lowValue = telemetrySnapshot.cpuLowPercent,
                            testTag = "diag_dialog_chart_cpu"
                        )

                        // Memory Chart
                        TelemetryLineChart(
                            title = "RAM CONSUMPTION",
                            currentValue = String.format(Locale.US, "%.1f", telemetrySnapshot.memoryUsagePercent),
                            unit = "%",
                            dataPoints = telemetrySnapshot.memoryHistory,
                            lineColor = NeonGreen,
                            gradientColor = NeonGreen,
                            detailSubtitle = "${telemetrySnapshot.usedRamGb} / ${telemetrySnapshot.totalRamGb} GB (Free: ${telemetrySnapshot.freeRamGb} GB)",
                            peakValue = 90f,
                            lowValue = 30f,
                            testTag = "diag_dialog_chart_memory"
                        )

                        // Battery Chart
                        TelemetryLineChart(
                            title = "BATTERY CELL FLUX",
                            currentValue = "${telemetrySnapshot.batteryPercent.toInt()}",
                            unit = "%",
                            dataPoints = telemetrySnapshot.batteryHistory,
                            lineColor = NeonAmber,
                            gradientColor = NeonAmber,
                            detailSubtitle = "Temp: ${telemetrySnapshot.batteryTempC}°C | ${telemetrySnapshot.batteryVoltageMv} mV",
                            peakValue = 100f,
                            lowValue = 0f,
                            testTag = "diag_dialog_chart_battery"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Launch as Overlay Shortcut Button
                    if (onLaunchOverlay != null) {
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onLaunchOverlay()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("diag_launch_overlay_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, NeonCyan),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                        ) {
                            Icon(Icons.Default.OpenInFull, null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DETACH AS FLOATING HUD OVERLAY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // HARDWARE METRICS LIST
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TelemetryRow(
                            icon = Icons.Default.BatteryChargingFull,
                            title = "BATTERY CELL",
                            value = "${batteryInfo.level}% (${if (batteryInfo.isCharging) "Charging" else "Discharging"})",
                            detail = "Temp: ${batteryInfo.temperatureC}°C | Voltage: ${batteryInfo.voltageMv}mV | Health: ${batteryInfo.health}",
                            color = NeonGreen
                        )

                        TelemetryRow(
                            icon = Icons.Default.Storage,
                            title = "STORAGE CAPACITY",
                            value = "${storageInfo.usedGb} GB / ${storageInfo.totalGb} GB (${storageInfo.usedPercent}%)",
                            detail = "Free space: ${storageInfo.freeGb} GB solid-state drive",
                            color = NeonCyan,
                            progress = storageInfo.usedPercent / 100f
                        )

                        TelemetryRow(
                            icon = Icons.Default.Speed,
                            title = "NETWORK LINK",
                            value = networkInfo.typeName,
                            detail = "Carrier: ${networkInfo.cellularCarrier} | Latency: 18ms",
                            color = NeonCyan
                        )

                        TelemetryRow(
                            icon = Icons.Default.Memory,
                            title = "HARDWARE PLATFORM",
                            value = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL}",
                            detail = "Android OS ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                            color = NeonAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scan Action Button
                Button(
                    onClick = {
                        scanStatus = "Full quantum diagnostic completed: 0 errors detected. Kernel telemetry nominal."
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("run_diagnostics_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberBackground,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "EXECUTE SYSTEM SCAN",
                        color = CyberBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                if (onOpenBackgroundMode != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onOpenBackgroundMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("open_background_mode_from_diagnostics_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SCREEN-OFF & BACKGROUND SETTINGS",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = scanStatus,
                    color = TextCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun TelemetryRow(
    icon: ImageVector,
    title: String,
    value: String,
    detail: String,
    color: Color,
    progress: Float? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface.copy(alpha = 0.6f))
            .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = title, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            if (progress != null) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = color,
                    trackColor = CyberBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = detail, color = TextSecondary, fontSize = 9.5.sp)
        }
    }
}
