package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("JARVIS", appName)
  }

  @Test
  fun `telemetry monitor provides initial history and metrics`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val monitor = com.example.services.SystemTelemetryMonitor(
      context = context,
      scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
    )

    val snapshot = monitor.telemetryState.value
    org.junit.Assert.assertNotNull(snapshot)
    org.junit.Assert.assertTrue("CPU history should not be empty", snapshot.cpuHistory.isNotEmpty())
    org.junit.Assert.assertTrue("Memory history should not be empty", snapshot.memoryHistory.isNotEmpty())
    org.junit.Assert.assertTrue("Battery history should not be empty", snapshot.batteryHistory.isNotEmpty())
    org.junit.Assert.assertTrue("Available cores should be >= 1", snapshot.availableCores >= 1)

    monitor.updateBatteryDetails(tempC = 33.5f, voltageMv = 4200, isCharging = true)
    val updated = monitor.telemetryState.value
    assertEquals(33.5f, updated.batteryTempC, 0.01f)
    assertEquals(4200, updated.batteryVoltageMv)
    org.junit.Assert.assertTrue(updated.isCharging)

    monitor.stopMonitoring()
  }

  @Test
  fun `background service lifecycle and action receiver`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    
    // Test broadcast receiver with optimize ram
    val receiver = com.example.services.JarvisActionReceiver()
    val intent = android.content.Intent(com.example.services.JarvisActionReceiver.ACTION_OPTIMIZE_RAM)
    receiver.onReceive(context, intent)

    // Test background service state flows
    org.junit.Assert.assertNotNull(com.example.services.JarvisBackgroundService.isRunning)
    org.junit.Assert.assertNotNull(com.example.services.JarvisBackgroundService.isScreenOff)
  }
}
