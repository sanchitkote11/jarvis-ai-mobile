package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun TelemetryLineChart(
    title: String,
    currentValue: String,
    unit: String,
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = NeonCyan,
    gradientColor: Color = NeonCyan,
    minValRange: Float = 0f,
    maxValRange: Float = 100f,
    detailSubtitle: String? = null,
    peakValue: Float? = null,
    lowValue: Float? = null,
    testTag: String = "telemetry_chart"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TelemetryChartAnim")

    // Pulsing head point animation
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HeadPulse"
    )

    // Holographic radar scanline sweep
    val scanPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ScanPhase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberSurface.copy(alpha = 0.75f))
            .border(1.dp, lineColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(12.dp)
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Chart Header: Title, Subtitle, Live Readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(lineColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            color = lineColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    if (detailSubtitle != null) {
                        Text(
                            text = detailSubtitle,
                            color = TextSecondary,
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(start = 14.dp, top = 2.dp)
                        )
                    }
                }

                // Current readout badge
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = currentValue,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = unit,
                        color = lineColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animated Line Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberBackground.copy(alpha = 0.85f))
                    .border(1.dp, CyberCardBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height
                    val paddingHorizontal = 12f
                    val paddingVertical = 10f

                    val usableWidth = width - (paddingHorizontal * 2)
                    val usableHeight = height - (paddingVertical * 2)

                    // Draw subtle grid lines (25%, 50%, 75%)
                    val gridDash = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    for (step in 1..3) {
                        val y = paddingVertical + usableHeight * (step / 4f)
                        drawLine(
                            color = CyberCardBorder.copy(alpha = 0.5f),
                            start = Offset(paddingHorizontal, y),
                            end = Offset(width - paddingHorizontal, y),
                            strokeWidth = 1f,
                            pathEffect = gridDash
                        )
                    }

                    // Vertical scanline effect
                    val scanX = paddingHorizontal + usableWidth * scanPhase
                    drawLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                lineColor.copy(alpha = 0.0f),
                                lineColor.copy(alpha = 0.35f),
                                lineColor.copy(alpha = 0.0f)
                            )
                        ),
                        start = Offset(scanX, paddingVertical),
                        end = Offset(scanX, height - paddingVertical),
                        strokeWidth = 2f
                    )

                    if (dataPoints.size >= 2) {
                        val range = (maxValRange - minValRange).coerceAtLeast(1f)
                        val stepX = usableWidth / (dataPoints.size - 1)

                        val points = dataPoints.mapIndexed { idx, value ->
                            val normalized = ((value - minValRange) / range).coerceIn(0f, 1f)
                            val x = paddingHorizontal + (idx * stepX)
                            val y = (height - paddingVertical) - (normalized * usableHeight)
                            Offset(x, y)
                        }

                        // Build smooth cubic bezier curve path
                        val strokePath = Path()
                        val fillPath = Path()

                        strokePath.moveTo(points.first().x, points.first().y)
                        fillPath.moveTo(points.first().x, height - paddingVertical)
                        fillPath.lineTo(points.first().x, points.first().y)

                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                            val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)
                            strokePath.cubicTo(
                                controlPoint1.x, controlPoint1.y,
                                controlPoint2.x, controlPoint2.y,
                                p1.x, p1.y
                            )
                            fillPath.cubicTo(
                                controlPoint1.x, controlPoint1.y,
                                controlPoint2.x, controlPoint2.y,
                                p1.x, p1.y
                            )
                        }

                        fillPath.lineTo(points.last().x, height - paddingVertical)
                        fillPath.close()

                        // 1. Draw glowing gradient fill under the curve
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    gradientColor.copy(alpha = 0.35f),
                                    gradientColor.copy(alpha = 0.05f)
                                ),
                                startY = paddingVertical,
                                endY = height - paddingVertical
                            )
                        )

                        // 2. Draw outer glow aura stroke
                        drawPath(
                            path = strokePath,
                            color = lineColor.copy(alpha = 0.25f),
                            style = Stroke(
                                width = 5.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // 3. Draw primary crisp neon curve line
                        drawPath(
                            path = strokePath,
                            color = lineColor,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // 4. Draw interactive pulsing head indicator at latest point
                        val lastPoint = points.last()

                        // Outer pulsing wave
                        drawCircle(
                            color = lineColor.copy(alpha = (1f - pulseFraction) * 0.6f),
                            radius = 4.dp.toPx() + (pulseFraction * 10.dp.toPx()),
                            center = lastPoint
                        )

                        // Solid neon core
                        drawCircle(
                            color = lineColor,
                            radius = 3.5.dp.toPx(),
                            center = lastPoint
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 1.5.dp.toPx(),
                            center = lastPoint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Footer info: Time markers and Peak/Low values
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "-24s",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "-12s",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "NOW",
                        color = lineColor,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (peakValue != null) {
                        Text(
                            text = "MAX: ${String.format(Locale.US, "%.0f", peakValue)}$unit",
                            color = TextSecondary,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    if (lowValue != null) {
                        Text(
                            text = "MIN: ${String.format(Locale.US, "%.0f", lowValue)}$unit",
                            color = TextSecondary,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact sparkline for mini HUD cards and widget badges
 */
@Composable
fun TelemetryMiniSparkline(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = NeonCyan,
    minValRange: Float = 0f,
    maxValRange: Float = 100f
) {
    Canvas(modifier = modifier) {
        if (dataPoints.size < 2) return@Canvas
        val width = size.width
        val height = size.height

        val range = (maxValRange - minValRange).coerceAtLeast(1f)
        val stepX = width / (dataPoints.size - 1)

        val path = Path()
        dataPoints.forEachIndexed { i, v ->
            val norm = ((v - minValRange) / range).coerceIn(0f, 1f)
            val x = i * stepX
            val y = height - (norm * height)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
