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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.ui.components.MarkdownContentView
import com.example.ui.components.StreamingShimmerIndicator
import com.example.ui.theme.AstraTheme
import com.example.ui.theme.AuroraAccentGradient

@Composable
fun ChatScreen(
    conversations: List<ConversationEntity>,
    activeConversationId: String?,
    messages: List<MessageEntity>,
    isStreaming: Boolean,
    streamingText: String,
    chatError: String?,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onSelectConversation: (String) -> Unit,
    onCreateNewConversation: () -> Unit,
    onDeleteConversation: (String) -> Unit,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val clipboard = LocalClipboardManager.current

    // Auto-scroll when new messages or streaming chunks arrive
    LaunchedEffect(messages.size, streamingText.length) {
        val totalCount = messages.size + (if (isStreaming || streamingText.isNotEmpty()) 1 else 0)
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AstraTheme.colors.canvas)
    ) {
        // Conversation Horizontal Carousel / Selector
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AstraTheme.colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONVERSATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AstraTheme.colors.textTertiary
                    )
                    IconButton(
                        onClick = onCreateNewConversation,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New chat",
                            tint = AstraTheme.colors.accentIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    items(conversations) { conv ->
                        val isSelected = conv.id == activeConversationId
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) AstraTheme.colors.surfaceHover
                                    else AstraTheme.colors.surfaceRaised
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AstraTheme.colors.accentIndigo
                                    else AstraTheme.colors.borderSubtle,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectConversation(conv.id) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = conv.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) AstraTheme.colors.textPrimary else AstraTheme.colors.textSecondary,
                                maxLines = 1
                            )
                            if (isSelected && conversations.size > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = AstraTheme.colors.textTertiary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onDeleteConversation(conv.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chat Error Banner
        if (chatError != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AstraTheme.colors.errorBg)
                    .border(1.dp, AstraTheme.colors.error.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Text(
                    text = chatError,
                    fontSize = 12.sp,
                    color = AstraTheme.colors.error
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (messages.isEmpty() && !isStreaming) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(AuroraAccentGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Start a conversation with Astra",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AstraTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ask complex questions, brainstorm ideas, request code, or summarize knowledge.",
                                fontSize = 13.sp,
                                color = AstraTheme.colors.textSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(messages) { message ->
                MessageBubble(
                    message = message,
                    onCopy = { clipboard.setText(AnnotatedString(message.content)) },
                    onRegenerate = { onSendMessage(message.content) }
                )
            }

            // Real Streaming Bubble
            if (isStreaming || streamingText.isNotEmpty()) {
                item {
                    Column {
                        StreamingShimmerIndicator("Astra is streaming response...")
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            MarkdownContentView(
                                content = streamingText.ifEmpty { "Generating insights..." }
                            )
                        }
                    }
                }
            }
        }

        // Input Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AstraTheme.colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask Astra anything...",
                            fontSize = 14.sp,
                            color = AstraTheme.colors.textTertiary
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AstraTheme.colors.accentIndigo,
                        unfocusedBorderColor = AstraTheme.colors.borderSubtle
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .then(
                            if (inputText.isNotBlank() && !isStreaming) Modifier.background(AuroraAccentGradient)
                            else Modifier.background(AstraTheme.colors.surfaceRaised)
                        )
                        .clickable(enabled = inputText.isNotBlank() && !isStreaming) {
                            val msg = inputText.trim()
                            inputText = ""
                            onSendMessage(msg)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isStreaming) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = AstraTheme.colors.accentCyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) Color.White else AstraTheme.colors.textTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: MessageEntity,
    onCopy: () -> Unit,
    onRegenerate: () -> Unit
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = if (isUser) "YOU" else "ASTRA AI",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = if (isUser) AstraTheme.colors.textTertiary else AstraTheme.colors.accentCyan
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.85f else 1f)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isUser) AstraTheme.colors.chatUserBubble
                    else AstraTheme.colors.chatAssistantBubble
                )
                .border(
                    1.dp,
                    if (isUser) AstraTheme.colors.borderStrong
                    else AstraTheme.colors.borderSubtle,
                    RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            MarkdownContentView(content = message.content)
        }

        // Action icons for Assistant messages
        if (!isUser) {
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onCopy, modifier = Modifier.size(26.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = AstraTheme.colors.textTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
