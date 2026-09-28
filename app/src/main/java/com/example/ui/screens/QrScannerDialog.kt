package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextSecondary

@Composable
fun QrScannerDialog(
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    var scannedResult by remember { mutableStateOf<String?>(null) }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QrCodeScanner, null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "OPTICAL MATRIX SCANNER",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).testTag("qr_close_btn")) {
                        Icon(Icons.Default.Close, "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scanner Viewport
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurface)
                        .testTag("scanner_viewport"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(220.dp)) {
                        val w = size.width
                        val h = size.height

                        // Grid lines
                        val gridCount = 5
                        for (i in 1..gridCount) {
                            val lineX = (w / (gridCount + 1)) * i
                            val lineY = (h / (gridCount + 1)) * i
                            drawLine(
                                color = NeonCyan.copy(alpha = 0.12f),
                                start = Offset(lineX, 0f),
                                end = Offset(lineX, h)
                            )
                            drawLine(
                                color = NeonCyan.copy(alpha = 0.12f),
                                start = Offset(0f, lineY),
                                end = Offset(w, lineY)
                            )
                        }

                        // Corner Brackets
                        val bracketLen = 28f
                        val strokeW = 3.5f

                        // Top-Left
                        drawLine(NeonCyan, Offset(8f, 8f), Offset(8f + bracketLen, 8f), strokeWidth = strokeW)
                        drawLine(NeonCyan, Offset(8f, 8f), Offset(8f, 8f + bracketLen), strokeWidth = strokeW)

                        // Top-Right
                        drawLine(NeonCyan, Offset(w - 8f, 8f), Offset(w - 8f - bracketLen, 8f), strokeWidth = strokeW)
                        drawLine(NeonCyan, Offset(w - 8f, 8f), Offset(w - 8f, 8f + bracketLen), strokeWidth = strokeW)

                        // Bottom-Left
                        drawLine(NeonCyan, Offset(8f, h - 8f), Offset(8f + bracketLen, h - 8f), strokeWidth = strokeW)
                        drawLine(NeonCyan, Offset(8f, h - 8f), Offset(8f, h - 8f - bracketLen), strokeWidth = strokeW)

                        // Bottom-Right
                        drawLine(NeonCyan, Offset(w - 8f, h - 8f), Offset(w - 8f - bracketLen, h - 8f), strokeWidth = strokeW)
                        drawLine(NeonCyan, Offset(w - 8f, h - 8f), Offset(w - 8f, h - 8f - bracketLen), strokeWidth = strokeW)

                        // Center target reticle
                        drawCircle(
                            color = NeonCyan.copy(alpha = 0.4f),
                            radius = 24f,
                            center = Offset(w / 2f, h / 2f),
                            style = Stroke(width = 1.5f)
                        )

                        // Animated Laser Line
                        val currentLaserY = 16f + (h - 32f) * laserY
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, NeonCyanLight, Color.Transparent)
                            ),
                            start = Offset(12f, currentLaserY),
                            end = Offset(w - 12f, currentLaserY),
                            strokeWidth = 3f
                        )
                    }

                    if (scannedResult == null) {
                        Text(
                            text = "ALIGN QR / BARCODE",
                            color = TextCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (scannedResult != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonGreen.copy(alpha = 0.15f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("TARGET DECODED:", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(scannedResult!!, color = Color.White, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    onClick = {
                        scannedResult = "JARVIS_SECURE_AUTH_KEY_#${(1000..9999).random()}"
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("simulate_scan_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text(
                        text = if (scannedResult == null) "TEST OPTICAL ACQUISITION" else "SCAN AGAIN",
                        color = CyberBackground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
