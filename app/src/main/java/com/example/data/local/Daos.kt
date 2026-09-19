package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActivityLogEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.DocumentChunkEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.MessageEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserFlow(id: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET themePreference = :theme WHERE id = :userId")
    suspend fun updateTheme(userId: String, theme: String)

    @Query("UPDATE users SET modelPreference = :model, responseStyle = :responseStyle WHERE id = :userId")
    suspend fun updateAiPreferences(userId: String, model: String, responseStyle: String)

    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :userId")
    suspend fun updatePassword(userId: String, passwordHash: String)

    @Query("UPDATE users SET name = :name WHERE id = :userId")
    suspend fun updateProfile(userId: String, name: String)
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getConversations(userId: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET title = :title, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTitle(id: String, title: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: String)

    @Query("SELECT COUNT(*) FROM conversations WHERE userId = :userId")
    fun getConversationCount(userId: String): Flow<Int>
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun getMessages(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun getMessagesList(conversationId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: String)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE userId = :userId ORDER BY createdAt DESC")
    fun getDocuments(userId: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: String): DocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Query("UPDATE documents SET status = :status, summary = :summary, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDocumentStatus(id: String, status: String, summary: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: String)

    @Query("SELECT COUNT(*) FROM documents WHERE userId = :userId")
    fun getDocumentCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<DocumentChunkEntity>)

    @Query("SELECT * FROM document_chunks WHERE documentId = :documentId ORDER BY chunkIndex ASC")
    suspend fun getChunksForDocument(documentId: String): List<DocumentChunkEntity>

    @Query("DELETE FROM document_chunks WHERE documentId = :documentId")
    suspend fun deleteChunksForDocument(documentId: String)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTaskStatus(id: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId")
    fun getTaskCount(userId: String): Flow<Int>
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 50")
    fun getActivities(userId: String): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityLogEntity)

    @Query("SELECT COUNT(*) FROM activity_logs WHERE userId = :userId AND actionType = 'TOOL'")
    fun getToolUsageCount(userId: String): Flow<Int>
}
