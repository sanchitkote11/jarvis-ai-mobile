package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.NoteEntity
import com.example.data.ScheduleEntity
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddScheduleDialog(
    onAdd: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("06:00 PM") }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("NEW CALENDAR PROTOCOL", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).testTag("add_schedule_close_btn")) {
                        Icon(Icons.Default.Close, "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("TASK OR EVENT TITLE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Study AI Algorithms", color = TextSecondary, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("schedule_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("SCHEDULED TIME", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    placeholder = { Text("e.g. 5:00 PM", color = TextSecondary, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("schedule_time_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onAdd(title.trim(), time.trim())
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("save_schedule_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Icon(Icons.Default.Add, null, tint = CyberBackground, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SYNCHRONIZE TO CALENDAR", color = CyberBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddNoteDialog(
    onAdd: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Work") }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EditNote, null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ENCRYPTED TACTICAL NOTE", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).testTag("add_note_close_btn")) {
                        Icon(Icons.Default.Close, "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("SUBJECT", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Project Ideas", color = TextSecondary, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("note_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("NOTE CONTENTS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Enter detailed mission debrief...", color = TextSecondary, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("note_content_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onAdd(title.trim(), content.trim(), category)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("save_note_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Icon(Icons.Default.Add, null, tint = CyberBackground, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ENCRYPT & STORE NOTE", color = CyberBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ViewAllNotesDialog(
    notes: List<NoteEntity>,
    onDeleteNote: (Int) -> Unit,
    onAddNewNoteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = CyberBackground
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EditNote, null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ALL TACTICAL NOTES", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).testTag("view_notes_close_btn")) {
                        Icon(Icons.Default.Close, "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onAddNewNoteClick,
                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("create_note_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCardBorder)
                ) {
                    Icon(Icons.Default.Add, null, tint = TextCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WRITE NEW NOTE", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notes) { note ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberSurface)
                                .border(BorderStroke(0.8.dp, CyberCardBorder), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = note.title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    if (note.content.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = note.content, color = TextSecondary, fontSize = 10.5.sp, maxLines = 3)
                                    }
                                }
                                IconButton(
                                    onClick = { onDeleteNote(note.id) },
                                    modifier = Modifier.size(28.dp).testTag("delete_note_${note.id}")
                                ) {
                                    Icon(Icons.Default.Delete, "Delete", tint = NeonRed.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
