package com.example.data.repository

import com.example.ai.AIProvider
import com.example.ai.ChatMessage
import com.example.ai.ExtractedTask
import com.example.ai.RagAnswer
import com.example.data.local.AstraDatabase
import com.example.data.model.ActivityLogEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.DocumentChunkEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.MessageEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

data class DashboardMetrics(
    val totalConversations: Int = 0,
    val totalDocuments: Int = 0,
    val totalTasks: Int = 0,
    val totalToolUsages: Int = 0
)

class AstraRepository(
    private val db: AstraDatabase,
    private val aiProvider: AIProvider
) {
    private val userDao = db.userDao()
    private val conversationDao = db.conversationDao()
    private val messageDao = db.messageDao()
    private val documentDao = db.documentDao()
    private val taskDao = db.taskDao()
    private val activityDao = db.activityDao()

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // --- Authentication ---
    suspend fun register(name: String, email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || password.length < 4 || name.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter valid name, email and password (at least 4 chars)"))
        }
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return@withContext Result.failure(IllegalStateException("An account with this email already exists"))
        }

        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            email = cleanEmail,
            name = name.trim(),
            passwordHash = hashPassword(password)
        )
        userDao.insertUser(newUser)
        logActivity(newUser.id, "USER", "Account Registered", "Welcome to Astra")
        seedDefaultDataIfNew(newUser.id)
        Result.success(newUser)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(cleanEmail)
            ?: return@withContext Result.failure(IllegalArgumentException("Invalid email or password"))

        if (user.passwordHash != hashPassword(password)) {
            return@withContext Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        logActivity(user.id, "USER", "User Login", "Signed in successfully")
        Result.success(user)
    }

    fun getUserFlow(userId: String): Flow<UserEntity?> = userDao.getUserFlow(userId)

    suspend fun updateTheme(userId: String, theme: String) = withContext(Dispatchers.IO) {
        userDao.updateTheme(userId, theme)
    }

    suspend fun updateAiPreferences(userId: String, model: String, style: String) = withContext(Dispatchers.IO) {
        userDao.updateAiPreferences(userId, model, style)
    }

    suspend fun updateProfile(userId: String, name: String) = withContext(Dispatchers.IO) {
        userDao.updateProfile(userId, name)
    }

    suspend fun updatePassword(userId: String, oldPass: String, newPass: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.passwordHash != hashPassword(oldPass)) {
            return@withContext Result.failure(IllegalArgumentException("Incorrect current password"))
        }
        userDao.updatePassword(userId, hashPassword(newPass))
        Result.success(Unit)
    }

    // --- Metrics ---
    fun getDashboardMetrics(userId: String): Flow<DashboardMetrics> {
        return combine(
            conversationDao.getConversationCount(userId),
            documentDao.getDocumentCount(userId),
            taskDao.getTaskCount(userId),
            activityDao.getToolUsageCount(userId)
        ) { convs, docs, tasks, tools ->
            DashboardMetrics(
                totalConversations = convs,
                totalDocuments = docs,
                totalTasks = tasks,
                totalToolUsages = tools
            )
        }
    }

    // --- Conversations & Messages ---
    fun getConversations(userId: String): Flow<List<ConversationEntity>> =
        conversationDao.getConversations(userId)

    suspend fun createConversation(userId: String, title: String): ConversationEntity = withContext(Dispatchers.IO) {
        val conv = ConversationEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = title.ifBlank { "New Conversation" }
        )
        conversationDao.insertConversation(conv)
        conv
    }

    suspend fun deleteConversation(id: String) = withContext(Dispatchers.IO) {
        messageDao.deleteMessagesForConversation(id)
        conversationDao.deleteConversationById(id)
    }

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
        messageDao.getMessages(conversationId)

    suspend fun sendMessage(
        userId: String,
        conversationId: String,
        userContent: String,
        model: String,
        responseStyle: String = "Balanced",
        onChunk: (String) -> Unit
    ): Result<MessageEntity> = withContext(Dispatchers.IO) {
        // Save user message
        val userMsg = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = "user",
            content = userContent,
            model = model
        )
        messageDao.insertMessage(userMsg)

        // Update conversation title if first message
        val historyList = messageDao.getMessagesList(conversationId)
        if (historyList.size <= 2) {
            val shortTitle = if (userContent.length > 28) userContent.take(28) + "…" else userContent
            conversationDao.updateTitle(conversationId, shortTitle)
        }

        // Prepare context
        val chatHistory = historyList.map {
            ChatMessage(role = it.role, content = it.content)
        }

        val styleInstruction = when (responseStyle) {
            "Concise" -> "Keep your responses brief, highly direct, and succinct with bullet points where appropriate."
            "Detailed" -> "Provide comprehensive, deeply explanatory responses with nuanced context and detailed markdown."
            else -> "Provide balanced, informative, structured responses with clear formatting and markdown."
        }

        val systemPrompt = "You are Astra, an intelligent and helpful personal productivity assistant. $styleInstruction"

        val streamResult = aiProvider.streamChat(
            history = chatHistory.dropLast(1),
            latestMessage = userContent,
            systemInstruction = systemPrompt,
            modelOverride = model,
            onChunk = onChunk
        )

        return@withContext streamResult.fold(
            onSuccess = { fullContent ->
                val assistantMsg = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    role = "assistant",
                    content = fullContent,
                    model = model
                )
                messageDao.insertMessage(assistantMsg)
                logActivity(userId, "CHAT", "Conversation Message", "Exchanged prompt with Astra ($model)")
                Result.success(assistantMsg)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    // --- Document Management & RAG ---
    fun getDocuments(userId: String): Flow<List<DocumentEntity>> =
        documentDao.getDocuments(userId)

    suspend fun uploadDocument(
        userId: String,
        filename: String,
        mimeType: String,
        content: String
    ): Result<DocumentEntity> = withContext(Dispatchers.IO) {
        if (content.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Document content cannot be empty"))
        }

        val docId = UUID.randomUUID().toString()
        val doc = DocumentEntity(
            id = docId,
            userId = userId,
            filename = filename.ifBlank { "Untitled Document.txt" },
            mimeType = mimeType,
            fileSize = content.toByteArray().size.toLong(),
            textContent = content,
            status = "PROCESSING"
        )
        documentDao.insertDocument(doc)

        // Chunking text (split into ~500 char paragraphs)
        val rawChunks = content.split("\n\n").filter { it.isNotBlank() }
        val chunkEntities = rawChunks.mapIndexed { index, text ->
            DocumentChunkEntity(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                chunkIndex = index,
                content = text.trim()
            )
        }
        documentDao.insertChunks(chunkEntities)

        // Generate summary through AI
        val summaryResult = aiProvider.generateContent(
            prompt = "Provide a clean 2-sentence executive summary of this document:\n\n${content.take(1500)}",
            systemInstruction = "You are Astra Document Processor. Return only the summary text without preamble."
        )

        val finalSummary = summaryResult.getOrDefault("Document processed and indexed into ${chunkEntities.size} chunks.")
        documentDao.updateDocumentStatus(docId, "READY", finalSummary)

        logActivity(userId, "DOCUMENT", "Document Processed", "Uploaded '$filename' (${chunkEntities.size} chunks)")
        Result.success(doc.copy(status = "READY", summary = finalSummary))
    }

    suspend fun deleteDocument(docId: String) = withContext(Dispatchers.IO) {
        documentDao.deleteChunksForDocument(docId)
        documentDao.deleteDocumentById(docId)
    }

    suspend fun queryDocumentRag(docId: String, query: String): Result<RagAnswer> = withContext(Dispatchers.IO) {
        val doc = documentDao.getDocumentById(docId)
            ?: return@withContext Result.failure(Exception("Document not found"))
        val chunks = documentDao.getChunksForDocument(docId)
        if (chunks.isEmpty()) {
            return@withContext Result.failure(Exception("No readable chunks found for document"))
        }

        // Match most relevant chunks (simple keyword/relevance match + top chunks)
        val queryTokens = query.lowercase().split(" ", ",", "?").filter { it.length > 2 }
        val scoredChunks = chunks.map { chunk ->
            val score = queryTokens.count { token -> chunk.content.lowercase().contains(token) }
            chunk to score
        }.sortedByDescending { it.second }

        val topChunks = scoredChunks.take(4).map { it.first.content }
        val ragResult = aiProvider.answerDocumentQuery(doc.filename, topChunks, query)

        ragResult.onSuccess {
            logActivity(doc.userId, "DOCUMENT", "Document Q&A", "Asked Astra about '${doc.filename}'")
        }
        ragResult
    }

    suspend fun performDocumentAction(
        docId: String,
        actionType: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val doc = documentDao.getDocumentById(docId)
            ?: return@withContext Result.failure(Exception("Document not found"))

        val excerpt = doc.textContent.take(3000)
        val prompt = when (actionType) {
            "SUMMARIZE" -> "Generate a comprehensive structured summary with bullet points of this document:\n\n$excerpt"
            "KEY_POINTS" -> "Extract the top 5-7 most critical insights and takeaways from this document:\n\n$excerpt"
            "QUESTIONS" -> "Generate 5 study and review questions based on this document with model answers:\n\n$excerpt"
            "MCQS" -> "Generate 4 multiple choice questions (with 4 options A-D and the correct answer indicated) based on this document:\n\n$excerpt"
            else -> "Analyze this document:\n\n$excerpt"
        }

        val result = aiProvider.generateContent(
            prompt = prompt,
            systemInstruction = "You are Astra's Document Intelligence Analyst. Provide clear, well-formatted Markdown."
        )
        result.onSuccess {
            logActivity(doc.userId, "DOCUMENT", "Document Analysis ($actionType)", "Analyzed '${doc.filename}'")
        }
        result
    }

    // --- AI Tools ---
    suspend fun executeAiTool(
        userId: String,
        toolName: String,
        prompt: String,
        systemInstruction: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val result = aiProvider.generateContent(prompt, systemInstruction)
        result.onSuccess {
            logActivity(userId, "TOOL", "AI Tool: $toolName", "Executed $toolName with Gemini")
        }
        result
    }

    // --- Task Management ---
    fun getTasks(userId: String): Flow<List<TaskEntity>> =
        taskDao.getTasks(userId)

    suspend fun createTask(
        userId: String,
        title: String,
        description: String = "",
        priority: String = "MEDIUM",
        dueDate: String = ""
    ): TaskEntity = withContext(Dispatchers.IO) {
        val task = TaskEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = title.trim(),
            description = description.trim(),
            priority = priority.uppercase(),
            status = "PENDING",
            dueDate = dueDate.trim()
        )
        taskDao.insertTask(task)
        logActivity(userId, "TASK", "Task Created", "Created task: '${task.title}'")
        task
    }

    suspend fun updateTaskStatus(taskId: String, status: String) = withContext(Dispatchers.IO) {
        taskDao.updateTaskStatus(taskId, status)
    }

    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTask(taskId: String) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun parseNaturalLanguageTask(text: String): Result<ExtractedTask> {
        return aiProvider.extractTaskFromNaturalLanguage(text)
    }

    // --- Activity Logs ---
    fun getActivities(userId: String): Flow<List<ActivityLogEntity>> =
        activityDao.getActivities(userId)

    private suspend fun logActivity(userId: String, actionType: String, title: String, description: String) {
        val act = ActivityLogEntity(
            userId = userId,
            actionType = actionType,
            title = title,
            description = description
        )
        activityDao.insertActivity(act)
    }

    // --- Default Seed Data for New Users ---
    private suspend fun seedDefaultDataIfNew(userId: String) {
        // Welcome Conversation
        val welcomeConv = ConversationEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = "Welcome to Astra"
        )
        conversationDao.insertConversation(welcomeConv)

        messageDao.insertMessage(
            MessageEntity(
                conversationId = welcomeConv.id,
                role = "assistant",
                content = "Greetings! I am **Astra**, your intelligent personal assistant.\n\nHere is what I can help you achieve:\n- 💬 **Live AI Chat**: Real streaming responses powered by Google Gemini.\n- 📄 **Document RAG**: Upload PDFs, DOCX, or text files and query them with grounded source citations.\n- ⚡ **Productivity Tools**: Translation, grammar editing, email and content drafting, coding help, and MCQ generation.\n- 📋 **Smart Task Management**: Natural language task capture with instant confirmation."
            )
        )

        // Default Sample Document
        val sampleDocId = UUID.randomUUID().toString()
        val docText = """
Astra Architecture Overview
Astra is built as a high-integrity, modular AI SaaS assistant. It enforces complete user data isolation, local persistence via Room, and real-time streaming intelligence via Google Gemini API.

Key Subsystems:
1. Multi-User Isolation: Every conversation, document, and task is strictly scoped to the active authenticated user ID.
2. Aurora Design Tokens: Strict adherence to high-contrast dark and light theme tokens, featuring subtle hairline borders (#232838) and the signature Aurora gradient (Indigo -> Violet -> Cyan).
3. Retrieval-Augmented Generation (RAG): Files are indexed into searchable chunks with citation references.
4. Natural Language Task Processing: Plain English statements like 'Remind me to submit project by Friday' are structured into actionable tasks requiring user verification before commit.
        """.trimIndent()

        documentDao.insertDocument(
            DocumentEntity(
                id = sampleDocId,
                userId = userId,
                filename = "Astra_Platform_Spec.txt",
                mimeType = "text/plain",
                fileSize = docText.toByteArray().size.toLong(),
                textContent = docText,
                summary = "Overview of Astra's architecture, security model, and Aurora styling tokens.",
                status = "READY"
            )
        )

        val chunks = docText.split("\n\n").mapIndexed { idx, c ->
            DocumentChunkEntity(
                documentId = sampleDocId,
                chunkIndex = idx,
                content = c
            )
        }
        documentDao.insertChunks(chunks)

        // Sample Tasks
        taskDao.insertTask(
            TaskEntity(
                userId = userId,
                title = "Explore Astra AI Chat",
                description = "Try asking Astra a question or requesting code generation.",
                priority = "HIGH",
                status = "IN_PROGRESS",
                dueDate = "Today"
            )
        )
        taskDao.insertTask(
            TaskEntity(
                userId = userId,
                title = "Try Document Q&A (RAG)",
                description = "Ask questions regarding the Astra Platform Spec document.",
                priority = "MEDIUM",
                status = "PENDING",
                dueDate = "Tomorrow"
            )
        )
        taskDao.insertTask(
            TaskEntity(
                userId = userId,
                title = "Configure AI Model Preferences",
                description = "Select your preferred Gemini model and response style in Settings.",
                priority = "LOW",
                status = "PENDING",
                dueDate = "This week"
            )
        )
    }
}
