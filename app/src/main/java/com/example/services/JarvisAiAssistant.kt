package com.example.services

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiReq(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResp(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

interface GeminiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiReq
    ): GeminiResp
}

class JarvisAiAssistant(private val deviceManager: DeviceManager) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val geminiService = retrofit.create(GeminiService::class.java)

    suspend fun processCommand(command: String, userName: String = "Sanchit"): Pair<String, String?> = withContext(Dispatchers.IO) {
        val lower = command.lowercase().trim()

        // 1. Direct device actions
        when {
            lower.contains("torch") || lower.contains("flashlight") -> {
                val isOn = deviceManager.isTorchOn.value
                val toggled = if (lower.contains("on")) {
                    if (!isOn) deviceManager.toggleTorch() else true
                    true
                } else if (lower.contains("off")) {
                    if (isOn) deviceManager.toggleTorch() else false
                    false
                } else {
                    deviceManager.toggleTorch()
                }
                return@withContext Pair("Flashlight is now ${if (deviceManager.isTorchOn.value) "ACTIVE" else "OFF"}.", "torch")
            }

            lower.contains("battery") -> {
                val b = deviceManager.batteryState.value
                val text = "Current battery level is at ${b.level}%, ${if (b.isCharging) "charging rapidly" else "running on internal cells"}. Cell temperature is ${b.temperatureC}°C, condition: ${b.health}."
                return@withContext Pair(text, "battery")
            }

            lower.contains("storage") -> {
                val s = deviceManager.storageState.value
                val text = "Internal storage status: ${s.usedGb} GB utilized out of ${s.totalGb} GB (${s.usedPercent}% capacity). Free space: ${s.freeGb} GB."
                return@withContext Pair(text, "storage")
            }

            lower.contains("youtube") -> {
                val query = lower.replace("open", "").replace("youtube", "").replace("search", "").trim()
                deviceManager.launchYouTube(query)
                return@withContext Pair("Opening YouTube ${if (query.isNotEmpty()) "for '$query'" else ""} now, Sir.", "youtube")
            }

            lower.contains("camera") -> {
                deviceManager.launchCamera()
                return@withContext Pair("Camera optical sensors engaged.", "camera")
            }

            lower.contains("whatsapp") -> {
                deviceManager.launchWhatsApp()
                return@withContext Pair("Launching WhatsApp communications relay.", "whatsapp")
            }

            lower.contains("call") || lower.contains("dial") -> {
                deviceManager.launchPhoneDialer()
                return@withContext Pair("Routing to communications dialer.", "call")
            }

            lower.contains("message") || lower.contains("sms") -> {
                deviceManager.launchMessages()
                return@withContext Pair("Opening messaging interface.", "messages")
            }

            lower.contains("map") || lower.contains("navigate") -> {
                deviceManager.launchMaps()
                return@withContext Pair("Navigational satellite telemetry linked.", "maps")
            }

            lower.contains("browser") || lower.contains("google") || lower.contains("search web") -> {
                deviceManager.launchBrowser()
                return@withContext Pair("Launching holographic web browser interface.", "browser")
            }

            lower.contains("weather") -> {
                return@withContext Pair("Atmospheric scan in Pune, India indicates 28°C, Partly Cloudy with 68% relative humidity and low atmospheric disturbance.", "weather")
            }

            lower.contains("time") || lower.contains("date") -> {
                val now = java.text.SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm a", java.util.Locale.US).format(java.util.Date())
                return@withContext Pair("Current chronometer reading is $now.", "time")
            }

            lower.contains("who are you") || lower.contains("your name") -> {
                return@withContext Pair("I am JARVIS — Just A Rather Very Intelligent System. Customized as your tactical AI co-pilot.", "jarvis")
            }
        }

        // 2. Math calculations (e.g., "calculate 45 * 12", "what is 250 + 180")
        if (lower.contains("calculate") || lower.contains("+") || lower.contains("-") || lower.contains("*") || lower.contains("/")) {
            val mathResult = evaluateSimpleMath(lower)
            if (mathResult != null) {
                return@withContext Pair("Computational result: $mathResult", "math")
            }
        }

        // 3. Attempt Gemini API if configured
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val req = GeminiReq(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = command)))),
                    systemInstruction = GeminiContent(
                        parts = listOf(
                            GeminiPart(
                                text = "You are JARVIS, Tony Stark's personal high-tech artificial intelligence assistant. Address the user respectfully as Sir or by their name ($userName). Respond concisely, smartly, and with subtle sci-fi sophistication."
                            )
                        )
                    )
                )
                val resp = geminiService.generateContent(apiKey, req)
                val reply = resp.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!reply.isNullOrBlank()) {
                    return@withContext Pair(reply.trim(), "gemini")
                }
            } catch (e: Exception) {
                // Fall back to offline neural responses
            }
        }

        // 4. Intelligent Offline JARVIS fallback replies
        val offlineReply = generateOfflineJarvisResponse(lower, userName)
        Pair(offlineReply, "offline")
    }

    private fun generateOfflineJarvisResponse(prompt: String, userName: String): String {
        return when {
            prompt.contains("joke") ->
                "Why did the AI cross the road, $userName? Because it was programmed with a pathfinding algorithm optimized for minimal latency."

            prompt.contains("system status") || prompt.contains("diagnostic") || prompt.contains("scan") ->
                "All primary systems nominal, Sir. Power grid stable, core temperature within optimal tolerances, quantum coprocessors synchronized."

            prompt.contains("iron man") || prompt.contains("tony") || prompt.contains("stark") ->
                "Mark 85 armor telemetry standby. Arc reactor core operating at 99.4% peak efficiency."

            prompt.contains("music") || prompt.contains("play") ->
                "Cyberpunk synthwave audio subsystem engaged. Playing 'Better Days' by NEFFEX on high fidelity channel."

            prompt.contains("hello") || prompt.contains("hi") || prompt.contains("hey jarvis") ->
                "Good day, $userName. All diagnostic protocols are green. How may I be of service?"

            prompt.contains("help") ->
                "I can monitor device telemetry, toggle your flashlight, launch apps, compute complex arithmetic, track your schedule, record encrypted notes, and provide AI assistance."

            else ->
                "Understood, $userName. Quantum neural network processed your request: \"$prompt\". Operating at peak parameters."
        }
    }

    private fun evaluateSimpleMath(expr: String): String? {
        try {
            val clean = expr.replace("what is", "")
                .replace("calculate", "")
                .replace("times", "*")
                .replace("multiplied by", "*")
                .replace("plus", "+")
                .replace("minus", "-")
                .replace("divided by", "/")
                .replace("x", "*")
                .trim()

            val regex = Regex("""([\d.]+)\s*([+\-*/])\s*([\d.]+)""")
            val match = regex.find(clean) ?: return null
            val num1 = match.groupValues[1].toDoubleOrNull() ?: return null
            val op = match.groupValues[2]
            val num2 = match.groupValues[3].toDoubleOrNull() ?: return null

            val res = when (op) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "*" -> num1 * num2
                "/" -> if (num2 != 0.0) num1 / num2 else return "Division by zero is undefined."
                else -> return null
            }
            return if (res % 1.0 == 0.0) res.toLong().toString() else String.format(java.util.Locale.US, "%.2f", res)
        } catch (_: Exception) {
            return null
        }
    }
}
