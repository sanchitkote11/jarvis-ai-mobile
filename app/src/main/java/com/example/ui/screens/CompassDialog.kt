package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun CompassDialog(
    degrees: Float,
    onDismiss: () -> Unit
) {
    val animatedDegrees by animateFloatAsState(
        targetValue = degrees,
        animationSpec = spring(stiffness = 300f),
        label = "compass_rot"
    )

    val cardinal = when (((degrees + 22.5f) % 360 / 45f).toInt()) {
        0 -> "N"
        1 -> "NE"
        2 -> "E"
        3 -> "SE"
        4 -> "S"
        5 -> "SW"
        6 -> "W"
        7 -> "NW"
        else -> "N"
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = CyberBackground
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TACTICAL COMPASS HUD",
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("compass_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compass Dial Canvas
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .testTag("compass_dial"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(230.dp)) {
                        drawCompassHud(animatedDegrees)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${degrees.roundToInt()}°",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = cardinal,
                            color = if (cardinal == "N") NeonRed else NeonCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sensor Status Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "AZIMUTH", color = TextSecondary, fontSize = 9.sp)
                        Text(
                            text = String.format(Locale.US, "%.1f°", degrees),
                            color = TextCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MAGNETOMETER", color = TextSecondary, fontSize = 9.sp)
                        Text(
                            text = "CALIBRATED",
                            color = NeonCyanLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "STATUS", color = TextSecondary, fontSize = 9.sp)
                        Text(
                            text = "LOCK ACTIVE",
                            color = NeonAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCompassHud(degrees: Float) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = size.minDimension / 2f - 10f

    // Outer Ambient Glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(NeonCyan.copy(alpha = 0.15f), Color.Transparent),
            center = center,
            radius = radius * 1.1f
        ),
        radius = radius,
        center = center
    )

    // Outer Dial Ring
    drawCircle(
        color = NeonCyan.copy(alpha = 0.4f),
        radius = radius,
        center = center,
        style = Stroke(width = 1.5f)
    )

    // Rotating dial according to heading
    rotate(-degrees, pivot = center) {
        // Ticks for every 10 degrees
        for (i in 0 until 36) {
            val angleDeg = i * 10f
            val isMajor = angleDeg % 90f == 0f
            val isMid = angleDeg % 30f == 0f
            val tickLen = if (isMajor) 14f else if (isMid) 9f else 5f
            val rad = Math.toRadians(angleDeg.toDouble())

            val start = Offset(
                center.x + (radius - tickLen) * cos(rad).toFloat(),
                center.y + (radius - tickLen) * sin(rad).toFloat()
            )
            val end = Offset(
                center.x + radius * cos(rad).toFloat(),
                center.y + radius * sin(rad).toFloat()
            )

            drawLine(
                color = if (isMajor && angleDeg == 270f) NeonRed else if (isMajor) NeonCyan else NeonCyan.copy(alpha = 0.5f),
                start = start,
                end = end,
                strokeWidth = if (isMajor) 2.2f else 1.2f,
                cap = StrokeCap.Round
            )
        }

        // Draw North Needle (pointed up)
        val needlePathNorth = Path().apply {
            moveTo(center.x, center.y - radius * 0.75f)
            lineTo(center.x + 8f, center.y - 18f)
            lineTo(center.x - 8f, center.y - 18f)
            close()
        }
        drawPath(needlePathNorth, color = NeonRed)

        // Draw South Needle (pointed down)
        val needlePathSouth = Path().apply {
            moveTo(center.x, center.y + radius * 0.75f)
            lineTo(center.x + 8f, center.y + 18f)
            lineTo(center.x - 8f, center.y + 18f)
            close()
        }
        drawPath(needlePathSouth, color = NeonCyanLight.copy(alpha = 0.6f))
    }

    // Static Crosshairs
    drawLine(
        color = NeonCyan.copy(alpha = 0.3f),
        start = Offset(center.x, center.y - radius * 0.4f),
        end = Offset(center.x, center.y + radius * 0.4f),
        strokeWidth = 1f
    )
    drawLine(
        color = NeonCyan.copy(alpha = 0.3f),
        start = Offset(center.x - radius * 0.4f, center.y),
        end = Offset(center.x + radius * 0.4f, center.y),
        strokeWidth = 1f
    )

    // Center pivot ring
    drawCircle(
        color = NeonCyan,
        radius = 28f,
        center = center,
        style = Stroke(width = 1.5f)
    )
    drawCircle(
        color = CyberBackground,
        radius = 26f,
        center = center
    )
}
