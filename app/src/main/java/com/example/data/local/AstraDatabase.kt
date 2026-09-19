package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ActivityLogEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.DocumentChunkEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.MessageEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        DocumentEntity::class,
        DocumentChunkEntity::class,
        TaskEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AstraDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun documentDao(): DocumentDao
    abstract fun taskDao(): TaskDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AstraDatabase? = null

        fun getDatabase(context: Context): AstraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AstraDatabase::class.java,
                    "astra_platform.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
