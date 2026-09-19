package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.ExtractedTask
import com.example.ai.GeminiProvider
import com.example.ai.RagAnswer
import com.example.data.local.AstraDatabase
import com.example.data.model.ActivityLogEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.MessageEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import com.example.data.repository.AstraRepository
import com.example.data.repository.DashboardMetrics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    DASHBOARD,
    CHAT,
    DOCUMENTS,
    AI_TOOLS,
    TASKS,
    HISTORY,
    SETTINGS,
    PROFILE
}

enum class ToolType(val label: String, val description: String) {
    SUMMARIZER("Summarizer", "Distill lengthy documents or text into crisp summaries"),
    GRAMMAR_CHECKER("Grammar Checker", "Polish spelling, syntax, punctuation and tone"),
    TRANSLATOR("Translator", "Translate accurately across major international languages"),
    EMAIL_WRITER("Email Writer", "Draft persuasive, professional corporate emails"),
    CONTENT_WRITER("Content Writer", "Create articles, briefs, outlines, and copy"),
    CODE_ASSISTANT("Code Assistant", "Generate, explain, and debug clean code"),
    MCQ_GENERATOR("MCQ Generator", "Create interactive multiple choice quizzes with answers"),
    QUESTION_GENERATOR("Question Generator", "Generate conceptual study and review questions")
}

@OptIn(ExperimentalCoroutinesApi::class)
class AstraViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AstraDatabase.getDatabase(application)
    private val aiProvider = GeminiProvider()
    private val repository = AstraRepository(database, aiProvider)

    // Current User & Session
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isSidebarOpen = MutableStateFlow(false)
    val isSidebarOpen: StateFlow<Boolean> = _isSidebarOpen.asStateFlow()

    // Dashboard Metrics
    val dashboardMetrics: StateFlow<DashboardMetrics> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getDashboardMetrics(user.id)
        else flowOf(DashboardMetrics())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    // Chat
    val conversations: StateFlow<List<ConversationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getConversations(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

    val messages: StateFlow<List<MessageEntity>> = _activeConversationId.flatMapLatest { convId ->
        if (convId != null) repository.getMessages(convId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    private val _chatSearchQuery = MutableStateFlow("")
    val chatSearchQuery: StateFlow<String> = _chatSearchQuery.asStateFlow()

    // Documents & RAG
    val documents: StateFlow<List<DocumentEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getDocuments(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDocument = MutableStateFlow<DocumentEntity?>(null)
    val selectedDocument: StateFlow<DocumentEntity?> = _selectedDocument.asStateFlow()

    private val _isDocProcessing = MutableStateFlow(false)
    val isDocProcessing: StateFlow<Boolean> = _isDocProcessing.asStateFlow()

    private val _ragAnswer = MutableStateFlow<RagAnswer?>(null)
    val ragAnswer: StateFlow<RagAnswer?> = _ragAnswer.asStateFlow()

    private val _isRagLoading = MutableStateFlow(false)
    val isRagLoading: StateFlow<Boolean> = _isRagLoading.asStateFlow()

    private val _docActionOutput = MutableStateFlow<String?>(null)
    val docActionOutput: StateFlow<String?> = _docActionOutput.asStateFlow()

    // AI Tools
    private val _selectedTool = MutableStateFlow(ToolType.SUMMARIZER)
    val selectedTool: StateFlow<ToolType> = _selectedTool.asStateFlow()

    private val _toolInput1 = MutableStateFlow("")
    val toolInput1: StateFlow<String> = _toolInput1.asStateFlow()

    private val _toolInput2 = MutableStateFlow("")
    val toolInput2: StateFlow<String> = _toolInput2.asStateFlow()

    private val _toolOutput = MutableStateFlow<String?>(null)
    val toolOutput: StateFlow<String?> = _toolOutput.asStateFlow()

    private val _isToolRunning = MutableStateFlow(false)
    val isToolRunning: StateFlow<Boolean> = _isToolRunning.asStateFlow()

    private val _toolError = MutableStateFlow<String?>(null)
    val toolError: StateFlow<String?> = _toolError.asStateFlow()

    // Tasks
    private val _rawTasks: StateFlow<List<TaskEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getTasks(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _taskSearch = MutableStateFlow("")
    val taskSearch: StateFlow<String> = _taskSearch.asStateFlow()

    private val _taskStatusFilter = MutableStateFlow("ALL")
    val taskStatusFilter: StateFlow<String> = _taskStatusFilter.asStateFlow()

    private val _taskPriorityFilter = MutableStateFlow("ALL")
    val taskPriorityFilter: StateFlow<String> = _taskPriorityFilter.asStateFlow()

    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        _rawTasks,
        _taskSearch,
        _taskStatusFilter,
        _taskPriorityFilter
    ) { tasks, query, status, priority ->
        tasks.filter { task ->
            val matchesQuery = query.isBlank() || task.title.contains(query, ignoreCase = true) || task.description.contains(query, ignoreCase = true)
            val matchesStatus = status == "ALL" || task.status == status
            val matchesPriority = priority == "ALL" || task.priority == priority
            matchesQuery && matchesStatus && matchesPriority
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Natural Language Task Extraction
    private val _extractedTask = MutableStateFlow<ExtractedTask?>(null)
    val extractedTask: StateFlow<ExtractedTask?> = _extractedTask.asStateFlow()

    private val _isExtractingTask = MutableStateFlow(false)
    val isExtractingTask: StateFlow<Boolean> = _isExtractingTask.asStateFlow()

    // History
    val activities: StateFlow<List<ActivityLogEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getActivities(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _historyTypeFilter = MutableStateFlow("ALL")
    val historyTypeFilter: StateFlow<String> = _historyTypeFilter.asStateFlow()

    private val _historySearch = MutableStateFlow("")
    val historySearch: StateFlow<String> = _historySearch.asStateFlow()

    val filteredActivities: StateFlow<List<ActivityLogEntity>> = combine(
        activities,
        _historyTypeFilter,
        _historySearch
    ) { list, filter, query ->
        list.filter { act ->
            val matchesType = filter == "ALL" || act.actionType == filter
            val matchesQuery = query.isBlank() || act.title.contains(query, ignoreCase = true) || act.description.contains(query, ignoreCase = true)
            matchesType && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Auto-login default account or create initial session
        viewModelScope.launch {
            val defaultEmail = "demo@astra.ai"
            val existing = database.userDao().getUserByEmail(defaultEmail)
            if (existing != null) {
                _currentUser.value = existing
            } else {
                repository.register("Alex Morgan", defaultEmail, "astra123")
                    .onSuccess { _currentUser.value = it }
            }
        }
    }

    // --- Navigation ---
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        _isSidebarOpen.value = false
    }

    fun toggleSidebar() {
        _isSidebarOpen.value = !_isSidebarOpen.value
    }

    // --- Auth ---
    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            repository.register(name, email, pass)
                .onSuccess {
                    _currentUser.value = it
                    _isAuthLoading.value = false
                }
                .onFailure {
                    _authError.value = it.message ?: "Registration failed"
                    _isAuthLoading.value = false
                }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            repository.login(email, pass)
                .onSuccess {
                    _currentUser.value = it
                    _isAuthLoading.value = false
                }
                .onFailure {
                    _authError.value = it.message ?: "Authentication failed"
                    _isAuthLoading.value = false
                }
        }
    }

    fun logout() {
        _currentUser.value = null
        _activeConversationId.value = null
        _selectedDocument.value = null
    }

    fun updateTheme(theme: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateTheme(user.id, theme)
            _currentUser.value = user.copy(themePreference = theme)
        }
    }

    fun updateAiPreferences(model: String, style: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateAiPreferences(user.id, model, style)
            _currentUser.value = user.copy(modelPreference = model, responseStyle = style)
        }
    }

    fun updateProfile(name: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateProfile(user.id, name)
            _currentUser.value = user.copy(name = name)
        }
    }

    // --- Chat Methods ---
    fun selectConversation(id: String) {
        _activeConversationId.value = id
        _chatError.value = null
    }

    fun createNewConversation(title: String = "New Conversation") {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val conv = repository.createConversation(user.id, title)
            _activeConversationId.value = conv.id
            _chatError.value = null
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_activeConversationId.value == id) {
                _activeConversationId.value = null
            }
        }
    }

    fun setChatSearch(query: String) {
        _chatSearchQuery.value = query
    }

    fun sendMessage(text: String) {
        val user = _currentUser.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            var convId = _activeConversationId.value
            if (convId == null) {
                val newConv = repository.createConversation(user.id, text.take(24))
                convId = newConv.id
                _activeConversationId.value = convId
            }

            _isStreaming.value = true
            _streamingText.value = ""
            _chatError.value = null

            val result = repository.sendMessage(
                userId = user.id,
                conversationId = convId,
                userContent = text,
                model = user.modelPreference,
                responseStyle = user.responseStyle,
                onChunk = { chunk ->
                    _streamingText.value += chunk
                }
            )

            _isStreaming.value = false
            _streamingText.value = ""

            result.onFailure { err ->
                _chatError.value = err.message ?: "Failed to generate response"
            }
        }
    }

    // --- Documents & RAG Methods ---
    fun selectDocument(doc: DocumentEntity?) {
        _selectedDocument.value = doc
        _ragAnswer.value = null
        _docActionOutput.value = null
    }

    fun uploadDocument(filename: String, mimeType: String, content: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isDocProcessing.value = true
            repository.uploadDocument(user.id, filename, mimeType, content)
                .onSuccess {
                    _selectedDocument.value = it
                    _isDocProcessing.value = false
                }
                .onFailure {
                    _isDocProcessing.value = false
                }
        }
    }

    fun deleteDocument(docId: String) {
        viewModelScope.launch {
            repository.deleteDocument(docId)
            if (_selectedDocument.value?.id == docId) {
                _selectedDocument.value = null
            }
        }
    }

    fun queryDocumentRag(docId: String, query: String) {
        viewModelScope.launch {
            _isRagLoading.value = true
            _ragAnswer.value = null
            val result = repository.queryDocumentRag(docId, query)
            _isRagLoading.value = false
            result.onSuccess {
                _ragAnswer.value = it
            }.onFailure {
                _ragAnswer.value = RagAnswer("Error answering query: ${it.message}", emptyList())
            }
        }
    }

    fun performDocumentAction(docId: String, actionType: String) {
        viewModelScope.launch {
            _isRagLoading.value = true
            _docActionOutput.value = null
            val result = repository.performDocumentAction(docId, actionType)
            _isRagLoading.value = false
            result.onSuccess {
                _docActionOutput.value = it
            }.onFailure {
                _docActionOutput.value = "Action failed: ${it.message}"
            }
        }
    }

    // --- AI Tools Methods ---
    fun selectTool(tool: ToolType) {
        _selectedTool.value = tool
        _toolInput1.value = ""
        _toolInput2.value = ""
        _toolOutput.value = null
        _toolError.value = null
    }

    fun setToolInput1(v: String) { _toolInput1.value = v }
    fun setToolInput2(v: String) { _toolInput2.value = v }

    fun runCurrentTool() {
        val user = _currentUser.value ?: return
        val input = _toolInput1.value.trim()
        if (input.isBlank()) {
            _toolError.value = "Please provide valid input."
            return
        }

        val tool = _selectedTool.value
        val (prompt, sysPrompt) = when (tool) {
            ToolType.SUMMARIZER -> {
                val format = if (_toolInput2.value.isNotBlank()) _toolInput2.value else "Structured bullet points and key takeaways"
                Pair(
                    "Summarize the following text in format ($format):\n\n$input",
                    "You are Astra's Executive Summarizer. Output clean, structured markdown summaries with high signal-to-noise ratio."
                )
            }
            ToolType.GRAMMAR_CHECKER -> {
                Pair(
                    "Proofread and improve the grammar, sentence structure and clarity of the following text:\n\n$input",
                    "You are Astra's Grammar & Style Editor. Output: 1) Corrected Version, 2) Key Improvements Made."
                )
            }
            ToolType.TRANSLATOR -> {
                val target = if (_toolInput2.value.isNotBlank()) _toolInput2.value else "Spanish"
                Pair(
                    "Translate the following text into $target:\n\n$input",
                    "You are Astra's High-Precision Polyglot Translator. Deliver accurate, natural translations with cultural nuance."
                )
            }
            ToolType.EMAIL_WRITER -> {
                val tone = if (_toolInput2.value.isNotBlank()) _toolInput2.value else "Professional & Courteous"
                Pair(
                    "Write an email with tone ($tone) based on these notes:\n\n$input",
                    "You are Astra's Corporate Email Strategist. Output a clear Subject line and refined Email body."
                )
            }
            ToolType.CONTENT_WRITER -> {
                val audience = if (_toolInput2.value.isNotBlank()) _toolInput2.value else "Tech-savvy professionals"
                Pair(
                    "Write engaging content for audience ($audience) about:\n\n$input",
                    "You are Astra's Content Creator. Produce well-crafted articles with catchy headings and readable flow."
                )
            }
            ToolType.CODE_ASSISTANT -> {
                val lang = if (_toolInput2.value.isNotBlank()) _toolInput2.value else "Kotlin"
                Pair(
                    "Language: $lang\nRequest:\n$input",
                    "You are Astra's Senior Software Engineer. Provide elegant, production-grade code with comments and a concise explanation."
                )
            }
            ToolType.MCQ_GENERATOR -> {
                val count = if (_toolInput2.value.isNotBlank()) _toolInput2.value else "4"
                Pair(
                    "Generate $count multiple choice questions based on:\n\n$input",
                    "You are Astra's Examination Architect. For each question, provide 4 options (A, B, C, D), mark the Correct Option, and give a 1-sentence Explanation."
                )
            }
            ToolType.QUESTION_GENERATOR -> {
                val count = if (_toolInput2.value.isNotBlank()) _toolInput2.value else "5"
                Pair(
                    "Generate $count insightful comprehension and study questions on:\n\n$input",
                    "You are Astra's Academic Tutor. Create thought-provoking questions with detailed answer keys."
                )
            }
        }

        viewModelScope.launch {
            _isToolRunning.value = true
            _toolOutput.value = null
            _toolError.value = null

            val result = repository.executeAiTool(user.id, tool.label, prompt, sysPrompt)
            _isToolRunning.value = false
            result.onSuccess {
                _toolOutput.value = it
            }.onFailure {
                _toolError.value = it.message ?: "Tool execution failed"
            }
        }
    }

    // --- Task Methods ---
    fun setTaskSearch(q: String) { _taskSearch.value = q }
    fun setTaskStatusFilter(f: String) { _taskStatusFilter.value = f }
    fun setTaskPriorityFilter(f: String) { _taskPriorityFilter.value = f }

    fun createTask(title: String, desc: String, priority: String, dueDate: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createTask(user.id, title, desc, priority, dueDate)
        }
    }

    fun toggleTaskStatus(taskId: String, currentStatus: String) {
        val nextStatus = when (currentStatus) {
            "COMPLETED" -> "PENDING"
            "PENDING" -> "IN_PROGRESS"
            else -> "COMPLETED"
        }
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, nextStatus)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun parseNaturalLanguageTask(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isExtractingTask.value = true
            val result = repository.parseNaturalLanguageTask(text)
            _isExtractingTask.value = false
            result.onSuccess {
                _extractedTask.value = it
            }.onFailure {
                // Fallback extraction
                _extractedTask.value = ExtractedTask(
                    title = text.take(40),
                    description = text,
                    dueDate = "Today",
                    priority = "MEDIUM"
                )
            }
        }
    }

    fun clearExtractedTask() {
        _extractedTask.value = null
    }

    fun confirmExtractedTask() {
        val user = _currentUser.value ?: return
        val ext = _extractedTask.value ?: return
        viewModelScope.launch {
            repository.createTask(
                userId = user.id,
                title = ext.title,
                description = ext.description,
                priority = ext.priority,
                dueDate = ext.dueDate
            )
            _extractedTask.value = null
        }
    }

    // --- History Filter Methods ---
    fun setHistoryTypeFilter(type: String) { _historyTypeFilter.value = type }
    fun setHistorySearch(query: String) { _historySearch.value = query }
}
