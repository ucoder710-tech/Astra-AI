package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.AstraShell
import com.example.ui.screens.AIToolsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocumentsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AstraViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    private val viewModel: AstraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstraApp(viewModel = viewModel)
        }
    }
}

@Composable
fun AstraApp(viewModel: AstraViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Persisted theme preference
    val isDarkTheme = currentUser?.themePreference != "LIGHT"

    MyApplicationTheme(darkTheme = isDarkTheme) {
        if (currentUser == null) {
            AuthScreen(
                onLogin = { email, pass -> viewModel.login(email, pass) },
                onRegister = { name, email, pass -> viewModel.register(name, email, pass) },
                isLoading = isAuthLoading,
                errorMessage = authError
            )
        } else {
            val user = currentUser!!
            AstraShell(
                currentUser = user,
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) },
                onLogout = { viewModel.logout() }
            ) {
                when (currentScreen) {
                    Screen.DASHBOARD -> {
                        val metrics by viewModel.dashboardMetrics.collectAsState()
                        val convs by viewModel.conversations.collectAsState()
                        val docs by viewModel.documents.collectAsState()
                        val tasks by viewModel.filteredTasks.collectAsState()

                        DashboardScreen(
                            userName = user.name,
                            metrics = metrics,
                            recentConversations = convs,
                            recentDocuments = docs,
                            recentTasks = tasks,
                            onNavigate = { viewModel.navigateTo(it) },
                            onOpenConversation = {
                                viewModel.selectConversation(it)
                                viewModel.navigateTo(Screen.CHAT)
                            },
                            onOpenDocument = {
                                viewModel.selectDocument(it)
                                viewModel.navigateTo(Screen.DOCUMENTS)
                            }
                        )
                    }

                    Screen.CHAT -> {
                        val convs by viewModel.conversations.collectAsState()
                        val activeConvId by viewModel.activeConversationId.collectAsState()
                        val msgs by viewModel.messages.collectAsState()
                        val isStreaming by viewModel.isStreaming.collectAsState()
                        val streamText by viewModel.streamingText.collectAsState()
                        val chatErr by viewModel.chatError.collectAsState()
                        val searchQ by viewModel.chatSearchQuery.collectAsState()

                        ChatScreen(
                            conversations = convs,
                            activeConversationId = activeConvId,
                            messages = msgs,
                            isStreaming = isStreaming,
                            streamingText = streamText,
                            chatError = chatErr,
                            searchQuery = searchQ,
                            onSearchChanged = { viewModel.setChatSearch(it) },
                            onSelectConversation = { viewModel.selectConversation(it) },
                            onCreateNewConversation = { viewModel.createNewConversation() },
                            onDeleteConversation = { viewModel.deleteConversation(it) },
                            onSendMessage = { viewModel.sendMessage(it) }
                        )
                    }

                    Screen.DOCUMENTS -> {
                        val docs by viewModel.documents.collectAsState()
                        val selectedDoc by viewModel.selectedDocument.collectAsState()
                        val isProcessing by viewModel.isDocProcessing.collectAsState()
                        val ragAnswer by viewModel.ragAnswer.collectAsState()
                        val isRagLoading by viewModel.isRagLoading.collectAsState()
                        val docActionOutput by viewModel.docActionOutput.collectAsState()

                        DocumentsScreen(
                            documents = docs,
                            selectedDocument = selectedDoc,
                            isProcessing = isProcessing,
                            ragAnswer = ragAnswer,
                            isRagLoading = isRagLoading,
                            actionOutput = docActionOutput,
                            onSelectDocument = { viewModel.selectDocument(it) },
                            onUploadDocument = { name, mime, text -> viewModel.uploadDocument(name, mime, text) },
                            onDeleteDocument = { viewModel.deleteDocument(it) },
                            onQueryRag = { docId, q -> viewModel.queryDocumentRag(docId, q) },
                            onPerformAction = { docId, action -> viewModel.performDocumentAction(docId, action) }
                        )
                    }

                    Screen.AI_TOOLS -> {
                        val selectedTool by viewModel.selectedTool.collectAsState()
                        val in1 by viewModel.toolInput1.collectAsState()
                        val in2 by viewModel.toolInput2.collectAsState()
                        val out by viewModel.toolOutput.collectAsState()
                        val isToolRunning by viewModel.isToolRunning.collectAsState()
                        val toolErr by viewModel.toolError.collectAsState()

                        AIToolsScreen(
                            selectedTool = selectedTool,
                            input1 = in1,
                            input2 = in2,
                            output = out,
                            isLoading = isToolRunning,
                            errorMessage = toolErr,
                            onSelectTool = { viewModel.selectTool(it) },
                            onInput1Changed = { viewModel.setToolInput1(it) },
                            onInput2Changed = { viewModel.setToolInput2(it) },
                            onRunTool = { viewModel.runCurrentTool() }
                        )
                    }

                    Screen.TASKS -> {
                        val tasks by viewModel.filteredTasks.collectAsState()
                        val taskSearch by viewModel.taskSearch.collectAsState()
                        val statusFilter by viewModel.taskStatusFilter.collectAsState()
                        val priorityFilter by viewModel.taskPriorityFilter.collectAsState()
                        val extractedTask by viewModel.extractedTask.collectAsState()
                        val isExtracting by viewModel.isExtractingTask.collectAsState()

                        TasksScreen(
                            tasks = tasks,
                            searchQuery = taskSearch,
                            statusFilter = statusFilter,
                            priorityFilter = priorityFilter,
                            extractedTask = extractedTask,
                            isExtractingTask = isExtracting,
                            onSearchChanged = { viewModel.setTaskSearch(it) },
                            onStatusFilterChanged = { viewModel.setTaskStatusFilter(it) },
                            onPriorityFilterChanged = { viewModel.setTaskPriorityFilter(it) },
                            onCreateTask = { title, desc, prio, due ->
                                viewModel.createTask(title, desc, prio, due)
                            },
                            onToggleStatus = { id, st -> viewModel.toggleTaskStatus(id, st) },
                            onDeleteTask = { viewModel.deleteTask(it) },
                            onParseNlTask = { viewModel.parseNaturalLanguageTask(it) },
                            onConfirmExtractedTask = { viewModel.confirmExtractedTask() },
                            onClearExtractedTask = { viewModel.clearExtractedTask() }
                        )
                    }

                    Screen.HISTORY -> {
                        val activities by viewModel.filteredActivities.collectAsState()
                        val typeFilter by viewModel.historyTypeFilter.collectAsState()
                        val searchQ by viewModel.historySearch.collectAsState()

                        HistoryScreen(
                            activities = activities,
                            typeFilter = typeFilter,
                            searchQuery = searchQ,
                            onTypeFilterChanged = { viewModel.setHistoryTypeFilter(it) },
                            onSearchChanged = { viewModel.setHistorySearch(it) }
                        )
                    }

                    Screen.SETTINGS, Screen.PROFILE -> {
                        ProfileSettingsScreen(
                            user = user,
                            onUpdateTheme = { viewModel.updateTheme(it) },
                            onUpdateAiPreferences = { model, style -> viewModel.updateAiPreferences(model, style) },
                            onUpdateProfile = { viewModel.updateProfile(it) },
                            onLogout = { viewModel.logout() }
                        )
                    }
                }
            }
        }
    }
}
