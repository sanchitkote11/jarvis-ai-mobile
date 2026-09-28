package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.services.DeviceManager
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun VoiceSettingsDialog(
    currentUserName: String,
    deviceManager: DeviceManager,
    onSaveUserName: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameInput by remember { mutableStateOf(currentUserName) }
    var speechRate by remember { mutableFloatStateOf(deviceManager.speechRate) }
    var speechPitch by remember { mutableFloatStateOf(deviceManager.speechPitch) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = CyberBackground
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VOICE & PROFILE SETTINGS",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("voice_settings_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User Name Field
                Text(text = "OPERATOR / USER CODENAME", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    modifier = Modifier.fillMaxWidth().testTag("user_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Speech Speed Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "SYNTHESIS RATE (SPEED)", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = String.format(Locale.US, "%.2fx", speechRate),
                        color = TextCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = speechRate,
                    onValueChange = {
                        speechRate = it
                        deviceManager.speechRate = it
                    },
                    valueRange = 0.6f..1.6f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = CyberCardBorder
                    ),
                    modifier = Modifier.testTag("speech_rate_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Speech Pitch Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "SYNTHESIS VOCAL PITCH", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = String.format(Locale.US, "%.2fx", speechPitch),
                        color = TextCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = speechPitch,
                    onValueChange = {
                        speechPitch = it
                        deviceManager.speechPitch = it
                    },
                    valueRange = 0.6f..1.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = CyberCardBorder
                    ),
                    modifier = Modifier.testTag("speech_pitch_slider")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Test Voice Button
                Button(
                    onClick = {
                        deviceManager.speak("Systems operational, $nameInput. Neural harmonics online.")
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("test_voice_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCardBorder)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = TextCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "TEST VOICE HARMONICS", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Save button
                Button(
                    onClick = {
                        onSaveUserName(nameInput.ifBlank { "Sanchit" })
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("save_voice_settings_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text(text = "APPLY PARAMETERS", color = CyberBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
