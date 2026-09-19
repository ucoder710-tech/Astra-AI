package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiProvider : AIProvider {
    override val providerName: String = "Google Gemini"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val defaultModel = "gemini-2.5-flash"

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
    }

    override suspend fun generateContent(
        prompt: String,
        systemInstruction: String?,
        modelOverride: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("GEMINI_API_KEY is not configured. Please add your key in AI Studio Secrets.")
            )
        }

        val model = modelOverride ?: defaultModel
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        try {
            val rootJson = JSONObject()
            val contentsArray = JSONArray()
            val userContent = JSONObject()
            userContent.put("role", "user")
            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", prompt))
            userContent.put("parts", partsArray)
            contentsArray.put(userContent)
            rootJson.put("contents", contentsArray)

            if (!systemInstruction.isNullOrBlank()) {
                val sysContent = JSONObject()
                val sysParts = JSONArray()
                sysParts.put(JSONObject().put("text", systemInstruction))
                sysContent.put("parts", sysParts)
                rootJson.put("systemInstruction", sysContent)
            }

            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: "HTTP ${response.code}"
                return@withContext Result.failure(Exception("Gemini API Error: $errBody"))
            }

            val bodyString = response.body?.string() ?: ""
            val responseJson = JSONObject(bodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val contentObj = firstCandidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                val textBuilder = StringBuilder()
                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        textBuilder.append(parts.getJSONObject(i).optString("text", ""))
                    }
                }
                Result.success(textBuilder.toString())
            } else {
                Result.failure(Exception("No generation candidates returned from Gemini."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun streamChat(
        history: List<ChatMessage>,
        latestMessage: String,
        systemInstruction: String?,
        modelOverride: String?,
        onChunk: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("GEMINI_API_KEY is not configured. Please add your key in AI Studio Secrets.")
            )
        }

        val model = modelOverride ?: defaultModel
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:streamGenerateContent?alt=sse&key=$apiKey"

        try {
            val rootJson = JSONObject()
            val contentsArray = JSONArray()

            // Add history
            for (msg in history.takeLast(10)) {
                val role = if (msg.role == "assistant" || msg.role == "model") "model" else "user"
                val c = JSONObject()
                c.put("role", role)
                val parts = JSONArray()
                parts.put(JSONObject().put("text", msg.content))
                c.put("parts", parts)
                contentsArray.put(c)
            }

            // Add current message
            val currentContent = JSONObject()
            currentContent.put("role", "user")
            val currentParts = JSONArray()
            currentParts.put(JSONObject().put("text", latestMessage))
            currentContent.put("parts", currentParts)
            contentsArray.put(currentContent)

            rootJson.put("contents", contentsArray)

            // System prompt
            val sysContent = JSONObject()
            val sysParts = JSONArray()
            val defaultInstruction = "You are Astra, an intelligent, precise and elegant personal productivity assistant. You provide clear, well-structured, insightful responses using Markdown. Maintain high craftsmanship, professional tone and concise clarity."
            sysParts.put(JSONObject().put("text", systemInstruction ?: defaultInstruction))
            sysContent.put("parts", sysParts)
            rootJson.put("systemInstruction", sysContent)

            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: "HTTP ${response.code}"
                return@withContext Result.failure(Exception("Streaming API Error: $errBody"))
            }

            val accumulatedText = StringBuilder()
            response.body?.byteStream()?.bufferedReader()?.use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val trimmed = line?.trim() ?: continue
                    if (!trimmed.startsWith("data:")) continue
                    val payload = trimmed.removePrefix("data:").trim()
                    if (payload.isEmpty() || payload == "[DONE]") continue

                    try {
                        val chunkObj = JSONObject(payload)
                        val candidates = chunkObj.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val candidate = candidates.getJSONObject(0)
                            val parts = candidate.optJSONObject("content")?.optJSONArray("parts")
                            if (parts != null) {
                                for (p in 0 until parts.length()) {
                                    val partText = parts.getJSONObject(p).optString("text", "")
                                    if (partText.isNotEmpty()) {
                                        accumulatedText.append(partText)
                                        withContext(Dispatchers.Main) {
                                            onChunk(partText)
                                        }
                                    }
                                }
                            }
                        }
                    } catch (ignore: Exception) {
                        // Keep streaming next chunk
                    }
                }
            }

            Result.success(accumulatedText.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun extractTaskFromNaturalLanguage(text: String): Result<ExtractedTask> =
        withContext(Dispatchers.IO) {
            val system = "Extract actionable task parameters from natural language into strict JSON: {\"title\": string, \"dueDate\": string, \"priority\": \"LOW\" | \"MEDIUM\" | \"HIGH\"}. Do not include markdown code block quotes. Output raw JSON only."
            val prompt = "Extract task from: \"$text\""
            val result = generateContent(prompt, systemInstruction = system)
            result.mapCatching { jsonStr ->
                val clean = jsonStr.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                ExtractedTask(
                    title = obj.optString("title", "New Task"),
                    description = obj.optString("description", ""),
                    dueDate = obj.optString("dueDate", "Today"),
                    priority = obj.optString("priority", "MEDIUM").uppercase()
                )
            }
        }

    override suspend fun answerDocumentQuery(
        documentName: String,
        relevantChunks: List<String>,
        question: String
    ): Result<RagAnswer> = withContext(Dispatchers.IO) {
        val contextBuilder = StringBuilder()
        relevantChunks.forEachIndexed { index, chunk ->
            contextBuilder.append("[Reference Section ${index + 1} from $documentName]:\n$chunk\n\n")
        }

        val prompt = """
Document: $documentName
Context:
$contextBuilder

User Question: $question

Instructions:
Answer using ONLY the provided document context above. If the information is not found in the context, clearly state: "I couldn't find that information in this document." Cite the section number where relevant.
        """.trimIndent()

        val system = "You are Astra's RAG Document Analyst. You answer questions strictly based on the provided document excerpts and cite references honestly without hallucinating."
        val result = generateContent(prompt, systemInstruction = system)
        result.map { text ->
            RagAnswer(
                answer = text,
                citations = listOf(documentName)
            )
        }
    }
}
