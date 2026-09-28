package com.example.ui.screens

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.NoteEntity
import com.example.data.ScheduleEntity
import com.example.services.BatteryInfo
import com.example.services.NetworkInfo
import com.example.services.StorageInfo
import com.example.services.TelemetrySnapshot
import com.example.ui.JarvisUiState
import com.example.ui.components.ArcReactorHud
import com.example.ui.components.CyberActionButton
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberToolTile
import com.example.ui.components.FluidLiquidPhysicsContainer
import com.example.ui.components.LiquidMeniscusWave
import com.example.ui.components.WaterRipplePulse
import com.example.ui.components.WaterWaveBackground
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBackgroundElevated
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardBorderGlow
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonBlueLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JarvisDashboardScreen(
    uiState: JarvisUiState,
    batteryInfo: BatteryInfo,
    storageInfo: StorageInfo,
    networkInfo: NetworkInfo,
    scheduleList: List<ScheduleEntity>,
    notesList: List<NoteEntity>,
    isTorchActive: Boolean,
    telemetrySnapshot: TelemetrySnapshot? = null,
    isBackgroundActive: Boolean = false,
    onReactorClick: () -> Unit,
    onVoiceMicClick: () -> Unit,
    onVoiceChipClick: (String) -> Unit,
    onNavClick: (String) -> Unit,
    onQuickAction: (String) -> Unit,
    onToolClick: (String) -> Unit,
    onToggleSchedule: (ScheduleEntity) -> Unit,
    onAddScheduleClick: () -> Unit,
    onViewAllNotesClick: () -> Unit,
    onMusicPlayPause: () -> Unit,
    onMusicPrev: () -> Unit,
    onMusicNext: () -> Unit,
    onOpenAiChat: () -> Unit,
    onOpenVoiceSettings: () -> Unit
) {
    FluidLiquidPhysicsContainer(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Left Futuristic Side-Dock (matches javis ui.png)
            SideCyberDock(
                activeTab = uiState.activeNavTab,
                onTabSelect = onNavClick
            )

            // Main Central Dashboard HUD
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Scrollable content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                ) {
                    // 1. Top HUD Status Bar (Clock, Weather, Battery & Network Widget)
                    item {
                        TopHudStatusBar(
                            batteryInfo = batteryInfo,
                            storageInfo = storageInfo,
                            networkInfo = networkInfo,
                            telemetrySnapshot = telemetrySnapshot,
                            isBackgroundActive = isBackgroundActive,
                            onSystemCardClick = { onToolClick("telemetry_overlay") },
                            onBackgroundClick = { onToolClick("background_mode") }
                        )
                    }

                    // Fluid liquid motion separator
                    item {
                        LiquidMeniscusWave(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        )
                    }

                    // 2. Center Arc Reactor HUD (The iconic centerpiece)
                    item {
                        CyberCard(
                            modifier = Modifier.fillMaxWidth(),
                            glowBorder = true
                        ) {
                            ArcReactorHud(
                                userName = uiState.userName,
                                statusText = uiState.statusGreeting,
                                isSpeaking = uiState.isSpeaking,
                                onReactorClick = onReactorClick
                            )
                        }
                    }

                    // 3. Voice Interaction Section with Water Ripple Pulse
                    item {
                        VoiceControlHud(
                            isListening = uiState.isListening,
                            onMicClick = onVoiceMicClick,
                            onChipClick = onVoiceChipClick
                        )
                    }

                    // 4. Quick Actions Grid (Call, Messages, WhatsApp, Camera, Gallery, etc.)
                    item {
                        QuickActionsSection(
                            onActionClick = onQuickAction
                        )
                    }

                    // 5. Triple HUD Cards: Weather, Calendar & Notes
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Weather Card
                            WeatherHudCard(
                                modifier = Modifier.weight(1f),
                                onCardClick = { onQuickAction("weather") }
                            )

                            // Calendar Card
                            CalendarHudCard(
                                modifier = Modifier.weight(1.1f),
                                schedule = scheduleList,
                                onToggle = onToggleSchedule,
                                onAddClick = onAddScheduleClick
                            )

                            // Notes Card
                            NotesHudCard(
                                modifier = Modifier.weight(1f),
                                notes = notesList,
                                onViewAllClick = onViewAllNotesClick
                            )
                        }
                    }

                    // 6. Tools & Features Grid (Calculator, Screen Recorder, Torch, QR, Compass, Battery Saver)
                    item {
                        ToolsAndFeaturesSection(
                            isTorchActive = isTorchActive,
                            isBackgroundActive = isBackgroundActive,
                            onToolClick = onToolClick
                        )
                    }

                    // 7. Media Player Card & Recent Files Card
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Media Player
                            MediaHudCard(
                                modifier = Modifier.weight(1.2f),
                                uiState = uiState,
                                onPlayPause = onMusicPlayPause,
                                onPrev = onMusicPrev,
                                onNext = onMusicNext
                            )

                            // Recent Files
                            RecentFilesHudCard(
                                modifier = Modifier.weight(1f),
                                onFilesClick = { onQuickAction("files") }
                            )
                        }
                    }

                    // 8. Bottom Action Banner Cards: AI Chat, Voice Settings, More Apps
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CyberCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenAiChat() }
                                    .testTag("open_ai_chat_card")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(text = "AI Chat", color = TextPrimary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Ask anything to Jarvis", color = TextSecondary, fontSize = 8.5.sp, maxLines = 1)
                                    }
                                }
                            }

                            CyberCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenVoiceSettings() }
                                    .testTag("open_voice_settings_card")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(text = "Voice Settings", color = TextPrimary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Voice, speed & profile", color = TextSecondary, fontSize = 8.5.sp, maxLines = 1)
                                    }
                                }
                            }

                            CyberCard(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .clickable { onToolClick("telemetry_overlay") }
                                    .testTag("more_apps_card")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(text = "Telemetry HUD", color = TextPrimary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Live animated charts", color = TextSecondary, fontSize = 8.5.sp, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Bottom Cyber Deck with Center Glowing Arc Reactor button
        BottomCyberDeck(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            activeTab = uiState.activeNavTab,
            onTabSelect = onNavClick,
            onCenterReactorClick = onReactorClick
        )
    }
}

/**
 * Top HUD Bar displaying Live Clock, Date, Weather conditions, and System Status card.
 */
@Composable
private fun TopHudStatusBar(
    batteryInfo: BatteryInfo,
    storageInfo: StorageInfo,
    networkInfo: NetworkInfo,
    onSystemCardClick: () -> Unit,
    telemetrySnapshot: TelemetrySnapshot? = null,
    isBackgroundActive: Boolean = false,
    onBackgroundClick: () -> Unit = {}
) {
    val currentTime = remember { SimpleDateFormat("hh:mm", Locale.US).format(Date()) }
    val currentDate = remember { SimpleDateFormat("EEE, d MMM yyyy", Locale.US).format(Date()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Clock & Weather
        Column {
            Text(
                text = currentTime,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = currentDate,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = NeonAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "28°C Partly Cloudy • Pune",
                    color = TextCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Right System Status Card (from javis ui.png)
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(12.dp))
                .clickable { onSystemCardClick() }
                .testTag("system_status_top_card"),
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface.copy(alpha = 0.85f)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "System Status",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = if (isBackgroundActive) NeonGreen.copy(alpha = 0.2f) else CyberSurfaceVariant,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isBackgroundActive) NeonGreen.copy(alpha = 0.8f) else CyberCardBorder
                            ),
                            modifier = Modifier
                                .clickable { onBackgroundClick() }
                                .testTag("top_status_bg_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (isBackgroundActive) NeonGreen else NeonAmber)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isBackgroundActive) "BG ON" else "BG OFF",
                                    color = if (isBackgroundActive) NeonGreen else TextSecondary,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            text = "HUD",
                            color = NeonCyan,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Battery
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${batteryInfo.level}%",
                            color = NeonGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Network
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "4G",
                            color = NeonCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Jio",
                            color = TextPrimary,
                            fontSize = 9.sp
                        )
                    }

                    // Wi-Fi
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                if (telemetrySnapshot != null) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "CPU ${telemetrySnapshot.cpuUsagePercent.toInt()}%",
                            color = NeonCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(text = "•", color = TextSecondary, fontSize = 7.sp)
                        Text(
                            text = "RAM ${telemetrySnapshot.memoryUsagePercent.toInt()}%",
                            color = NeonGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                // Storage Bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Storage: ${storageInfo.usedPercent}% (${storageInfo.usedGb.toInt()}/${storageInfo.totalGb.toInt()} GB)",
                        color = TextSecondary,
                        fontSize = 8.sp
                    )
                }
                LinearProgressIndicator(
                    progress = { storageInfo.usedPercent / 100f },
                    modifier = Modifier
                        .width(130.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp)),
                    color = NeonCyan,
                    trackColor = CyberBackground
                )
            }
        }
    }
}

/**
 * Left Cyber Side-Dock with vertical nav links and bottom Arc Reactor.
 */
@Composable
private fun SideCyberDock(
    activeTab: String,
    onTabSelect: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .width(58.dp)
            .fillMaxHeight()
            .padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = CyberSurface.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Logo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f))
                        .border(1.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▼",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "JARVIS",
                    color = TextPrimary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "AI ASSISTANT",
                    color = TextCyan,
                    fontSize = 5.sp
                )
            }

            // Nav Icons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val dockItems = listOf(
                    Pair("Home", Icons.Default.Home),
                    Pair("Voice", Icons.Default.Mic),
                    Pair("Apps", Icons.Default.Workspaces),
                    Pair("Files", Icons.Default.Folder),
                    Pair("Settings", Icons.Default.Settings),
                    Pair("Tools", Icons.Default.Calculate),
                    Pair("Browser", Icons.Default.Language),
                    Pair("Notes", Icons.Default.Description),
                    Pair("Camera", Icons.Default.CameraAlt),
                    Pair("Music", Icons.Default.MusicNote)
                )

                for ((name, icon) in dockItems) {
                    val isActive = activeTab == name
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isActive) NeonBlue.copy(alpha = 0.35f) else Color.Transparent
                            )
                            .clickable { onTabSelect(name) }
                            .testTag("dock_item_$name"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = name,
                            tint = if (isActive) NeonCyan else TextSecondary.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Bottom Arc Reactor Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Smarter\nFaster",
                    color = TextCyan.copy(alpha = 0.7f),
                    fontSize = 6.sp,
                    lineHeight = 7.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f))
                        .border(1.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BrightnessLow,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Tap to speak voice HUD bar with interactive ripple pulse and quick voice command chips.
 */
@Composable
private fun VoiceControlHud(
    isListening: Boolean,
    onMicClick: () -> Unit,
    onChipClick: (String) -> Unit
) {
    CyberCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Microphone Button with Water Ripple Pulse
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("voice_mic_btn")
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = NeonCyan),
                            onClick = onMicClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isListening) {
                        WaterRipplePulse(
                            modifier = Modifier.size(54.dp),
                            color = NeonCyan
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening) NeonCyan else CyberSurfaceVariant
                            )
                            .border(1.5.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speak",
                            tint = if (isListening) CyberBackground else NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isListening) "LISTENING TO VOICE..." else "Tap to speak",
                        color = if (isListening) NeonCyan else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "or say \"Hey Jarvis\"",
                        color = TextCyan,
                        fontSize = 10.sp
                    )
                }

                // Wave equalizer indicator
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = if (isListening) NeonGreen else NeonCyan,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick suggestion chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val chips = listOf(
                    "what's the weather?",
                    "Open YouTube",
                    "Set a reminder",
                    "Play music",
                    "Turn on torch",
                    "System status"
                )
                items(chips) { chip ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(BorderStroke(0.8.dp, CyberCardBorder), RoundedCornerShape(8.dp))
                            .clickable { onChipClick(chip) },
                        shape = RoundedCornerShape(8.dp),
                        color = CyberBackgroundElevated
                    ) {
                        Text(
                            text = chip,
                            color = TextCyan,
                            fontSize = 9.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Quick Actions 10-button grid from the image.
 */
@Composable
private fun QuickActionsSection(
    onActionClick: (String) -> Unit
) {
    CyberCard(
        title = "⚡ Quick Actions",
        headerAction = {
            Text(
                text = "Customize ⚙",
                color = TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Row 1: Call, Messages, WhatsApp, Camera, Gallery
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CyberActionButton(
                    icon = Icons.Default.Call,
                    label = "Call",
                    subLabel = "Make a call",
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("call") }

                CyberActionButton(
                    icon = Icons.Default.Sms,
                    label = "Messages",
                    subLabel = "Send text",
                    accentColor = NeonBlueLight,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("messages") }

                CyberActionButton(
                    icon = Icons.Default.Workspaces,
                    label = "WhatsApp",
                    subLabel = "Open app",
                    accentColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("whatsapp") }

                CyberActionButton(
                    icon = Icons.Default.CameraAlt,
                    label = "Camera",
                    subLabel = "Open camera",
                    accentColor = NeonMagenta,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("camera") }

                CyberActionButton(
                    icon = Icons.Default.Photo,
                    label = "Gallery",
                    subLabel = "View photos",
                    accentColor = NeonCyanLight,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("gallery") }
            }

            // Row 2: Music, YouTube, Maps, Browser, Files
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CyberActionButton(
                    icon = Icons.Default.MusicNote,
                    label = "Music",
                    subLabel = "Play music",
                    accentColor = NeonMagenta,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("music") }

                CyberActionButton(
                    icon = Icons.Default.Videocam,
                    label = "YouTube",
                    subLabel = "Open YouTube",
                    accentColor = NeonRed,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("youtube") }

                CyberActionButton(
                    icon = Icons.Default.Map,
                    label = "Maps",
                    subLabel = "Open Maps",
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("maps") }

                CyberActionButton(
                    icon = Icons.Default.Language,
                    label = "Browser",
                    subLabel = "Open web",
                    accentColor = NeonBlueLight,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("browser") }

                CyberActionButton(
                    icon = Icons.Default.Folder,
                    label = "Files",
                    subLabel = "Browse files",
                    accentColor = NeonAmber,
                    modifier = Modifier.weight(1f)
                ) { onActionClick("files") }
            }
        }
    }
}

/**
 * Weather HUD Card with gradient backdrop and high/low stats.
 */
@Composable
private fun WeatherHudCard(
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit
) {
    CyberCard(
        modifier = modifier.clickable { onCardClick() },
        title = "Weather",
        icon = Icons.Default.Cloud
    ) {
        Column {
            Text(
                text = "28° C",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Partly Cloudy",
                color = TextCyan,
                fontSize = 10.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "H: 32°  L: 24°",
                color = TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}

/**
 * Calendar HUD Card with schedule check items.
 */
@Composable
private fun CalendarHudCard(
    modifier: Modifier = Modifier,
    schedule: List<ScheduleEntity>,
    onToggle: (ScheduleEntity) -> Unit,
    onAddClick: () -> Unit
) {
    CyberCard(
        modifier = modifier,
        title = "Calendar",
        icon = Icons.Default.WbSunny,
        headerAction = {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier.size(20.dp).testTag("card_add_schedule_btn")
            ) {
                Icon(Icons.Default.Add, null, tint = NeonCyan, modifier = Modifier.size(14.dp))
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val displayItems = schedule.take(3)
            for (item in displayItems) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle(item) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (item.isCompleted) NeonGreen else TextSecondary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.title,
                            color = if (item.isCompleted) TextSecondary else TextPrimary,
                            fontSize = 9.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = item.time,
                        color = NeonCyanLight,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

/**
 * Notes HUD Card displaying quick saved note topics.
 */
@Composable
private fun NotesHudCard(
    modifier: Modifier = Modifier,
    notes: List<NoteEntity>,
    onViewAllClick: () -> Unit
) {
    CyberCard(
        modifier = modifier.clickable { onViewAllClick() },
        title = "Notes",
        icon = Icons.Default.Description,
        headerAction = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(10.dp)
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            val displayNotes = notes.take(4)
            for (note in displayNotes) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "• ", color = NeonCyan, fontSize = 10.sp)
                    Text(
                        text = note.title,
                        color = TextPrimary,
                        fontSize = 9.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Tools & Features Grid (Calculator, Screen Recorder, Torch, QR Scanner, Compass, Battery Saver)
 */
@Composable
private fun ToolsAndFeaturesSection(
    isTorchActive: Boolean,
    isBackgroundActive: Boolean = false,
    onToolClick: (String) -> Unit
) {
    CyberCard(
        title = "🔧 Tools & Features"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CyberToolTile(
                icon = Icons.Default.Calculate,
                label = "Calculator",
                modifier = Modifier.weight(1f)
            ) { onToolClick("calculator") }

            CyberToolTile(
                icon = Icons.Default.GraphicEq,
                label = "Live HUD",
                modifier = Modifier.weight(1f)
            ) { onToolClick("telemetry_overlay") }

            CyberToolTile(
                icon = Icons.Default.Security,
                label = "Screen-Off",
                isActive = isBackgroundActive,
                activeColor = NeonGreen,
                modifier = Modifier.weight(1.1f)
            ) { onToolClick("background_mode") }

            CyberToolTile(
                icon = Icons.Default.FlashlightOn,
                label = "Torch",
                isActive = isTorchActive,
                activeColor = NeonAmber,
                modifier = Modifier.weight(1f)
            ) { onToolClick("torch") }

            CyberToolTile(
                icon = Icons.Default.QrCodeScanner,
                label = "QR Scan",
                modifier = Modifier.weight(1f)
            ) { onToolClick("qr_scanner") }

            CyberToolTile(
                icon = Icons.Default.CompassCalibration,
                label = "Compass",
                modifier = Modifier.weight(1f)
            ) { onToolClick("compass") }

            CyberToolTile(
                icon = Icons.Default.BatteryChargingFull,
                label = "Battery",
                modifier = Modifier.weight(1f)
            ) { onToolClick("diagnostics") }
        }
    }
}

/**
 * Media HUD Card with cyberpunk album cover and playback controls.
 */
@Composable
private fun MediaHudCard(
    modifier: Modifier = Modifier,
    uiState: JarvisUiState,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current

    CyberCard(
        modifier = modifier,
        title = "Media",
        icon = Icons.Default.MusicNote
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Album Art
                Surface(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, NeonMagenta.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    color = CyberSurfaceVariant
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("android.resource://${context.packageName}/drawable/cyber_music_cover_1790504939563")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Track Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.currentTrackTitle,
                        color = TextPrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = uiState.currentTrackArtist,
                        color = NeonCyanLight,
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                }

                Text(
                    text = "${uiState.trackTimeElapsed} / ${uiState.trackTimeDuration}",
                    color = TextSecondary,
                    fontSize = 8.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Playback Progress
            LinearProgressIndicator(
                progress = { uiState.trackProgressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp)),
                color = NeonMagenta,
                trackColor = CyberBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrev,
                    modifier = Modifier.size(32.dp).testTag("media_prev_btn")
                ) {
                    Icon(Icons.Default.SkipPrevious, "Prev", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                        .clickable { onPlayPause() }
                        .testTag("media_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isPlayingMusic) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isPlayingMusic) "Pause" else "Play",
                        tint = CyberBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(32.dp).testTag("media_next_btn")
                ) {
                    Icon(Icons.Default.SkipNext, "Next", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/**
 * Recent Files HUD Card from the image.
 */
@Composable
private fun RecentFilesHudCard(
    modifier: Modifier = Modifier,
    onFilesClick: () -> Unit
) {
    CyberCard(
        modifier = modifier.clickable { onFilesClick() },
        title = "Recent Files",
        icon = Icons.Default.Folder,
        headerAction = {
            Text(
                text = "view all",
                color = NeonCyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val files = listOf(
                Pair("Project.zip", "2.4 MB • 10 min ago"),
                Pair("Notes.txt", "8 KB • 1 hr ago"),
                Pair("Jarvis_UI.html", "12 KB • 3 hr ago"),
                Pair("Map_Offline.apk", "11.7 MB • 5 hr ago")
            )
            for ((name, meta) in files) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = NeonCyanLight,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = name,
                            color = TextPrimary,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = meta,
                            color = TextSecondary,
                            fontSize = 7.5.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom Floating Cyber Deck with Center Glowing Arc Reactor.
 */
@Composable
private fun BottomCyberDeck(
    modifier: Modifier = Modifier,
    activeTab: String,
    onTabSelect: (String) -> Unit,
    onCenterReactorClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(26.dp))
                .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            color = CyberSurface.copy(alpha = 0.94f),
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left 3 items
                IconButton(
                    onClick = { onTabSelect("Home") },
                    modifier = Modifier.testTag("deck_home")
                ) {
                    Icon(Icons.Default.Home, "Home", tint = if (activeTab == "Home") NeonCyan else TextSecondary, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { onTabSelect("History") },
                    modifier = Modifier.testTag("deck_history")
                ) {
                    Icon(Icons.Default.Explore, "History", tint = if (activeTab == "History") NeonCyan else TextSecondary, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { onTabSelect("Apps") },
                    modifier = Modifier.testTag("deck_apps")
                ) {
                    Icon(Icons.Default.Workspaces, "Apps", tint = if (activeTab == "Apps") NeonCyan else TextSecondary, modifier = Modifier.size(20.dp))
                }

                // Space for center Arc Reactor button
                Spacer(modifier = Modifier.width(48.dp))

                // Right 3 items
                IconButton(
                    onClick = { onTabSelect("Music") },
                    modifier = Modifier.testTag("deck_music")
                ) {
                    Icon(Icons.Default.MusicNote, "Music", tint = if (activeTab == "Music") NeonCyan else TextSecondary, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { onTabSelect("Gallery") },
                    modifier = Modifier.testTag("deck_gallery")
                ) {
                    Icon(Icons.Default.Photo, "Gallery", tint = if (activeTab == "Gallery") NeonCyan else TextSecondary, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { onTabSelect("Settings") },
                    modifier = Modifier.testTag("deck_settings")
                ) {
                    Icon(Icons.Default.Settings, "Settings", tint = if (activeTab == "Settings") NeonCyan else TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Center Floating Glowing Arc Reactor Button
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(NeonCyanLight, NeonBlue, CyberBackground)
                    )
                )
                .border(2.dp, NeonCyan, CircleShape)
                .clickable { onCenterReactorClick() }
                .testTag("deck_center_arc_reactor"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BrightnessLow,
                contentDescription = "Jarvis Reactor",
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
