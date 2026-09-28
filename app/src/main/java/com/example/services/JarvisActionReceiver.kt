package com.example.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class JarvisActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TOGGLE_TORCH = "com.example.action.TOGGLE_TORCH"
        const val ACTION_OPTIMIZE_RAM = "com.example.action.OPTIMIZE_RAM"
        const val ACTION_STOP_SERVICE = "com.example.action.STOP_SERVICE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when (intent.action) {
            ACTION_TOGGLE_TORCH -> {
                val dm = DeviceManager(context.applicationContext)
                dm.toggleTorch()
                JarvisBackgroundService.requestNotificationUpdate(context.applicationContext)
            }
            ACTION_OPTIMIZE_RAM -> {
                val before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                System.gc()
                val after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                val freedMb = ((before - after).coerceAtLeast(0L) / (1024 * 1024)).toFloat()

                val dm = DeviceManager(context.applicationContext)
                dm.speak("Background memory optimization complete. Freed ${freedMb.toInt()} megabytes.")
                JarvisBackgroundService.requestNotificationUpdate(context.applicationContext)
            }
            ACTION_STOP_SERVICE -> {
                JarvisBackgroundService.stopService(context.applicationContext)
            }
        }
    }
}
