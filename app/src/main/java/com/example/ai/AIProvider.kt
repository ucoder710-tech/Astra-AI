package com.example.ai

data class ChatMessage(
    val role: String, // "user" or "model"
    val content: String
)

interface AIProvider {
    val providerName: String

    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null,
        modelOverride: String? = null
    ): Result<String>

    suspend fun streamChat(
        history: List<ChatMessage>,
        latestMessage: String,
        systemInstruction: String? = null,
        modelOverride: String? = null,
        onChunk: (String) -> Unit
    ): Result<String>

    suspend fun extractTaskFromNaturalLanguage(
        text: String
    ): Result<ExtractedTask>

    suspend fun answerDocumentQuery(
        documentName: String,
        relevantChunks: List<String>,
        question: String
    ): Result<RagAnswer>
}

data class ExtractedTask(
    val title: String,
    val description: String = "",
    val dueDate: String,
    val priority: String // "LOW", "MEDIUM", "HIGH"
)

data class RagAnswer(
    val answer: String,
    val citations: List<String>
)
