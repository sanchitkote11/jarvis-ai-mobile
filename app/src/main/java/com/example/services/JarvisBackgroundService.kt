package com.example.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class JarvisBackgroundService : Service() {

    companion object {
        const val CHANNEL_ID = "jarvis_background_channel"
        const val NOTIFICATION_ID = 40401

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        private val _isScreenOff = MutableStateFlow(false)
        val isScreenOff: StateFlow<Boolean> = _isScreenOff.asStateFlow()

        fun startService(context: Context) {
            val intent = Intent(context, JarvisBackgroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, JarvisBackgroundService::class.java)
            context.stopService(intent)
        }

        fun requestNotificationUpdate(context: Context) {
            val intent = Intent(context, JarvisBackgroundService::class.java).apply {
                action = "ACTION_REFRESH_NOTIFICATION"
            }
            if (_isRunning.value) {
                try {
                    context.startService(intent)
                } catch (_: Exception) {}
            }
        }
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var telemetryJob: Job? = null
    private var telemetryMonitor: SystemTelemetryMonitor? = null

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    _isScreenOff.value = true
                    ensureWakeLock()
                    updateNotification(screenStatus = "Screen Off • Low-Power Surveillance Active")
                }
                Intent.ACTION_SCREEN_ON -> {
                    _isScreenOff.value = false
                    updateNotification(screenStatus = "Display Active • Live Telemetry Stream")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        _isRunning.value = true

        createNotificationChannel()
        acquireWakeLock()
        registerScreenReceiver()

        telemetryMonitor = SystemTelemetryMonitor(applicationContext, serviceScope)

        // Start in foreground
        val initialNotification = buildNotification("Initializing JARVIS background core...")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    initialNotification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(NOTIFICATION_ID, initialNotification)
            }
        } catch (_: Exception) {
            startForeground(NOTIFICATION_ID, initialNotification)
        }

        startTelemetryPolling()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "ACTION_REFRESH_NOTIFICATION") {
            updateNotification()
        }
        return START_STICKY
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "JARVIS:ScreenOffServiceWakeLock"
            )?.apply {
                setReferenceCounted(false)
                acquire(24 * 60 * 60 * 1000L) // Safe 24hr timeout limit
            }
        } catch (_: Exception) {}
    }

    private fun ensureWakeLock() {
        try {
            if (wakeLock?.isHeld != true) {
                wakeLock?.acquire(24 * 60 * 60 * 1000L)
            }
        } catch (_: Exception) {}
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (_: Exception) {}
        wakeLock = null
    }

    private fun registerScreenReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(screenReceiver, filter)
    }

    private fun unregisterScreenReceiver() {
        try {
            unregisterReceiver(screenReceiver)
        } catch (_: Exception) {}
    }

    private fun startTelemetryPolling() {
        telemetryJob?.cancel()
        telemetryJob = serviceScope.launch {
            while (isActive) {
                updateNotification()
                delay(3000L)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "JARVIS Background Core"
            val descriptionText = "Maintains JARVIS assistant and live telemetry when backgrounded or screen is off"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(customStatus: String? = null): Notification {
        val snapshot = telemetryMonitor?.telemetryState?.value
        val cpu = snapshot?.cpuUsagePercent?.toInt() ?: 28
        val ram = snapshot?.memoryUsagePercent?.toInt() ?: 55
        val bat = snapshot?.batteryPercent?.toInt() ?: 80
        val isScreenOffNow = _isScreenOff.value

        val stateLabel = if (isScreenOffNow) "SCREEN-OFF PERSISTENCE" else "BACKGROUND HUD"
        val statusLine = customStatus ?: "CPU: $cpu% | RAM: $ram% | Bat: $bat%"

        // Open App Intent
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 1: Wake JARVIS Voice (Can turn screen on and show above lockscreen)
        val wakeVoiceIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_WAKE_VOICE", true)
        }
        val wakeVoicePendingIntent = PendingIntent.getActivity(
            this,
            1,
            wakeVoiceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 2: Toggle Torch Flashlight (Direct receiver invocation from lockscreen)
        val torchIntent = Intent(this, JarvisActionReceiver::class.java).apply {
            action = JarvisActionReceiver.ACTION_TOGGLE_TORCH
        }
        val torchPendingIntent = PendingIntent.getBroadcast(
            this,
            2,
            torchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 3: Optimize RAM in Background
        val ramIntent = Intent(this, JarvisActionReceiver::class.java).apply {
            action = JarvisActionReceiver.ACTION_OPTIMIZE_RAM
        }
        val ramPendingIntent = PendingIntent.getBroadcast(
            this,
            3,
            ramIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 4: Stop Background Mode
        val stopIntent = Intent(this, JarvisActionReceiver::class.java).apply {
            action = JarvisActionReceiver.ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            4,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("JARVIS Core [$stateLabel]")
            .setContentText(statusLine)
            .setSubText("Status: Online")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(openPendingIntent)
            .addAction(android.R.drawable.ic_btn_speak_now, "Wake Voice", wakeVoicePendingIntent)
            .addAction(android.R.drawable.ic_menu_compass, "Torch", torchPendingIntent)
            .addAction(android.R.drawable.ic_menu_manage, "Optimize RAM", ramPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Deactivate", stopPendingIntent)
            .build()
    }

    private fun updateNotification(screenStatus: String? = null) {
        try {
            val notification = buildNotification(screenStatus)
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
        } catch (_: Exception) {}
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        _isRunning.value = false
        telemetryJob?.cancel()
        telemetryMonitor?.stopMonitoring()
        serviceScope.cancel()
        unregisterScreenReceiver()
        releaseWakeLock()
    }
}
