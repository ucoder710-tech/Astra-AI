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
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityLogEntity
import com.example.ui.theme.AstraTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    activities: List<ActivityLogEntity>,
    typeFilter: String,
    searchQuery: String,
    onTypeFilterChanged: (String) -> Unit,
    onSearchChanged: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AstraTheme.colors.canvas)
            .padding(16.dp)
    ) {
        Text(
            text = "SYSTEM HISTORY & AUDIT LOG",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AstraTheme.colors.textPrimary
        )
        Text(
            text = "Track all activities across chat, document RAG, tools and tasks",
            fontSize = 12.sp,
            color = AstraTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text("Search activity logs...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AstraTheme.colors.textTertiary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AstraTheme.colors.accentIndigo,
                unfocusedBorderColor = AstraTheme.colors.borderSubtle
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val types = listOf("ALL", "CHAT", "DOCUMENT", "TOOL", "TASK", "USER")
            items(types) { t ->
                val isSelected = t == typeFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) AstraTheme.colors.accentIndigo else AstraTheme.colors.surface)
                        .border(1.dp, if (isSelected) AstraTheme.colors.accentIndigo else AstraTheme.colors.borderSubtle, RoundedCornerShape(6.dp))
                        .clickable { onTypeFilterChanged(t) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (t == "ALL") "All Activities" else t,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else AstraTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (activities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No activity logs found.",
                            color = AstraTheme.colors.textTertiary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            items(activities) { act ->
                val icon = when (act.actionType) {
                    "CHAT" -> Icons.Default.ChatBubbleOutline
                    "DOCUMENT" -> Icons.Default.Description
                    "TOOL" -> Icons.Default.AutoAwesome
                    "TASK" -> Icons.Default.CheckCircle
                    else -> Icons.Default.Person
                }
                val iconColor = when (act.actionType) {
                    "CHAT" -> AstraTheme.colors.accentIndigo
                    "DOCUMENT" -> AstraTheme.colors.accentViolet
                    "TOOL" -> AstraTheme.colors.accentCyan
                    "TASK" -> AstraTheme.colors.success
                    else -> AstraTheme.colors.textSecondary
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = AstraTheme.colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(iconColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(17.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = act.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AstraTheme.colors.textPrimary
                            )
                            if (act.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = act.description,
                                    fontSize = 11.sp,
                                    color = AstraTheme.colors.textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = dateFormat.format(Date(act.timestamp)),
                            fontSize = 10.sp,
                            color = AstraTheme.colors.textTertiary
                        )
                    }
                }
            }
        }
    }
}
