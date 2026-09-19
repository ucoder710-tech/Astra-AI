package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val email: String,
    val name: String,
    val passwordHash: String,
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val themePreference: String = "DARK",
    val modelPreference: String = "gemini-2.5-flash",
    val responseStyle: String = "Balanced"
)

@Entity(
    tableName = "conversations",
    indices = [
        Index(value = ["userId", "updatedAt"]),
        Index(value = ["userId"])
    ]
)
data class ConversationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["conversationId", "createdAt"]),
        Index(value = ["conversationId"])
    ]
)
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val role: String, // "user" or "assistant"
    val content: String,
    val model: String = "gemini-2.5-flash",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "documents",
    indices = [Index(value = ["userId", "createdAt"])]
)
data class DocumentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val filename: String,
    val mimeType: String,
    val fileSize: Long,
    val textContent: String,
    val summary: String = "",
    val status: String = "READY", // UPLOADING, PROCESSING, READY, FAILED
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "document_chunks",
    indices = [Index(value = ["documentId", "chunkIndex"])]
)
data class DocumentChunkEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val documentId: String,
    val chunkIndex: Int,
    val content: String
)

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["userId", "status", "dueDate"])]
)
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val description: String = "",
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH
    val status: String = "PENDING", // PENDING, IN_PROGRESS, COMPLETED
    val dueDate: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "activity_logs",
    indices = [Index(value = ["userId", "timestamp"])]
)
data class ActivityLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val actionType: String, // "CHAT", "DOCUMENT", "TOOL", "TASK"
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
