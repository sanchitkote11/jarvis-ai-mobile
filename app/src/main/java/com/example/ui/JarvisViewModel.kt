package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessageEntity
import com.example.data.JarvisDatabase
import com.example.data.JarvisRepository
import com.example.data.NoteEntity
import com.example.data.ScheduleEntity
import com.example.services.BatteryInfo
import com.example.services.DeviceManager
import com.example.services.JarvisAiAssistant
import com.example.services.JarvisBackgroundService
import com.example.services.NetworkInfo
import com.example.services.StorageInfo
import com.example.services.SystemTelemetryMonitor
import com.example.services.TelemetrySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class JarvisUiState(
    val userName: String = "Sanchit",
    val statusGreeting: String = "How can I help you today?",
    val isSpeaking: Boolean = false,
    val isListening: Boolean = false,
    val voiceTranscript: String = "",
    val activeNavTab: String = "Home",
    // Active Dialogs & Overlays
    val showCalculator: Boolean = false,
    val showCompass: Boolean = false,
    val showDiagnostics: Boolean = false,
    val showDiagnosticOverlay: Boolean = false,
    val isDiagnosticOverlayMinimized: Boolean = false,
    val showQrScanner: Boolean = false,
    val showAiChat: Boolean = false,
    val showVoiceSettings: Boolean = false,
    val showBackgroundSettings: Boolean = false,
    val showAddSchedule: Boolean = false,
    val showAddNote: Boolean = false,
    val showAllNotes: Boolean = false,
    // Music Player State
    val isPlayingMusic: Boolean = false,
    val currentTrackTitle: String = "Better Days",
    val currentTrackArtist: String = "NEFFEX",
    val trackProgressFraction: Float = 0.65f,
    val trackTimeElapsed: String = "02:14",
    val trackTimeDuration: String = "03:24"
)

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    val deviceManager = DeviceManager(application.applicationContext)
    val aiAssistant = JarvisAiAssistant(deviceManager)

    private val database = JarvisDatabase.getDatabase(application.applicationContext, viewModelScope)
    val repository = JarvisRepository(database.jarvisDao())

    val scheduleItems: StateFlow<List<ScheduleEntity>> = repository.allSchedule
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notesItems: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val batteryState: StateFlow<BatteryInfo> = deviceManager.batteryState
    val storageState: StateFlow<StorageInfo> = deviceManager.storageState
    val networkState: StateFlow<NetworkInfo> = deviceManager.networkState
    val compassDegrees: StateFlow<Float> = deviceManager.compassDegrees
    val isTorchOn: StateFlow<Boolean> = deviceManager.isTorchOn

    val telemetryMonitor = SystemTelemetryMonitor(application.applicationContext, viewModelScope)
    val telemetryState: StateFlow<TelemetrySnapshot> = telemetryMonitor.telemetryState

    val isBackgroundActive: StateFlow<Boolean> = JarvisBackgroundService.isRunning

    init {
        viewModelScope.launch {
            deviceManager.batteryState.collect { bat ->
                telemetryMonitor.updateBatteryDetails(bat.temperatureC, bat.voltageMv, bat.isCharging)
            }
        }
    }

    private val _uiState = MutableStateFlow(JarvisUiState())
    val uiState: StateFlow<JarvisUiState> = _uiState.asStateFlow()

    // Playlist
    private val playlist = listOf(
        Pair("Better Days", "NEFFEX"),
        Pair("Synthwave Horizon", "Kavinsky"),
        Pair("Quantum Core", "Hyper"),
        Pair("Mark 85 Armor", "Stark Audio")
    )
    private var currentTrackIndex = 0

    fun setUserName(name: String) {
        _uiState.value = _uiState.value.copy(userName = name)
    }

    fun setActiveNav(tab: String) {
        _uiState.value = _uiState.value.copy(activeNavTab = tab)
        when (tab) {
            "Voice" -> openAiChat()
            "Browser" -> deviceManager.launchBrowser()
            "Notes" -> openAllNotes()
            "Camera" -> deviceManager.launchCamera()
            "Music" -> togglePlayMusic()
            "Tools" -> openDiagnosticOverlay()
            "Files" -> deviceManager.launchFiles()
            "Settings" -> openVoiceSettings()
        }
    }

    // Dialog controls
    fun openCalculator() { _uiState.value = _uiState.value.copy(showCalculator = true) }
    fun closeCalculator() { _uiState.value = _uiState.value.copy(showCalculator = false) }

    fun openCompass() { _uiState.value = _uiState.value.copy(showCompass = true) }
    fun closeCompass() { _uiState.value = _uiState.value.copy(showCompass = false) }

    fun openDiagnostics() { _uiState.value = _uiState.value.copy(showDiagnostics = true) }
    fun closeDiagnostics() { _uiState.value = _uiState.value.copy(showDiagnostics = false) }

    fun openDiagnosticOverlay() { _uiState.value = _uiState.value.copy(showDiagnosticOverlay = true, isDiagnosticOverlayMinimized = false) }
    fun closeDiagnosticOverlay() { _uiState.value = _uiState.value.copy(showDiagnosticOverlay = false) }
    fun toggleDiagnosticOverlay() {
        val next = !_uiState.value.showDiagnosticOverlay
        _uiState.value = _uiState.value.copy(showDiagnosticOverlay = next, isDiagnosticOverlayMinimized = false)
    }
    fun toggleDiagnosticOverlayMinimize() {
        _uiState.value = _uiState.value.copy(isDiagnosticOverlayMinimized = !_uiState.value.isDiagnosticOverlayMinimized)
    }
    fun optimizeMemory() {
        val freedMb = telemetryMonitor.optimizeMemory()
        deviceManager.speak("Memory optimized. Freed ${freedMb.toInt()} megabytes.")
    }

    fun openQrScanner() { _uiState.value = _uiState.value.copy(showQrScanner = true) }
    fun closeQrScanner() { _uiState.value = _uiState.value.copy(showQrScanner = false) }

    fun openAiChat() { _uiState.value = _uiState.value.copy(showAiChat = true) }
    fun closeAiChat() { _uiState.value = _uiState.value.copy(showAiChat = false) }

    fun openVoiceSettings() { _uiState.value = _uiState.value.copy(showVoiceSettings = true) }
    fun closeVoiceSettings() { _uiState.value = _uiState.value.copy(showVoiceSettings = false) }

    fun openBackgroundSettings() { _uiState.value = _uiState.value.copy(showBackgroundSettings = true) }
    fun closeBackgroundSettings() { _uiState.value = _uiState.value.copy(showBackgroundSettings = false) }

    fun toggleBackgroundMode(enable: Boolean? = null) {
        val current = JarvisBackgroundService.isRunning.value
        val target = enable ?: !current
        if (target) {
            JarvisBackgroundService.startService(getApplication())
            deviceManager.speak("Background surveillance protocol engaged. Core services, telemetry, and voice sensors will persist with screen off.")
        } else {
            JarvisBackgroundService.stopService(getApplication())
            deviceManager.speak("Background protocol deactivated.")
        }
    }

    fun testBackgroundAudio() {
        deviceManager.speak("Background audio synthesizer confirmed operational. JARVIS is standing by while device is locked.")
    }

    fun openAddSchedule() { _uiState.value = _uiState.value.copy(showAddSchedule = true) }
    fun closeAddSchedule() { _uiState.value = _uiState.value.copy(showAddSchedule = false) }

    fun openAddNote() { _uiState.value = _uiState.value.copy(showAddNote = true, showAllNotes = false) }
    fun closeAddNote() { _uiState.value = _uiState.value.copy(showAddNote = false) }

    fun openAllNotes() { _uiState.value = _uiState.value.copy(showAllNotes = true) }
    fun closeAllNotes() { _uiState.value = _uiState.value.copy(showAllNotes = false) }

    // Schedule actions
    fun toggleSchedule(item: ScheduleEntity) {
        viewModelScope.launch {
            repository.updateSchedule(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun addSchedule(title: String, time: String) {
        viewModelScope.launch {
            repository.insertSchedule(ScheduleEntity(title = title, time = time, isCompleted = false))
        }
    }

    // Note actions
    fun addNote(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.insertNote(NoteEntity(title = title, content = content, category = category))
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    // Chat actions
    fun sendMessage(text: String, sender: String) {
        viewModelScope.launch {
            repository.insertMessage(ChatMessageEntity(text = text, sender = sender))
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // Music Player
    fun togglePlayMusic() {
        val next = !_uiState.value.isPlayingMusic
        _uiState.value = _uiState.value.copy(isPlayingMusic = next)
        if (next) {
            deviceManager.speak("Playing ${_uiState.value.currentTrackTitle} by ${_uiState.value.currentTrackArtist}.")
        }
    }

    fun nextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % playlist.size
        val (t, a) = playlist[currentTrackIndex]
        _uiState.value = _uiState.value.copy(
            currentTrackTitle = t,
            currentTrackArtist = a,
            isPlayingMusic = true,
            trackProgressFraction = 0.05f,
            trackTimeElapsed = "00:10"
        )
        deviceManager.speak("Next track: $t.")
    }

    fun prevTrack() {
        currentTrackIndex = if (currentTrackIndex - 1 < 0) playlist.size - 1 else currentTrackIndex - 1
        val (t, a) = playlist[currentTrackIndex]
        _uiState.value = _uiState.value.copy(
            currentTrackTitle = t,
            currentTrackArtist = a,
            isPlayingMusic = true,
            trackProgressFraction = 0.05f,
            trackTimeElapsed = "00:05"
        )
        deviceManager.speak("Track: $t.")
    }

    // Quick Voice interaction
    fun executeVoiceQuery(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isListening = false,
                isSpeaking = true,
                statusGreeting = "Processing: \"$query\""
            )
            repository.insertMessage(ChatMessageEntity(text = query, sender = "user"))

            val (reply, _) = aiAssistant.processCommand(query, _uiState.value.userName)
            repository.insertMessage(ChatMessageEntity(text = reply, sender = "jarvis"))
            deviceManager.speak(reply)

            _uiState.value = _uiState.value.copy(
                isSpeaking = false,
                statusGreeting = reply.take(45) + if (reply.length > 45) "..." else ""
            )
        }
    }

    fun toggleListening() {
        val currentlyListening = _uiState.value.isListening
        if (!currentlyListening) {
            _uiState.value = _uiState.value.copy(
                isListening = true,
                statusGreeting = "Listening to audio feed..."
            )
            deviceManager.speak("Voice sensors active, Sir. Say command.")
        } else {
            _uiState.value = _uiState.value.copy(
                isListening = false,
                statusGreeting = "How can I help you today?"
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        telemetryMonitor.stopMonitoring()
        deviceManager.cleanup()
    }
}
