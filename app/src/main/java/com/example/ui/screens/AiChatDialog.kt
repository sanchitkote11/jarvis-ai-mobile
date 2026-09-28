package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ChatMessageEntity
import com.example.services.DeviceManager
import com.example.services.JarvisAiAssistant
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonBlueLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun AiChatDialog(
    messages: List<ChatMessageEntity>,
    aiAssistant: JarvisAiAssistant,
    deviceManager: DeviceManager,
    userName: String = "Sanchit",
    onSendMessage: (String, String) -> Unit, // text, sender
    onClearChat: () -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun submitQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty() || isThinking) return

        inputText = ""
        onSendMessage(trimmed, "user")
        isThinking = true

        scope.launch {
            val (reply, _) = aiAssistant.processCommand(trimmed, userName)
            onSendMessage(reply, "jarvis")
            isThinking = false
            deviceManager.speak(reply)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(22.dp))
                .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)), RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = CyberBackground
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "JARVIS NEURAL LINK",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "AI Core Online • Protocol Mark-85",
                                color = NeonCyanLight,
                                fontSize = 9.5.sp
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = onClearChat,
                            modifier = Modifier.size(32.dp).testTag("clear_chat_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear Chat",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp).testTag("close_chat_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Suggestion chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val suggestions = listOf(
                        "Turn on torch",
                        "Battery status",
                        "Atmospheric weather",
                        "Calculate 125 * 8",
                        "Tell me a joke",
                        "System diagnostic",
                        "Open YouTube"
                    )
                    items(suggestions) { chip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurface)
                                .border(BorderStroke(0.7.dp, CyberCardBorder), RoundedCornerShape(8.dp))
                                .clickable { submitQuery(chip) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = chip, color = TextCyan, fontSize = 9.5.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        val isUser = msg.sender == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (isUser) 14.dp else 2.dp,
                                            bottomEnd = if (isUser) 2.dp else 14.dp
                                        )
                                    )
                                    .background(
                                        if (isUser) NeonBlueLight.copy(alpha = 0.25f)
                                        else CyberSurfaceVariant.copy(alpha = 0.85f)
                                    )
                                    .border(
                                        BorderStroke(
                                            0.8.dp,
                                            if (isUser) NeonBlueLight.copy(alpha = 0.6f)
                                            else CyberCardBorder
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isUser) userName.uppercase() else "JARVIS",
                                            color = if (isUser) TextCyan else NeonCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        if (!isUser) {
                                            IconButton(
                                                onClick = { deviceManager.speak(msg.text) },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = "Speak",
                                                    tint = TextSecondary,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = msg.text,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    if (isThinking) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CyberSurface)
                                        .padding(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = NeonCyan,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Neural processors computing response...",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input"),
                        placeholder = {
                            Text(
                                text = "Ask JARVIS or issue command...",
                                color = TextSecondary,
                                fontSize = 11.5.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = NeonCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { submitQuery(inputText) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                            .testTag("ai_chat_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = CyberBackground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
