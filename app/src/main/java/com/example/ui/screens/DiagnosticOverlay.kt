package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun DiagnosticOverlayHUD(
    snapshot: TelemetrySnapshot,
    isMinimized: Boolean,
    onMinimizeToggle: () -> Unit,
    onClose: () -> Unit,
    onOptimizeMemory: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isMinimized) {
        // Floating Mini-HUD pill pinned on screen
        FloatingDiagnosticPill(
            snapshot = snapshot,
            onExpand = onMinimizeToggle,
            onClose = onClose,
            modifier = modifier
        )
    } else {
        // Full Holographic Diagnostic Overlay HUD
        FullDiagnosticOverlay(
            snapshot = snapshot,
            onMinimize = onMinimizeToggle,
            onClose = onClose,
            onOptimizeMemory = onOptimizeMemory,
            modifier = modifier
        )
    }
}

@Composable
private fun FullDiagnosticOverlay(
    snapshot: TelemetrySnapshot,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    onOptimizeMemory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var optimizationFeedback by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "DiagOverlayAnim")
    val beaconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BeaconAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground.copy(alpha = 0.94f))
            .statusBarsPadding()
            .testTag("diagnostic_overlay_fullscreen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Header: Status Beacon, Title, Minimize & Close Controls
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
                            .background(NeonGreen.copy(alpha = beaconAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SYSTEM TELEMETRY OVERLAY",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "SAMPLE #${snapshot.sampleIndex} | STATUS: ${snapshot.systemStatus} | CORES: ${snapshot.availableCores}",
                            color = TextSecondary,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Minimize to Floating HUD
                    IconButton(
                        onClick = onMinimize,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("diag_minimize_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerticalAlignBottom,
                            contentDescription = "Minimize to Floating Pill",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Close Overlay
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("diag_overlay_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Diagnostic Overlay",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs (ALL / CPU / RAM / BATTERY)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurface)
                    .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(8.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    Pair("ALL", "ALL METRICS"),
                    Pair("CPU", "CPU CORE"),
                    Pair("RAM", "RAM MEMORY"),
                    Pair("BAT", "BATTERY CELL")
                ).forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) NeonCyan.copy(alpha = 0.6f) else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedFilter = key }
                            .padding(vertical = 6.dp)
                            .testTag("filter_tab_$key"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) NeonCyan else TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Visualizer Scrollable Charts Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. CPU LINE CHART
                if (selectedFilter == "ALL" || selectedFilter == "CPU") {
                    TelemetryLineChart(
                        title = "CENTRAL PROCESSING UNIT",
                        currentValue = String.format(Locale.US, "%.1f", snapshot.cpuUsagePercent),
                        unit = "%",
                        dataPoints = snapshot.cpuHistory,
                        lineColor = NeonCyan,
                        gradientColor = NeonCyan,
                        detailSubtitle = "${snapshot.availableCores} Cores Online | ${snapshot.activeThreads} Process Threads",
                        peakValue = snapshot.cpuPeakPercent,
                        lowValue = snapshot.cpuLowPercent,
                        testTag = "chart_cpu"
                    )
                }

                // 2. MEMORY (RAM) LINE CHART
                if (selectedFilter == "ALL" || selectedFilter == "RAM") {
                    TelemetryLineChart(
                        title = "RAM ALLOCATION & HEAP",
                        currentValue = String.format(Locale.US, "%.1f", snapshot.memoryUsagePercent),
                        unit = "%",
                        dataPoints = snapshot.memoryHistory,
                        lineColor = NeonGreen,
                        gradientColor = NeonGreen,
                        detailSubtitle = "Used: ${snapshot.usedRamGb} GB / ${snapshot.totalRamGb} GB | Free: ${snapshot.freeRamGb} GB | JVM: ${snapshot.jvmUsedMb} MB",
                        peakValue = 88f,
                        lowValue = 35f,
                        testTag = "chart_memory"
                    )
                }

                // 3. BATTERY & THERMALS LINE CHART
                if (selectedFilter == "ALL" || selectedFilter == "BAT") {
                    TelemetryLineChart(
                        title = "ARC BATTERY CELL & THERMALS",
                        currentValue = "${snapshot.batteryPercent.toInt()}",
                        unit = "%",
                        dataPoints = snapshot.batteryHistory,
                        lineColor = NeonAmber,
                        gradientColor = NeonAmber,
                        detailSubtitle = "Temp: ${snapshot.batteryTempC}°C | Voltage: ${snapshot.batteryVoltageMv} mV | ${if (snapshot.isCharging) "Power Source Connected" else "Discharging Under Load"}",
                        peakValue = 100f,
                        lowValue = 0f,
                        testTag = "chart_battery"
                    )
                }

                // Live Summary Stats Grid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurface.copy(alpha = 0.7f))
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TelemetryStatItem(
                                label = "THERMAL STATUS",
                                value = if (snapshot.batteryTempC < 38f) "NOMINAL" else "ELEVATED",
                                color = if (snapshot.batteryTempC < 38f) NeonGreen else NeonAmber
                            )
                            TelemetryStatItem(
                                label = "HEAP USAGE",
                                value = "${snapshot.jvmUsedMb.toInt()} MB",
                                color = NeonCyan
                            )
                            TelemetryStatItem(
                                label = "CORES LOADED",
                                value = "${snapshot.availableCores} / ${snapshot.availableCores}",
                                color = NeonGreen
                            )
                            TelemetryStatItem(
                                label = "REFRESH RATE",
                                value = "1.0s / 60Hz",
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Bar: Optimize Memory / Flush Heap & Minimize Shortcut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onOptimizeMemory()
                        optimizationFeedback = "Garbage collection complete: JVM memory optimized."
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("diag_optimize_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        tint = CyberBackground,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FLUSH & OPTIMIZE",
                        color = CyberBackground,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                OutlinedButton(
                    onClick = onMinimize,
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("diag_dock_floating_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerticalAlignBottom,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "DOCK MINI HUD",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (optimizationFeedback != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = optimizationFeedback ?: "",
                    color = NeonGreen,
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

/**
 * Sleek floating HUD pill that remains on top of any screen when minimized
 */
@Composable
private fun FloatingDiagnosticPill(
    snapshot: TelemetrySnapshot,
    onExpand: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PillPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PillPulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 10.dp, end = 12.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.8f)), RoundedCornerShape(20.dp))
                .clickable { onExpand() }
                .testTag("floating_telemetry_pill"),
            shape = RoundedCornerShape(20.dp),
            color = CyberBackground.copy(alpha = 0.92f),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Live status dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonGreen.copy(alpha = pulseAlpha))
                )

                // Quick readouts
                Text(
                    text = "CPU ${snapshot.cpuUsagePercent.toInt()}%",
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "•",
                    color = TextSecondary,
                    fontSize = 9.sp
                )

                Text(
                    text = "RAM ${snapshot.memoryUsagePercent.toInt()}%",
                    color = NeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "•",
                    color = TextSecondary,
                    fontSize = 9.sp
                )

                Text(
                    text = "BAT ${snapshot.batteryPercent.toInt()}%",
                    color = NeonAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // Expand icon
                Icon(
                    imageVector = Icons.Default.OpenInFull,
                    contentDescription = "Expand Overlay",
                    tint = TextPrimary,
                    modifier = Modifier.size(13.dp)
                )

                // Close icon
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryStatItem(
    label: String,
    value: String,
    color: Color
) {
    Column {
        Text(text = label, color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = color, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
