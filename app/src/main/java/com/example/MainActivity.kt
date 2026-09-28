package com.example

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.JarvisViewModel
import com.example.ui.screens.AddNoteDialog
import com.example.ui.screens.AddScheduleDialog
import com.example.ui.screens.AiChatDialog
import com.example.ui.screens.BackgroundSettingsDialog
import com.example.ui.screens.CalculatorDialog
import com.example.ui.screens.CompassDialog
import com.example.ui.screens.DeviceDiagnosticsDialog
import com.example.ui.screens.DiagnosticOverlayHUD
import com.example.ui.screens.JarvisDashboardScreen
import com.example.ui.screens.QrScannerDialog
import com.example.ui.screens.ViewAllNotesDialog
import com.example.ui.screens.VoiceSettingsDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: JarvisViewModel by viewModels()

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.deviceManager.speak("Notifications authorized for background service and lockscreen controls.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enable show when locked and turn screen on for lockscreen assistant interactions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                JarvisApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("EXTRA_WAKE_VOICE", false) == true) {
            viewModel.openAiChat()
            viewModel.toggleListening()
        }
    }
}

@Composable
fun JarvisApp(viewModel: JarvisViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val batteryInfo by viewModel.batteryState.collectAsStateWithLifecycle()
    val storageInfo by viewModel.storageState.collectAsStateWithLifecycle()
    val networkInfo by viewModel.networkState.collectAsStateWithLifecycle()
    val scheduleItems by viewModel.scheduleItems.collectAsStateWithLifecycle()
    val notesItems by viewModel.notesItems.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val compassDegrees by viewModel.compassDegrees.collectAsStateWithLifecycle()
    val isTorchActive by viewModel.isTorchOn.collectAsStateWithLifecycle()
    val telemetrySnapshot by viewModel.telemetryState.collectAsStateWithLifecycle()
    val isBackgroundActive by viewModel.isBackgroundActive.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Screen HUD
        JarvisDashboardScreen(
            uiState = uiState,
            batteryInfo = batteryInfo,
            storageInfo = storageInfo,
            networkInfo = networkInfo,
            scheduleList = scheduleItems,
            notesList = notesItems,
            isTorchActive = isTorchActive,
            telemetrySnapshot = telemetrySnapshot,
            isBackgroundActive = isBackgroundActive,
            onReactorClick = {
                viewModel.openAiChat()
            },
            onVoiceMicClick = {
                viewModel.toggleListening()
            },
            onVoiceChipClick = { chip ->
                viewModel.executeVoiceQuery(chip)
            },
            onNavClick = { tab ->
                viewModel.setActiveNav(tab)
            },
            onQuickAction = { action ->
                when (action) {
                    "call" -> viewModel.deviceManager.launchPhoneDialer()
                    "messages" -> viewModel.deviceManager.launchMessages()
                    "whatsapp" -> viewModel.deviceManager.launchWhatsApp()
                    "camera" -> viewModel.deviceManager.launchCamera()
                    "gallery" -> viewModel.deviceManager.launchFiles()
                    "music" -> viewModel.togglePlayMusic()
                    "youtube" -> viewModel.deviceManager.launchYouTube()
                    "maps" -> viewModel.deviceManager.launchMaps()
                    "browser" -> viewModel.deviceManager.launchBrowser()
                    "files" -> viewModel.deviceManager.launchFiles()
                    "weather" -> viewModel.executeVoiceQuery("weather update")
                }
            },
            onToolClick = { tool ->
                when (tool) {
                    "calculator" -> viewModel.openCalculator()
                    "torch" -> viewModel.deviceManager.toggleTorch()
                    "compass" -> viewModel.openCompass()
                    "qr_scanner" -> viewModel.openQrScanner()
                    "diagnostics" -> viewModel.openDiagnostics()
                    "telemetry_overlay" -> viewModel.openDiagnosticOverlay()
                    "background_mode" -> viewModel.openBackgroundSettings()
                }
            },
            onToggleSchedule = { item ->
                viewModel.toggleSchedule(item)
            },
            onAddScheduleClick = {
                viewModel.openAddSchedule()
            },
            onViewAllNotesClick = {
                viewModel.openAllNotes()
            },
            onMusicPlayPause = {
                viewModel.togglePlayMusic()
            },
            onMusicPrev = {
                viewModel.prevTrack()
            },
            onMusicNext = {
                viewModel.nextTrack()
            },
            onOpenAiChat = {
                viewModel.openAiChat()
            },
            onOpenVoiceSettings = {
                viewModel.openVoiceSettings()
            }
        )

        // Dialogs
        if (uiState.showCalculator) {
            CalculatorDialog(
                onDismiss = { viewModel.closeCalculator() }
            )
        }

        if (uiState.showCompass) {
            CompassDialog(
                degrees = compassDegrees,
                onDismiss = { viewModel.closeCompass() }
            )
        }

        if (uiState.showDiagnostics) {
            DeviceDiagnosticsDialog(
                batteryInfo = batteryInfo,
                storageInfo = storageInfo,
                networkInfo = networkInfo,
                telemetrySnapshot = telemetrySnapshot,
                onLaunchOverlay = { viewModel.openDiagnosticOverlay() },
                onOptimizeMemory = { viewModel.optimizeMemory() },
                onOpenBackgroundMode = {
                    viewModel.closeDiagnostics()
                    viewModel.openBackgroundSettings()
                },
                onDismiss = { viewModel.closeDiagnostics() }
            )
        }

        if (uiState.showDiagnosticOverlay) {
            DiagnosticOverlayHUD(
                snapshot = telemetrySnapshot,
                isMinimized = uiState.isDiagnosticOverlayMinimized,
                onMinimizeToggle = { viewModel.toggleDiagnosticOverlayMinimize() },
                onClose = { viewModel.closeDiagnosticOverlay() },
                onOptimizeMemory = { viewModel.optimizeMemory() }
            )
        }

        if (uiState.showQrScanner) {
            QrScannerDialog(
                onDismiss = { viewModel.closeQrScanner() }
            )
        }

        if (uiState.showAiChat) {
            AiChatDialog(
                messages = chatMessages,
                aiAssistant = viewModel.aiAssistant,
                deviceManager = viewModel.deviceManager,
                userName = uiState.userName,
                onSendMessage = { text, sender ->
                    viewModel.sendMessage(text, sender)
                },
                onClearChat = {
                    viewModel.clearChat()
                },
                onDismiss = { viewModel.closeAiChat() }
            )
        }

        if (uiState.showVoiceSettings) {
            VoiceSettingsDialog(
                currentUserName = uiState.userName,
                deviceManager = viewModel.deviceManager,
                onSaveUserName = { newName ->
                    viewModel.setUserName(newName)
                },
                onDismiss = { viewModel.closeVoiceSettings() }
            )
        }

        if (uiState.showBackgroundSettings) {
            BackgroundSettingsDialog(
                isBackgroundActive = isBackgroundActive,
                onToggleBackground = { viewModel.toggleBackgroundMode(it) },
                onTestAudio = { viewModel.testBackgroundAudio() },
                onOptimizeMemory = { viewModel.optimizeMemory() },
                onDismiss = { viewModel.closeBackgroundSettings() }
            )
        }

        if (uiState.showAddSchedule) {
            AddScheduleDialog(
                onAdd = { title, time ->
                    viewModel.addSchedule(title, time)
                },
                onDismiss = { viewModel.closeAddSchedule() }
            )
        }

        if (uiState.showAddNote) {
            AddNoteDialog(
                onAdd = { title, content, category ->
                    viewModel.addNote(title, content, category)
                },
                onDismiss = { viewModel.closeAddNote() }
            )
        }

        if (uiState.showAllNotes) {
            ViewAllNotesDialog(
                notes = notesItems,
                onDeleteNote = { id ->
                    viewModel.deleteNote(id)
                },
                onAddNewNoteClick = {
                    viewModel.openAddNote()
                },
                onDismiss = { viewModel.closeAllNotes() }
            )
        }
    }
}
