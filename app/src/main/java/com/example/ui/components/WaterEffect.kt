package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data structure representing a physics-based liquid wave ripple propagating through the container.
 */
class FluidRipple(
    val id: Long,
    val origin: Offset,
    val birthTimeSeconds: Float,
    val maxRadius: Float = 550f,
    val speed: Float = 340f, // pixels per second
    val damping: Float = 1.35f, // exponential decay rate
    val wavelength: Float = 48f
)

/**
 * Data structure for buoyant bioluminescent micro-bubbles floating in the water column.
 */
class FluidDroplet(
    var xFraction: Float,
    var y: Float,
    val radius: Float,
    val buoyancySpeed: Float,
    val swayAmp: Float,
    val swayFreq: Float,
    val phase: Float,
    val alpha: Float,
    var impulseX: Float = 0f,
    var impulseY: Float = 0f
)

/**
 * High-graphics, liquid-motion physics container background for JARVIS.
 * Combines:
 * 1. Multi-harmonic sinusoidal fluid wave equations.
 * 2. Interactive touch/drag fluid ripple dispersion physics.
 * 3. Suspended bioluminescent fluid droplet particles with buoyancy & viscous damping.
 * 4. Animated underwater caustic light refraction bands.
 */
@Composable
fun FluidLiquidPhysicsContainer(
    modifier: Modifier = Modifier,
    baseWaveColor: Color = NeonCyan.copy(alpha = 0.09f),
    midWaveColor: Color = Color(0xFF0070F3).copy(alpha = 0.07f),
    deepWaveColor: Color = Color(0xFF00E5FF).copy(alpha = 0.05f),
    content: @Composable BoxScope.() -> Unit
) {
    val ripples = remember { mutableStateListOf<FluidRipple>() }
    var nextRippleId by remember { mutableLongStateOf(0L) }
    var currentTimeSeconds by remember { mutableFloatStateOf(0f) }

    // Initialize buoyant fluid droplets
    val droplets = remember {
        val list = mutableListOf<FluidDroplet>()
        val rng = Random(42)
        for (i in 0 until 24) {
            list.add(
                FluidDroplet(
                    xFraction = rng.nextFloat(),
                    y = rng.nextFloat() * 1800f,
                    radius = 1.8f + rng.nextFloat() * 2.8f,
                    buoyancySpeed = 22f + rng.nextFloat() * 32f,
                    swayAmp = 8f + rng.nextFloat() * 16f,
                    swayFreq = 0.8f + rng.nextFloat() * 1.2f,
                    phase = rng.nextFloat() * 6.28f,
                    alpha = 0.25f + rng.nextFloat() * 0.45f
                )
            )
        }
        list
    }

    // Physics frame loop: runs at native display refresh rate
    LaunchedEffect(Unit) {
        val startNanos = System.nanoTime()
        var lastNanos = startNanos
        while (true) {
            withFrameNanos { frameNanos ->
                val totalSeconds = (frameNanos - startNanos) / 1_000_000_000f
                val deltaSeconds = ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = frameNanos
                currentTimeSeconds = totalSeconds

                // Update and prune old ripples whose amplitude is decayed to near zero
                val it = ripples.iterator()
                while (it.hasNext()) {
                    val r = it.next()
                    val age = totalSeconds - r.birthTimeSeconds
                    if (age > 2.2f || (age * r.speed) > r.maxRadius) {
                        it.remove()
                    }
                }

                // Update droplet physics (buoyancy, sway, impulse decay)
                for (d in droplets) {
                    d.y -= d.buoyancySpeed * deltaSeconds
                    if (d.y < -30f) {
                        d.y = 2200f
                    }

                    // Dampen touch-induced impulses
                    d.impulseX *= (1f - 4.5f * deltaSeconds).coerceAtLeast(0f)
                    d.impulseY *= (1f - 4.5f * deltaSeconds).coerceAtLeast(0f)
                }
            }
        }
    }

    // Helper to spawn a new ripple when a touch event occurs
    val spawnRipple: (Offset) -> Unit = { touchPos ->
        // Limit max active ripples to maintain high performance
        if (ripples.size > 8) {
            ripples.removeAt(0)
        }
        val newRipple = FluidRipple(
            id = nextRippleId++,
            origin = touchPos,
            birthTimeSeconds = currentTimeSeconds
        )
        ripples.add(newRipple)

        // Apply physical push impulses to nearby droplets
        for (d in droplets) {
            // Rough screen pixel calculation for droplet
            val dx = d.xFraction * 1080f - touchPos.x
            val dy = d.y - touchPos.y
            val dist = hypot(dx, dy)
            if (dist < 260f && dist > 2f) {
                val force = (1f - dist / 260f) * 85f
                d.impulseX += (dx / dist) * force
                d.impulseY += (dy / dist) * force
            }
        }
    }

    // Smooth continuous background animations
    val infiniteTransition = rememberInfiniteTransition(label = "fluid_caustics_anim")
    val wavePhase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wp1"
    )
    val wavePhase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wp2"
    )
    val wavePhase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wp3"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Non-consuming touch listener: tracks touches for liquid ripples
                // without interrupting or blocking button clicks or LazyColumn scrolling!
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        if (event.type == PointerEventType.Press || event.type == PointerEventType.Move) {
                            event.changes.forEach { change ->
                                if (event.type == PointerEventType.Press) {
                                    spawnRipple(change.position)
                                }
                            }
                        }
                    }
                }
            }
    ) {
        // Fluid Canvas Layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Underwater Caustic Shimmer Bands
            drawCausticsShimmer(width, height, wavePhase1, wavePhase2)

            // 2. Multi-Harmonic Bottom Wave Undulations
            drawHarmonicWaves(
                width = width,
                height = height,
                phase1 = wavePhase1,
                phase2 = wavePhase2,
                phase3 = wavePhase3,
                baseColor = baseWaveColor,
                midColor = midWaveColor,
                deepColor = deepWaveColor
            )

            // 3. Buoyant Bioluminescent Droplets / Micro-bubbles
            drawBuoyantDroplets(
                droplets = droplets,
                width = width,
                timeSeconds = currentTimeSeconds
            )

            // 4. Interactive Fluid Ripples with Physical Dispersion
            drawFluidRipples(
                ripples = ripples,
                currentTimeSeconds = currentTimeSeconds
            )
        }

        // Dashboard UI content
        content()
    }
}

/**
 * Draws soft organic underwater caustic light streaks across the container.
 */
private fun DrawScope.drawCausticsShimmer(
    width: Float,
    height: Float,
    phase1: Float,
    phase2: Float
) {
    val causticAlpha = 0.028f
    val color1 = NeonCyan.copy(alpha = causticAlpha)
    val color2 = NeonBlue.copy(alpha = causticAlpha * 0.7f)

    // Moving diagonal caustic band 1
    val path1 = Path().apply {
        val yOffset1 = height * 0.35f + 40f * sin(phase1)
        moveTo(0f, yOffset1)
        cubicTo(
            width * 0.3f, yOffset1 + 80f * cos(phase2),
            width * 0.7f, yOffset1 - 60f * sin(phase1),
            width, yOffset1 + 30f * cos(phase1)
        )
        lineTo(width, yOffset1 + 120f)
        cubicTo(
            width * 0.65f, yOffset1 + 60f + 60f * sin(phase2),
            width * 0.35f, yOffset1 + 160f * cos(phase1),
            0f, yOffset1 + 90f
        )
        close()
    }
    drawPath(path1, brush = Brush.linearGradient(listOf(color1, color2, Color.Transparent)))

    // Moving diagonal caustic band 2
    val path2 = Path().apply {
        val yOffset2 = height * 0.62f + 50f * cos(phase2)
        moveTo(0f, yOffset2)
        cubicTo(
            width * 0.35f, yOffset2 - 70f * sin(phase1),
            width * 0.65f, yOffset2 + 50f * cos(phase2),
            width, yOffset2 - 20f * sin(phase2)
        )
        lineTo(width, yOffset2 + 100f)
        cubicTo(
            width * 0.7f, yOffset2 + 140f * sin(phase1),
            width * 0.3f, yOffset2 + 30f * cos(phase1),
            0f, yOffset2 + 80f
        )
        close()
    }
    drawPath(path2, brush = Brush.linearGradient(listOf(color2, color1, Color.Transparent)))
}

/**
 * Draws multi-harmonic sinusoidal liquid wave layers along the lower half of the screen.
 */
private fun DrawScope.drawHarmonicWaves(
    width: Float,
    height: Float,
    phase1: Float,
    phase2: Float,
    phase3: Float,
    baseColor: Color,
    midColor: Color,
    deepColor: Color
) {
    // Wave 3 (Deepest Layer, large slow wave)
    val path3 = Path().apply {
        moveTo(0f, height)
        val baseH3 = height * 0.84f
        val amp3 = 26f
        for (x in 0..width.toInt() step 8) {
            val progress = x / width
            val y = baseH3 + amp3 * sin(progress * 3.2f * Math.PI.toFloat() + phase3) +
                    10f * cos(progress * 6.4f * Math.PI.toFloat() - phase1)
            lineTo(x.toFloat(), y)
        }
        lineTo(width, height)
        close()
    }
    drawPath(
        path = path3,
        brush = Brush.verticalGradient(
            colors = listOf(deepColor, Color.Transparent),
            startY = height * 0.72f,
            endY = height
        ),
        style = Fill
    )

    // Wave 2 (Middle Layer)
    val path2 = Path().apply {
        moveTo(0f, height)
        val baseH2 = height * 0.89f
        val amp2 = 20f
        for (x in 0..width.toInt() step 8) {
            val progress = x / width
            val y = baseH2 + amp2 * sin(progress * 4.2f * Math.PI.toFloat() - phase2) +
                    8f * sin(progress * 8.4f * Math.PI.toFloat() + phase3)
            lineTo(x.toFloat(), y)
        }
        lineTo(width, height)
        close()
    }
    drawPath(
        path = path2,
        brush = Brush.verticalGradient(
            colors = listOf(midColor, Color.Transparent),
            startY = height * 0.80f,
            endY = height
        ),
        style = Fill
    )

    // Wave 1 (Foreground Surface Shimmer)
    val path1 = Path().apply {
        moveTo(0f, height)
        val baseH1 = height * 0.93f
        val amp1 = 15f
        for (x in 0..width.toInt() step 8) {
            val progress = x / width
            val y = baseH1 + amp1 * sin(progress * 5.4f * Math.PI.toFloat() + phase1) +
                    6f * cos(progress * 10.8f * Math.PI.toFloat() - phase2)
            lineTo(x.toFloat(), y)
        }
        lineTo(width, height)
        close()
    }
    drawPath(
        path = path1,
        brush = Brush.verticalGradient(
            colors = listOf(baseColor, Color.Transparent),
            startY = height * 0.86f,
            endY = height
        ),
        style = Fill
    )

    // Crest Highlight Line
    val crestPath = Path().apply {
        val baseH = height * 0.93f
        val amp1 = 15f
        for (x in 0..width.toInt() step 8) {
            val progress = x / width
            val y = baseH + amp1 * sin(progress * 5.4f * Math.PI.toFloat() + phase1) +
                    6f * cos(progress * 10.8f * Math.PI.toFloat() - phase2)
            if (x == 0) moveTo(0f, y) else lineTo(x.toFloat(), y)
        }
    }
    drawPath(
        path = crestPath,
        color = NeonCyanLight.copy(alpha = 0.22f),
        style = Stroke(width = 1.4f)
    )
}

/**
 * Draws floating bioluminescent fluid droplets that drift upwards with buoyant physics.
 */
private fun DrawScope.drawBuoyantDroplets(
    droplets: List<FluidDroplet>,
    width: Float,
    timeSeconds: Float
) {
    for (d in droplets) {
        val sway = d.swayAmp * sin(timeSeconds * d.swayFreq * 2 * Math.PI.toFloat() + d.phase)
        val posX = (d.xFraction * width) + sway + d.impulseX
        val posY = d.y + d.impulseY

        // Subtle squash & stretch from motion
        val speedFactor = (d.buoyancySpeed / 30f).coerceIn(0.8f, 1.4f)
        val rx = d.radius * (1f / speedFactor)
        val ry = d.radius * speedFactor

        // Soft outer glow
        drawCircle(
            color = NeonCyan.copy(alpha = d.alpha * 0.35f),
            radius = rx * 2.2f,
            center = Offset(posX, posY)
        )

        // Droplet Core
        drawOval(
            color = NeonCyanLight.copy(alpha = d.alpha),
            topLeft = Offset(posX - rx, posY - ry),
            size = Size(rx * 2, ry * 2)
        )
    }
}

/**
 * Draws interactive fluid ripples that propagate outwards with wave physics.
 */
private fun DrawScope.drawFluidRipples(
    ripples: List<FluidRipple>,
    currentTimeSeconds: Float
) {
    for (r in ripples) {
        val age = currentTimeSeconds - r.birthTimeSeconds
        if (age < 0f) continue

        // Radius expands over time
        val currentRadius = age * r.speed
        if (currentRadius <= 0f) continue

        // Exponential decay envelope
        val decay = exp(-r.damping * age)
        if (decay < 0.02f) continue

        // Primary wave crest ring
        val ringAlpha = (0.55f * decay).coerceIn(0f, 1f)
        drawCircle(
            color = NeonCyan.copy(alpha = ringAlpha),
            radius = currentRadius,
            center = r.origin,
            style = Stroke(width = (3.2f * decay).coerceAtLeast(0.8f), cap = StrokeCap.Round)
        )

        // Secondary inner refraction wave (spaced by wavelength)
        val innerRadius = currentRadius - r.wavelength
        if (innerRadius > 0f) {
            val innerAlpha = (0.35f * decay).coerceIn(0f, 1f)
            drawCircle(
                color = Color(0xFF0070F3).copy(alpha = innerAlpha),
                radius = innerRadius,
                center = r.origin,
                style = Stroke(width = (2.2f * decay).coerceAtLeast(0.6f), cap = StrokeCap.Round)
            )
        }

        // Third subtle trailing harmonic ripple
        val trailingRadius = currentRadius - r.wavelength * 1.8f
        if (trailingRadius > 0f) {
            val trailingAlpha = (0.18f * decay).coerceIn(0f, 1f)
            drawCircle(
                color = NeonCyanLight.copy(alpha = trailingAlpha),
                radius = trailingRadius,
                center = r.origin,
                style = Stroke(width = (1.4f * decay).coerceAtLeast(0.5f))
            )
        }
    }
}

/**
 * Legacy support for direct WaterWaveBackground if called standalone.
 */
@Composable
fun WaterWaveBackground(
    modifier: Modifier = Modifier,
    baseColor: Color = NeonCyan.copy(alpha = 0.08f),
    waveColor2: Color = Color(0xFF0070F3).copy(alpha = 0.06f),
    waveColor3: Color = Color(0xFF00E5FF).copy(alpha = 0.04f)
) {
    FluidLiquidPhysicsContainer(
        modifier = modifier,
        baseWaveColor = baseColor,
        midWaveColor = waveColor2,
        deepWaveColor = waveColor3
    ) {}
}

/**
 * Pulsing water ripple circle effect for buttons or center reactor.
 */
@Composable
fun WaterRipplePulse(
    modifier: Modifier = Modifier,
    color: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple_anim")
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        drawCircle(
            color = color.copy(alpha = alpha1),
            radius = radius * pulse1
        )
        drawCircle(
            color = color.copy(alpha = alpha2),
            radius = radius * pulse2
        )
    }
}

/**
 * Undulating liquid meniscus line for container headers and dividers.
 * Uses dynamic multi-frequency wave physics to produce flowing water surface animations.
 */
@Composable
fun LiquidMeniscusWave(
    modifier: Modifier = Modifier,
    heightDp: Dp = 8.dp,
    primaryColor: Color = NeonCyan,
    secondaryColor: Color = NeonBlue
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meniscus_physics_anim")
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mp1"
    )
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mp2"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp)
    ) {
        val w = size.width
        val h = size.height
        val midY = h * 0.5f

        val path = Path().apply {
            moveTo(0f, midY)
            for (x in 0..w.toInt() step 6) {
                val prog = x / w
                val y = midY + (h * 0.35f) * sin(prog * 6f * Math.PI.toFloat() + phase1) +
                        (h * 0.15f) * cos(prog * 12f * Math.PI.toFloat() - phase2)
                lineTo(x.toFloat(), y)
            }
        }

        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.1f),
                    primaryColor.copy(alpha = 0.85f),
                    secondaryColor.copy(alpha = 0.85f),
                    primaryColor.copy(alpha = 0.1f)
                )
            ),
            style = Stroke(width = 2.2f, cap = StrokeCap.Round)
        )
    }
}

