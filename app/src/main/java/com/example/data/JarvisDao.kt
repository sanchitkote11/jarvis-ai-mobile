package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JarvisDao {
    // Schedule
    @Query("SELECT * FROM schedule_items ORDER BY id ASC")
    fun getAllSchedule(): Flow<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(item: ScheduleEntity): Long

    @Update
    suspend fun updateSchedule(item: ScheduleEntity)

    @Query("DELETE FROM schedule_items WHERE id = :id")
    suspend fun deleteSchedule(id: Int)

    // Notes
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Int)

    // Chat
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}
