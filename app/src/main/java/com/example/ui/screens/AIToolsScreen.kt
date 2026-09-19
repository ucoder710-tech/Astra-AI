package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MarkdownContentView
import com.example.ui.components.StreamingShimmerIndicator
import com.example.ui.theme.AstraTheme
import com.example.ui.theme.AuroraAccentGradient
import com.example.ui.viewmodel.ToolType

@Composable
fun AIToolsScreen(
    selectedTool: ToolType,
    input1: String,
    input2: String,
    output: String?,
    isLoading: Boolean,
    errorMessage: String?,
    onSelectTool: (ToolType) -> Unit,
    onInput1Changed: (String) -> Unit,
    onInput2Changed: (String) -> Unit,
    onRunTool: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AstraTheme.colors.canvas)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "AI PRODUCTIVITY SUITE",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AstraTheme.colors.textPrimary
        )
        Text(
            text = "8 specialized AI tools with Google Gemini intelligence",
            fontSize = 12.sp,
            color = AstraTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tool Selector Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 8.dp)
        ) {
            items(ToolType.values()) { tool ->
                val isSelected = tool == selectedTool
                val icon = getToolIcon(tool)

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) AstraTheme.colors.surfaceHover
                            else AstraTheme.colors.surface
                        )
                        .border(
                            1.dp,
                            if (isSelected) AstraTheme.colors.accentIndigo
                            else AstraTheme.colors.borderSubtle,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectTool(tool) }
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) AstraTheme.colors.accentIndigo else AstraTheme.colors.textTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tool.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) AstraTheme.colors.textPrimary else AstraTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AstraTheme.colors.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = selectedTool.label.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = AstraTheme.colors.accentIndigo
                        )
                        Text(
                            text = selectedTool.description,
                            fontSize = 13.sp,
                            color = AstraTheme.colors.textSecondary,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        // Secondary Options Field
                        val (label2, placeholder2) = getSecondaryFieldLabels(selectedTool)
                        if (label2 != null) {
                            OutlinedTextField(
                                value = input2,
                                onValueChange = onInput2Changed,
                                label = { Text(label2, fontSize = 12.sp) },
                                placeholder = { Text(placeholder2 ?: "", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AstraTheme.colors.accentIndigo,
                                    unfocusedBorderColor = AstraTheme.colors.borderSubtle
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Primary Input Field
                        OutlinedTextField(
                            value = input1,
                            onValueChange = onInput1Changed,
                            label = { Text(getPrimaryFieldLabel(selectedTool), fontSize = 12.sp) },
                            placeholder = { Text(getPrimaryFieldPlaceholder(selectedTool), fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AstraTheme.colors.accentIndigo,
                                unfocusedBorderColor = AstraTheme.colors.borderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Execute Button
                        Button(
                            onClick = onRunTool,
                            enabled = !isLoading && input1.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AuroraAccentGradient),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Run ${selectedTool.label}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    StreamingShimmerIndicator("Astra is running ${selectedTool.label}...")
                }
            }

            if (errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AstraTheme.colors.errorBg)
                            .border(1.dp, AstraTheme.colors.error.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(text = errorMessage, color = AstraTheme.colors.error, fontSize = 13.sp)
                    }
                }
            }

            if (output != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AstraTheme.colors.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderStrong),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AstraTheme.colors.accentCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Generated Output", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AstraTheme.colors.accentCyan)
                                }
                                IconButton(
                                    onClick = { clipboard.setText(AnnotatedString(output)) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AstraTheme.colors.textTertiary, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            MarkdownContentView(content = output)
                        }
                    }
                }
            }
        }
    }
}

fun getToolIcon(tool: ToolType): ImageVector = when (tool) {
    ToolType.SUMMARIZER -> Icons.Default.ShortText
    ToolType.GRAMMAR_CHECKER -> Icons.Default.Spellcheck
    ToolType.TRANSLATOR -> Icons.Default.Translate
    ToolType.EMAIL_WRITER -> Icons.Default.Email
    ToolType.CONTENT_WRITER -> Icons.Default.Edit
    ToolType.CODE_ASSISTANT -> Icons.Default.Code
    ToolType.MCQ_GENERATOR -> Icons.Default.Quiz
    ToolType.QUESTION_GENERATOR -> Icons.Default.HelpOutline
}

fun getSecondaryFieldLabels(tool: ToolType): Pair<String?, String?> = when (tool) {
    ToolType.SUMMARIZER -> Pair("Summary Format", "e.g. Bullet Points, Executive Brief, 1-Paragraph")
    ToolType.TRANSLATOR -> Pair("Target Language", "e.g. Spanish, German, Japanese, French")
    ToolType.EMAIL_WRITER -> Pair("Tone / Style", "e.g. Formal, Persuasive, Friendly, Urgent")
    ToolType.CONTENT_WRITER -> Pair("Target Audience", "e.g. Engineers, Executives, Beginners")
    ToolType.CODE_ASSISTANT -> Pair("Programming Language", "e.g. Kotlin, Python, TypeScript, SQL")
    ToolType.MCQ_GENERATOR -> Pair("Question Count", "e.g. 3, 5, 10")
    ToolType.QUESTION_GENERATOR -> Pair("Question Count", "e.g. 5, 8")
    else -> Pair(null, null)
}

fun getPrimaryFieldLabel(tool: ToolType): String = when (tool) {
    ToolType.SUMMARIZER -> "Text to Summarize"
    ToolType.GRAMMAR_CHECKER -> "Text to Proofread & Polish"
    ToolType.TRANSLATOR -> "Text to Translate"
    ToolType.EMAIL_WRITER -> "Key Notes / Goal of Email"
    ToolType.CONTENT_WRITER -> "Topic or Outline"
    ToolType.CODE_ASSISTANT -> "Problem Description or Code snippet"
    ToolType.MCQ_GENERATOR -> "Topic or Source Text for Quiz"
    ToolType.QUESTION_GENERATOR -> "Topic or Subject Material"
}

fun getPrimaryFieldPlaceholder(tool: ToolType): String = when (tool) {
    ToolType.CODE_ASSISTANT -> "e.g. Write a Room DAO with Coroutines Flow for tasks"
    ToolType.EMAIL_WRITER -> "e.g. Request meeting reschedule with client to next Tuesday at 3pm"
    ToolType.MCQ_GENERATOR -> "e.g. Operating System Deadlocks and Banker's Algorithm"
    else -> "Enter your text or prompt here..."
}
