package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.TaskEntity
import com.example.data.repository.DashboardMetrics
import com.example.ui.components.MetricCard
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AstraTheme
import com.example.ui.theme.AuroraAccentGradient
import com.example.ui.viewmodel.Screen
import java.util.Calendar

@Composable
fun DashboardScreen(
    userName: String,
    metrics: DashboardMetrics,
    recentConversations: List<ConversationEntity>,
    recentDocuments: List<DocumentEntity>,
    recentTasks: List<TaskEntity>,
    onNavigate: (Screen) -> Unit,
    onOpenConversation: (String) -> Unit,
    onOpenDocument: (DocumentEntity) -> Unit
) {
    val greeting = rememberGreeting()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AstraTheme.colors.canvas)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Hero Greeting
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(16.dp)),
                color = AstraTheme.colors.surface
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(AuroraAccentGradient)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYSTEM ACTIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AstraTheme.colors.accentCyan,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "$greeting, $userName",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AstraTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "How can Astra assist your productivity today?",
                        fontSize = 14.sp,
                        color = AstraTheme.colors.textSecondary
                    )
                }
            }
        }

        // Quick Actions
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "QUICK ACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = AstraTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    QuickActionPill(
                        title = "Start Chat",
                        icon = Icons.Default.ChatBubbleOutline,
                        isPrimary = true,
                        onClick = { onNavigate(Screen.CHAT) }
                    )
                }
                item {
                    QuickActionPill(
                        title = "Upload Document",
                        icon = Icons.Default.UploadFile,
                        onClick = { onNavigate(Screen.DOCUMENTS) }
                    )
                }
                item {
                    QuickActionPill(
                        title = "AI Tools",
                        icon = Icons.Default.AutoAwesome,
                        onClick = { onNavigate(Screen.AI_TOOLS) }
                    )
                }
                item {
                    QuickActionPill(
                        title = "Create Task",
                        icon = Icons.Default.Add,
                        onClick = { onNavigate(Screen.TASKS) }
                    )
                }
            }
        }

        // Metrics Grid
        item {
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = "SYSTEM METRICS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = AstraTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Chats",
                    value = metrics.totalConversations.toString(),
                    icon = Icons.Default.ChatBubbleOutline,
                    accentColor = AstraTheme.colors.accentIndigo,
                    subtitle = "Conversations"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Docs",
                    value = metrics.totalDocuments.toString(),
                    icon = Icons.Default.Description,
                    accentColor = AstraTheme.colors.accentViolet,
                    subtitle = "Indexed in RAG"
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Tasks",
                    value = metrics.totalTasks.toString(),
                    icon = Icons.Default.CheckCircle,
                    accentColor = AstraTheme.colors.success,
                    subtitle = "Active tasks"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "AI Usages",
                    value = metrics.totalToolUsages.toString(),
                    icon = Icons.Default.SmartToy,
                    accentColor = AstraTheme.colors.accentCyan,
                    subtitle = "Tool runs"
                )
            }
        }

        // Recent Tasks
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT TASKS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = AstraTheme.colors.textTertiary
                )
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    color = AstraTheme.colors.accentIndigo,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigate(Screen.TASKS) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (recentTasks.isEmpty()) {
            item {
                EmptyCardMessage(message = "No tasks created yet. Create one via natural language!")
            }
        } else {
            items(recentTasks.take(3)) { task ->
                TaskRowItem(task = task, onClick = { onNavigate(Screen.TASKS) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Recent Documents
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INDEXED DOCUMENTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = AstraTheme.colors.textTertiary
                )
                Text(
                    text = "Manage Docs",
                    fontSize = 12.sp,
                    color = AstraTheme.colors.accentIndigo,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigate(Screen.DOCUMENTS) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (recentDocuments.isEmpty()) {
            item {
                EmptyCardMessage(message = "No documents uploaded yet.")
            }
        } else {
            items(recentDocuments.take(2)) { doc ->
                DocumentRowItem(doc = doc, onClick = { onOpenDocument(doc) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Recent Chats
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT CONVERSATIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = AstraTheme.colors.textTertiary
                )
                Text(
                    text = "Open Chat",
                    fontSize = 12.sp,
                    color = AstraTheme.colors.accentIndigo,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigate(Screen.CHAT) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (recentConversations.isEmpty()) {
            item {
                EmptyCardMessage(message = "No recent conversations. Ask Astra anything!")
            }
        } else {
            items(recentConversations.take(3)) { conv ->
                ConversationRowItem(conv = conv, onClick = { onOpenConversation(conv.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun QuickActionPill(
    title: String,
    icon: ImageVector,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (isPrimary) Modifier.background(AuroraAccentGradient)
                else Modifier
                    .background(AstraTheme.colors.surface)
                    .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(10.dp))
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isPrimary) androidx.compose.ui.graphics.Color.White else AstraTheme.colors.accentIndigo,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPrimary) androidx.compose.ui.graphics.Color.White else AstraTheme.colors.textPrimary
            )
        }
    }
}

@Composable
fun TaskRowItem(task: TaskEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AstraTheme.colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AstraTheme.colors.textPrimary
                )
                if (task.dueDate.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Due: ${task.dueDate}",
                        fontSize = 12.sp,
                        color = AstraTheme.colors.textTertiary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            PriorityBadge(priority = task.priority)
        }
    }
}

@Composable
fun DocumentRowItem(doc: DocumentEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AstraTheme.colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doc.filename,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AstraTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (doc.summary.isNotBlank()) doc.summary else "Indexed in RAG store",
                    fontSize = 12.sp,
                    color = AstraTheme.colors.textSecondary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            StatusBadge(status = doc.status)
        }
    }
}

@Composable
fun ConversationRowItem(conv: ConversationEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AstraTheme.colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = null,
                tint = AstraTheme.colors.accentIndigo,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = conv.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AstraTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun EmptyCardMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AstraTheme.colors.surface)
            .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(10.dp))
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 13.sp,
            color = AstraTheme.colors.textTertiary
        )
    }
}

@Composable
fun rememberGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}
