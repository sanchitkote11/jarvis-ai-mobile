package com.example.data

import kotlinx.coroutines.flow.Flow

class JarvisRepository(private val dao: JarvisDao) {
    val allSchedule: Flow<List<ScheduleEntity>> = dao.getAllSchedule()
    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    val allMessages: Flow<List<ChatMessageEntity>> = dao.getAllMessages()

    suspend fun insertSchedule(item: ScheduleEntity) = dao.insertSchedule(item)
    suspend fun updateSchedule(item: ScheduleEntity) = dao.updateSchedule(item)
    suspend fun deleteSchedule(id: Int) = dao.deleteSchedule(id)

    suspend fun insertNote(note: NoteEntity) = dao.insertNote(note)
    suspend fun deleteNote(id: Int) = dao.deleteNote(id)

    suspend fun insertMessage(msg: ChatMessageEntity) = dao.insertMessage(msg)
    suspend fun clearChat() = dao.clearChat()
}
