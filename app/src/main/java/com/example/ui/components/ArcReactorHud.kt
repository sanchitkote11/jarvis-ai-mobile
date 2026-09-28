package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactorHud(
    userName: String = "Sanchit",
    statusText: String = "How can I help you today?",
    isSpeaking: Boolean = false,
    onReactorClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hud_anim")

    // Slow continuous outer rotation
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_rot"
    )

    // Reverse inner rotation
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_rot"
    )

    // Core pulsing glow
    val coreGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    // Audio Waveform animation
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 600 else 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "audio_wave"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        ) {
            Text(
                text = "JARVIS",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Your personal\nAI assistant",
                color = TextCyan,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Center Arc Reactor
        Box(
            modifier = Modifier
                .size(136.dp)
                .testTag("arc_reactor_center")
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onReactorClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(136.dp)) {
                drawArcReactor(
                    outerRotation = outerRotation,
                    innerRotation = innerRotation,
                    coreGlow = coreGlow
                )
            }
        }

        // Right Info & Audio Waveform
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "\"Hello $userName,",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "$statusText\"",
                color = NeonCyanLight,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Animated Audio Waveform Bars
            Canvas(
                modifier = Modifier
                    .width(72.dp)
                    .height(22.dp)
            ) {
                drawAudioWaveBars(wavePhase, isSpeaking)
            }
        }
    }
}

private fun DrawScope.drawArcReactor(
    outerRotation: Float,
    innerRotation: Float,
    coreGlow: Float
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val maxRadius = size.minDimension / 2f - 4f

    // Ambient radial glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                NeonCyan.copy(alpha = 0.28f * coreGlow),
                NeonBlue.copy(alpha = 0.12f),
                Color.Transparent
            ),
            center = center,
            radius = maxRadius * 1.1f
        ),
        radius = maxRadius * 1.05f,
        center = center
    )

    // Outer Thin Orbit Ring
    drawCircle(
        color = NeonCyan.copy(alpha = 0.35f),
        radius = maxRadius,
        center = center,
        style = Stroke(width = 1.2f)
    )

    // Outer Rotating Segmented Arcs
    rotate(outerRotation, pivot = center) {
        val segmentCount = 6
        val sweepAngle = 38f
        val gapAngle = (360f - segmentCount * sweepAngle) / segmentCount
        val arcRadius = maxRadius - 5f
        for (i in 0 until segmentCount) {
            val startAngle = i * (sweepAngle + gapAngle)
            drawArc(
                color = NeonCyan,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                size = Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = 2.8f, cap = StrokeCap.Round)
            )

            // Outer indicator dot
            val dotRad = Math.toRadians((startAngle + sweepAngle / 2f).toDouble())
            val dotX = center.x + (arcRadius + 4f) * cos(dotRad).toFloat()
            val dotY = center.y + (arcRadius + 4f) * sin(dotRad).toFloat()
            drawCircle(
                color = NeonCyanLight,
                radius = 1.8f,
                center = Offset(dotX, dotY)
            )
        }
    }

    // Inner Counter-Rotating Ring with Tech Brackets
    rotate(innerRotation, pivot = center) {
        val innerRadius = maxRadius * 0.72f
        drawCircle(
            color = NeonBlue.copy(alpha = 0.5f),
            radius = innerRadius,
            center = center,
            style = Stroke(width = 1.5f)
        )

        // 8 small tick marks
        for (i in 0 until 8) {
            val angleDeg = i * 45f
            val rad = Math.toRadians(angleDeg.toDouble())
            val p1 = Offset(
                center.x + (innerRadius - 4f) * cos(rad).toFloat(),
                center.y + (innerRadius - 4f) * sin(rad).toFloat()
            )
            val p2 = Offset(
                center.x + (innerRadius + 4f) * cos(rad).toFloat(),
                center.y + (innerRadius + 4f) * sin(rad).toFloat()
            )
            drawLine(
                color = NeonCyanLight,
                start = p1,
                end = p2,
                strokeWidth = 2f
            )
        }
    }

    // Glowing Core Ring
    val coreRadius = maxRadius * 0.48f * coreGlow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                NeonCyanLight.copy(alpha = 0.75f),
                NeonCyan.copy(alpha = 0.4f),
                Color.Transparent
            ),
            center = center,
            radius = coreRadius
        ),
        radius = coreRadius,
        center = center
    )
    drawCircle(
        color = NeonCyan,
        radius = coreRadius * 0.85f,
        center = center,
        style = Stroke(width = 2.5f)
    )

    // Center Iron Man Helmet / Reactor Silhouette
    val helmetScale = coreRadius * 0.65f
    drawIronManHelmet(center, helmetScale)
}

private fun DrawScope.drawIronManHelmet(center: Offset, scale: Float) {
    val strokeColor = NeonCyanLight
    val fillColor = Color(0xFF041226)

    val path = Path().apply {
        // Forehead crest
        moveTo(center.x, center.y - scale)
        lineTo(center.x + scale * 0.55f, center.y - scale * 0.85f)
        lineTo(center.x + scale * 0.68f, center.y - scale * 0.35f)
        // Jaw / Cheeks
        lineTo(center.x + scale * 0.5f, center.y + scale * 0.45f)
        lineTo(center.x + scale * 0.28f, center.y + scale * 0.9f)
        // Chin
        lineTo(center.x - scale * 0.28f, center.y + scale * 0.9f)
        lineTo(center.x - scale * 0.5f, center.y + scale * 0.45f)
        lineTo(center.x - scale * 0.68f, center.y - scale * 0.35f)
        lineTo(center.x - scale * 0.55f, center.y - scale * 0.85f)
        close()
    }

    drawPath(path, color = fillColor)
    drawPath(path, color = strokeColor, style = Stroke(width = 1.8f))

    // Glowing Eyes
    val eyeY = center.y - scale * 0.1f
    val eyeWidth = scale * 0.28f
    val eyeHeight = scale * 0.08f
    val eyeOffset = scale * 0.32f

    // Left eye
    drawRoundRect(
        color = NeonCyanLight,
        topLeft = Offset(center.x - eyeOffset, eyeY),
        size = Size(eyeWidth, eyeHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
    )
    // Right eye
    drawRoundRect(
        color = NeonCyanLight,
        topLeft = Offset(center.x + eyeOffset - eyeWidth, eyeY),
        size = Size(eyeWidth, eyeHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
    )

    // Center mouth/chin grid line
    drawLine(
        color = NeonCyan.copy(alpha = 0.6f),
        start = Offset(center.x - scale * 0.2f, center.y + scale * 0.6f),
        end = Offset(center.x + scale * 0.2f, center.y + scale * 0.6f),
        strokeWidth = 1.4f
    )
}

private fun DrawScope.drawAudioWaveBars(phase: Float, isSpeaking: Boolean) {
    val barCount = 14
    val barWidth = 3.2f
    val spacing = (size.width - barCount * barWidth) / (barCount - 1)
    val maxHeight = size.height
    val midY = maxHeight / 2f

    for (i in 0 until barCount) {
        val multiplier = if (isSpeaking) 0.9f else 0.45f
        val waveVal = (sin(phase + i * 0.55f) + 1f) / 2f
        val height = (maxHeight * 0.2f + maxHeight * multiplier * waveVal).coerceIn(4f, maxHeight)

        val x = i * (barWidth + spacing)
        val y = midY - height / 2f

        drawLine(
            color = if (i % 2 == 0) NeonCyan else NeonCyanLight,
            start = Offset(x + barWidth / 2f, y),
            end = Offset(x + barWidth / 2f, y + height),
            strokeWidth = barWidth,
            cap = StrokeCap.Round
        )
    }
}
