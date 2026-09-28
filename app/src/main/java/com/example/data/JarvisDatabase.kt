package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ScheduleEntity::class, NoteEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun jarvisDao(): JarvisDao

    companion object {
        @Volatile
        private var INSTANCE: JarvisDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): JarvisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JarvisDatabase::class.java,
                    "jarvis_master_database"
                )
                    .addCallback(JarvisDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class JarvisDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.jarvisDao())
                }
            }
        }

        suspend fun populateInitialData(dao: JarvisDao) {
            // Initial schedule items from the image
            dao.insertSchedule(ScheduleEntity(title = "Study", time = "5:00 PM", isCompleted = false))
            dao.insertSchedule(ScheduleEntity(title = "Game Time", time = "7:00 PM", isCompleted = false))
            dao.insertSchedule(ScheduleEntity(title = "Sleep", time = "10:30 PM", isCompleted = false))

            // Initial notes from the image
            dao.insertNote(NoteEntity(title = "Project Ideas", content = "Neural network interface, real-time voice synthesis and quantum dashboard HUD.", category = "Work"))
            dao.insertNote(NoteEntity(title = "Study Plan", content = "Review Jetpack Compose animation specs, Kotlin coroutines flow, and physics-based motion.", category = "Study"))
            dao.insertNote(NoteEntity(title = "Goals", content = "Complete Jarvis Android interface deployment and test system hardware diagnostic modules.", category = "Personal"))
            dao.insertNote(NoteEntity(title = "Remember...", content = "Check arc reactor cooling cycle and recharge core batteries before midnight.", category = "Daily"))

            // Initial Jarvis greeting
            dao.insertMessage(
                ChatMessageEntity(
                    sender = "jarvis",
                    text = "Hello Sir! JARVIS online and all neural subroutines operational. How may I assist your mission today?"
                )
            )
        }
    }
}
