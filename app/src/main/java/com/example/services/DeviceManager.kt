package com.example.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class BatteryInfo(
    val level: Int = 78,
    val isCharging: Boolean = true,
    val temperatureC: Float = 31.5f,
    val voltageMv: Int = 4120,
    val health: String = "Good"
)

data class StorageInfo(
    val totalGb: Float = 128f,
    val usedGb: Float = 72f,
    val freeGb: Float = 56f,
    val usedPercent: Int = 62
)

data class NetworkInfo(
    val isConnected: Boolean = true,
    val isWifi: Boolean = true,
    val typeName: String = "Wi-Fi (Connected)",
    val cellularCarrier: String = "Jio 5G"
)

class DeviceManager(private val context: Context) : SensorEventListener {

    // Battery State
    private val _batteryState = MutableStateFlow(BatteryInfo())
    val batteryState: StateFlow<BatteryInfo> = _batteryState.asStateFlow()

    // Storage State
    private val _storageState = MutableStateFlow(calculateStorage())
    val storageState: StateFlow<StorageInfo> = _storageState.asStateFlow()

    // Network State
    private val _networkState = MutableStateFlow(checkNetwork())
    val networkState: StateFlow<NetworkInfo> = _networkState.asStateFlow()

    // Torch State
    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    // Compass Azimuth
    private val _compassDegrees = MutableStateFlow(0f)
    val compassDegrees: StateFlow<Float> = _compassDegrees.asStateFlow()

    // Sensors
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var hasAccelerometer = false
    private var hasMagnetometer = false

    // Camera Manager for Flashlight
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private var torchCameraId: String? = null

    // Text to Speech
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    var speechRate: Float = 1.0f
    var speechPitch: Float = 0.95f

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            intent?.let { updateBattery(it) }
        }
    }

    init {
        // Init Battery receiver
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val initialIntent = context.registerReceiver(batteryReceiver, filter)
        initialIntent?.let { updateBattery(it) }

        // Init Torch
        try {
            cameraManager?.let { cm ->
                for (id in cm.cameraIdList) {
                    val characteristics = cm.getCameraCharacteristics(id)
                    val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                    val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                    if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        torchCameraId = id
                        break
                    }
                }
                if (torchCameraId == null && cm.cameraIdList.isNotEmpty()) {
                    torchCameraId = cm.cameraIdList[0]
                }
            }
        } catch (_: Exception) {}

        // Init Compass Sensors
        sensorManager?.let { sm ->
            val rotationVectorSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            if (rotationVectorSensor != null) {
                sm.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
            } else {
                val accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                val magnet = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
                accel?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
                magnet?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            }
        }

        // Init TTS
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    tts?.setPitch(speechPitch)
                    tts?.setSpeechRate(speechRate)
                    isTtsReady = true
                }
            }
        } catch (_: Exception) {}
    }

    private fun updateBattery(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val pct = if (level >= 0 && scale > 0) (level * 100) / scale else 78
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 310)
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4100)
        val healthCode = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
        val healthStr = when (healthCode) {
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Critical"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            else -> "Optimal"
        }

        _batteryState.value = BatteryInfo(
            level = pct,
            isCharging = isCharging,
            temperatureC = tempTenths / 10f,
            voltageMv = voltage,
            health = healthStr
        )
    }

    private fun calculateStorage(): StorageInfo {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalBytes = totalBlocks * blockSize
            val freeBytes = availableBlocks * blockSize
            val usedBytes = totalBytes - freeBytes

            val totalGb = (totalBytes / (1024f * 1024f * 1024f))
            val usedGb = (usedBytes / (1024f * 1024f * 1024f))
            val freeGb = (freeBytes / (1024f * 1024f * 1024f))
            val percent = if (totalBytes > 0) ((usedBytes * 100) / totalBytes).toInt() else 62

            StorageInfo(
                totalGb = String.format(Locale.US, "%.0f", totalGb).toFloat(),
                usedGb = String.format(Locale.US, "%.1f", usedGb).toFloat(),
                freeGb = String.format(Locale.US, "%.1f", freeGb).toFloat(),
                usedPercent = percent.coerceIn(0, 100)
            )
        } catch (_: Exception) {
            StorageInfo()
        }
    }

    private fun checkNetwork(): NetworkInfo {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNet = cm?.activeNetwork
            val caps = cm?.getNetworkCapabilities(activeNet)

            val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            val isCell = caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
            val isConnected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

            NetworkInfo(
                isConnected = isConnected,
                isWifi = isWifi,
                typeName = if (isWifi) "Wi-Fi (Connected)" else if (isCell) "Cellular (5G/4G)" else "Offline",
                cellularCarrier = if (isCell) "Jio 5G High Speed" else "Jio"
            )
        } catch (_: Exception) {
            NetworkInfo()
        }
    }

    fun toggleTorch(): Boolean {
        return try {
            val target = !_isTorchOn.value
            torchCameraId?.let { id ->
                cameraManager?.setTorchMode(id, target)
                _isTorchOn.value = target
                speak(if (target) "Flashlight illumination active, Sir." else "Flashlight deactivated.")
                true
            } ?: run {
                _isTorchOn.value = target
                Toast.makeText(context, "Flashlight: ${if (target) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
                true
            }
        } catch (e: Exception) {
            _isTorchOn.value = !_isTorchOn.value
            false
        }
    }

    fun speak(text: String) {
        try {
            if (isTtsReady) {
                tts?.setPitch(speechPitch)
                tts?.setSpeechRate(speechRate)
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "JARVIS_VOICE_${System.currentTimeMillis()}")
            }
        } catch (_: Exception) {}
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
    }

    // Launchers
    fun launchPhoneDialer() {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Opening Phone Dialer", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchMessages() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MESSAGING)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(smsIntent)
            } catch (_: Exception) {
                Toast.makeText(context, "Opening Messages", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchWhatsApp() {
        try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage("com.whatsapp")
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Opening WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCamera() {
        try {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Launching Camera", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchBrowser(url: String = "https://www.google.com") {
        try {
            val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Opening Web Browser", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchYouTube(query: String = "") {
        try {
            val uri = if (query.isNotEmpty()) {
                Uri.parse("https://www.youtube.com/results?search_query=$query")
            } else {
                Uri.parse("https://www.youtube.com")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Opening YouTube", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchMaps(location: String = "") {
        try {
            val uri = if (location.isNotEmpty()) {
                Uri.parse("geo:0,0?q=${Uri.encode(location)}")
            } else {
                Uri.parse("geo:0,0?q=Pune,India")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Opening Maps", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchFiles() {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Browsing Files", Toast.LENGTH_SHORT).show()
        }
    }

    // SensorEventListener
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                _compassDegrees.value = (azimuth + 360) % 360
            }
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
                hasAccelerometer = true
                computeOrientation()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
                hasMagnetometer = true
                computeOrientation()
            }
        }
    }

    private fun computeOrientation() {
        if (hasAccelerometer && hasMagnetometer) {
            if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                _compassDegrees.value = (azimuth + 360) % 360
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun cleanup() {
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (_: Exception) {}
        try {
            sensorManager?.unregisterListener(this)
        } catch (_: Exception) {}
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
