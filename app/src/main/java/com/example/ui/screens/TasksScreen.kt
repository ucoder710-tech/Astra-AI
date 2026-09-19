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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ExtractedTask
import com.example.data.model.TaskEntity
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AstraTheme
import com.example.ui.theme.AuroraAccentGradient

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    searchQuery: String,
    statusFilter: String,
    priorityFilter: String,
    extractedTask: ExtractedTask?,
    isExtractingTask: Boolean,
    onSearchChanged: (String) -> Unit,
    onStatusFilterChanged: (String) -> Unit,
    onPriorityFilterChanged: (String) -> Unit,
    onCreateTask: (title: String, desc: String, priority: String, dueDate: String) -> Unit,
    onToggleStatus: (taskId: String, currentStatus: String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onParseNlTask: (String) -> Unit,
    onConfirmExtractedTask: () -> Unit,
    onClearExtractedTask: () -> Unit
) {
    var showManualCreateDialog by remember { mutableStateOf(false) }
    var showNlInputDialog by remember { mutableStateOf(false) }
    var nlTextPrompt by remember { mutableStateOf("") }

    var manualTitle by remember { mutableStateOf("") }
    var manualDesc by remember { mutableStateOf("") }
    var manualPriority by remember { mutableStateOf("MEDIUM") }
    var manualDueDate by remember { mutableStateOf("Today") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AstraTheme.colors.canvas)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TASK MANAGEMENT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AstraTheme.colors.textPrimary
                )
                Text(
                    text = "Organize actions with AI natural language parsing",
                    fontSize = 12.sp,
                    color = AstraTheme.colors.textSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Natural Language Task Button
                Button(
                    onClick = { showNlInputDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AstraTheme.colors.surfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderStrong),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AstraTheme.colors.accentCyan, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Quick Add", color = AstraTheme.colors.textPrimary, fontSize = 12.sp)
                }

                // Manual Add Button
                Button(
                    onClick = { showManualCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AuroraAccentGradient)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text("Search tasks...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AstraTheme.colors.textTertiary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AstraTheme.colors.accentIndigo,
                unfocusedBorderColor = AstraTheme.colors.borderSubtle
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val statuses = listOf("ALL", "PENDING", "IN_PROGRESS", "COMPLETED")
            items(statuses) { st ->
                val isSelected = st == statusFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) AstraTheme.colors.accentIndigo else AstraTheme.colors.surface)
                        .border(1.dp, if (isSelected) AstraTheme.colors.accentIndigo else AstraTheme.colors.borderSubtle, RoundedCornerShape(6.dp))
                        .clickable { onStatusFilterChanged(st) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (st == "ALL") "All Statuses" else st.replace("_", " "),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else AstraTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Task List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tasks found matching criteria.",
                            color = AstraTheme.colors.textTertiary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            items(tasks) { task ->
                val isCompleted = task.status == "COMPLETED"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) AstraTheme.colors.surfaceRaised.copy(alpha = 0.5f) else AstraTheme.colors.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Checkbox status toggle
                        IconButton(
                            onClick = { onToggleStatus(task.id, task.status) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle status",
                                tint = if (isCompleted) AstraTheme.colors.success else AstraTheme.colors.textTertiary
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCompleted) AstraTheme.colors.textTertiary else AstraTheme.colors.textPrimary
                            )
                            if (task.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = task.description,
                                    fontSize = 12.sp,
                                    color = AstraTheme.colors.textSecondary
                                )
                            }
                            if (task.dueDate.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Due: ${task.dueDate}",
                                    fontSize = 11.sp,
                                    color = AstraTheme.colors.accentCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            PriorityBadge(priority = task.priority)
                            Spacer(modifier = Modifier.height(6.dp))
                            StatusBadge(status = task.status)
                        }

                        IconButton(
                            onClick = { onDeleteTask(task.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = AstraTheme.colors.textTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Natural Language Input Dialog
    if (showNlInputDialog) {
        AlertDialog(
            onDismissRequest = { showNlInputDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AstraTheme.colors.accentCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Natural Language Task", color = AstraTheme.colors.textPrimary, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Speak or write in plain English. Astra will structure it into an actionable task with due dates and priority.",
                        fontSize = 12.sp,
                        color = AstraTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = nlTextPrompt,
                        onValueChange = { nlTextPrompt = it },
                        placeholder = { Text("e.g. Prepare system presentation for tomorrow 4pm priority high", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nlTextPrompt.isNotBlank()) {
                            onParseNlTask(nlTextPrompt.trim())
                            showNlInputDialog = false
                            nlTextPrompt = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AstraTheme.colors.accentIndigo)
                ) {
                    if (isExtractingTask) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Extract Task")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showNlInputDialog = false }) {
                    Text("Cancel", color = AstraTheme.colors.textTertiary)
                }
            },
            containerColor = AstraTheme.colors.surfaceRaised
        )
    }

    // STRICT CONFIRMATION DIALOG (Mandatory from Section 21)
    if (extractedTask != null) {
        AlertDialog(
            onDismissRequest = onClearExtractedTask,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AstraTheme.colors.accentIndigo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create this task?", color = AstraTheme.colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "Astra extracted the following details. Please review before committing to your database:",
                        fontSize = 12.sp,
                        color = AstraTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AstraTheme.colors.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "TITLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AstraTheme.colors.textTertiary)
                            Text(text = extractedTask.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AstraTheme.colors.textPrimary)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "DUE DATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AstraTheme.colors.textTertiary)
                            Text(text = extractedTask.dueDate, fontSize = 13.sp, color = AstraTheme.colors.accentCyan)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "PRIORITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AstraTheme.colors.textTertiary)
                            PriorityBadge(priority = extractedTask.priority)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onConfirmExtractedTask,
                    colors = ButtonDefaults.buttonColors(containerColor = AstraTheme.colors.accentIndigo)
                ) {
                    Text("Confirm & Create")
                }
            },
            dismissButton = {
                TextButton(onClick = onClearExtractedTask) {
                    Text("Cancel", color = AstraTheme.colors.textTertiary)
                }
            },
            containerColor = AstraTheme.colors.surfaceRaised
        )
    }

    // Manual Create Task Dialog
    if (showManualCreateDialog) {
        AlertDialog(
            onDismissRequest = { showManualCreateDialog = false },
            title = { Text("Create New Task", color = AstraTheme.colors.textPrimary) },
            text = {
                Column {
                    OutlinedTextField(
                        value = manualTitle,
                        onValueChange = { manualTitle = it },
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = manualDesc,
                        onValueChange = { manualDesc = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = manualDueDate,
                        onValueChange = { manualDueDate = it },
                        label = { Text("Due Date (e.g. Friday 5 PM)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Priority:", fontSize = 12.sp, color = AstraTheme.colors.textSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                            val isSel = manualPriority == p
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) AstraTheme.colors.accentIndigo else AstraTheme.colors.surface)
                                    .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(6.dp))
                                    .clickable { manualPriority = p }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(p, fontSize = 11.sp, color = if (isSel) Color.White else AstraTheme.colors.textSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (manualTitle.isNotBlank()) {
                            onCreateTask(manualTitle.trim(), manualDesc.trim(), manualPriority, manualDueDate.trim())
                            showManualCreateDialog = false
                            manualTitle = ""
                            manualDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AstraTheme.colors.accentIndigo)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualCreateDialog = false }) {
                    Text("Cancel", color = AstraTheme.colors.textTertiary)
                }
            },
            containerColor = AstraTheme.colors.surfaceRaised
        )
    }
}
