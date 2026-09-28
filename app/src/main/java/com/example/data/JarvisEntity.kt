package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_items")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val time: String,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "jarvis"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
