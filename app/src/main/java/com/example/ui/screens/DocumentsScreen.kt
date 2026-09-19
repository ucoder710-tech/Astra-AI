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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.material.icons.filled.UploadFile
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
import com.example.ai.RagAnswer
import com.example.data.model.DocumentEntity
import com.example.ui.components.MarkdownContentView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AstraTheme
import com.example.ui.theme.AuroraAccentGradient

@Composable
fun DocumentsScreen(
    documents: List<DocumentEntity>,
    selectedDocument: DocumentEntity?,
    isProcessing: Boolean,
    ragAnswer: RagAnswer?,
    isRagLoading: Boolean,
    actionOutput: String?,
    onSelectDocument: (DocumentEntity?) -> Unit,
    onUploadDocument: (filename: String, mimeType: String, content: String) -> Unit,
    onDeleteDocument: (String) -> Unit,
    onQueryRag: (docId: String, query: String) -> Unit,
    onPerformAction: (docId: String, actionType: String) -> Unit
) {
    var showUploadDialog by remember { mutableStateOf(false) }
    var uploadFilename by remember { mutableStateOf("") }
    var uploadContent by remember { mutableStateOf("") }
    var ragQuestionInput by remember { mutableStateOf("") }

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
                    text = "DOCUMENTS & RAG",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AstraTheme.colors.textPrimary
                )
                Text(
                    text = "Upload knowledge files & query with semantic grounding",
                    fontSize = 12.sp,
                    color = AstraTheme.colors.textSecondary
                )
            }
            Button(
                onClick = { showUploadDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AuroraAccentGradient)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Document", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDocument != null) {
            // Document Detail & RAG Interface
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(12.dp)),
                color = AstraTheme.colors.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = AstraTheme.colors.accentViolet)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedDocument.filename,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AstraTheme.colors.textPrimary
                            )
                        }
                        IconButton(onClick = { onSelectDocument(null) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = AstraTheme.colors.textTertiary)
                        }
                    }

                    if (selectedDocument.summary.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedDocument.summary,
                            fontSize = 13.sp,
                            color = AstraTheme.colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Document Quick Actions
                    Text(
                        text = "DOCUMENT ACTIONS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AstraTheme.colors.textTertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            ActionChip("Summarize", Icons.Default.ShortText) {
                                onPerformAction(selectedDocument.id, "SUMMARIZE")
                            }
                        }
                        item {
                            ActionChip("Key Insights", Icons.Default.AutoAwesome) {
                                onPerformAction(selectedDocument.id, "KEY_POINTS")
                            }
                        }
                        item {
                            ActionChip("Questions", Icons.Default.HelpOutline) {
                                onPerformAction(selectedDocument.id, "QUESTIONS")
                            }
                        }
                        item {
                            ActionChip("MCQ Quiz", Icons.Default.Quiz) {
                                onPerformAction(selectedDocument.id, "MCQS")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ask Astra (RAG) Query
                    Text(
                        text = "ASK ASTRA ABOUT THIS DOCUMENT (RAG)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AstraTheme.colors.accentCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = ragQuestionInput,
                            onValueChange = { ragQuestionInput = it },
                            placeholder = { Text("Ask a question about this document...", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AstraTheme.colors.accentIndigo,
                                unfocusedBorderColor = AstraTheme.colors.borderSubtle
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (ragQuestionInput.isNotBlank()) {
                                    onQueryRag(selectedDocument.id, ragQuestionInput.trim())
                                }
                            },
                            enabled = !isRagLoading && ragQuestionInput.isNotBlank(),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AstraTheme.colors.accentIndigo)
                        ) {
                            if (isRagLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                            } else {
                                Icon(Icons.Default.Send, contentDescription = "Query", tint = Color.White)
                            }
                        }
                    }

                    // RAG Answer Display
                    if (ragAnswer != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AstraTheme.colors.surfaceRaised)
                                .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = AstraTheme.colors.accentCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("RAG Grounded Response", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AstraTheme.colors.accentCyan)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                MarkdownContentView(content = ragAnswer.answer)
                                if (ragAnswer.citations.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Source: ${ragAnswer.citations.joinToString(", ")}",
                                        fontSize = 11.sp,
                                        color = AstraTheme.colors.textTertiary
                                    )
                                }
                            }
                        }
                    }

                    // Action Output Display
                    if (actionOutput != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AstraTheme.colors.surfaceRaised)
                                .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            MarkdownContentView(content = actionOutput)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Document List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(documents) { doc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectDocument(doc) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AstraTheme.colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AstraTheme.colors.accentIndigo.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = AstraTheme.colors.accentIndigo)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = doc.filename,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AstraTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${doc.fileSize} bytes • ${doc.mimeType}",
                                    fontSize = 11.sp,
                                    color = AstraTheme.colors.textTertiary
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(status = doc.status)
                            IconButton(onClick = { onDeleteDocument(doc.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AstraTheme.colors.textTertiary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Upload Document Dialog
    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = { Text("Upload Document for RAG", color = AstraTheme.colors.textPrimary) },
            text = {
                Column {
                    OutlinedTextField(
                        value = uploadFilename,
                        onValueChange = { uploadFilename = it },
                        label = { Text("File Name (e.g. System_Design.txt)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = uploadContent,
                        onValueChange = { uploadContent = it },
                        label = { Text("Paste Document Text / Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (uploadContent.isNotBlank()) {
                            val name = if (uploadFilename.isBlank()) "Document_${System.currentTimeMillis()}.txt" else uploadFilename
                            onUploadDocument(name, "text/plain", uploadContent.trim())
                            showUploadDialog = false
                            uploadFilename = ""
                            uploadContent = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AstraTheme.colors.accentIndigo)
                ) {
                    Text("Index & Process")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) {
                    Text("Cancel", color = AstraTheme.colors.textTertiary)
                }
            },
            containerColor = AstraTheme.colors.surfaceRaised
        )
    }
}

@Composable
fun ActionChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AstraTheme.colors.surfaceRaised)
            .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = AstraTheme.colors.accentIndigo, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = AstraTheme.colors.textPrimary)
        }
    }
}
