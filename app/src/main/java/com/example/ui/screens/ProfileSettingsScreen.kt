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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.UserEntity
import com.example.ui.theme.AstraTheme
import com.example.ui.theme.AuroraAccentGradient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileSettingsScreen(
    user: UserEntity,
    onUpdateTheme: (String) -> Unit,
    onUpdateAiPreferences: (model: String, style: String) -> Unit,
    onUpdateProfile: (name: String) -> Unit,
    onLogout: () -> Unit
) {
    var editName by remember { mutableStateOf(user.name) }
    var selectedModel by remember { mutableStateOf(user.modelPreference) }
    var selectedStyle by remember { mutableStateOf(user.responseStyle) }
    var showSavedNotification by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AstraTheme.colors.canvas)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PROFILE & SETTINGS",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AstraTheme.colors.textPrimary
        )
        Text(
            text = "Manage your preferences, AI model configuration and identity",
            fontSize = 12.sp,
            color = AstraTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Profile Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = AstraTheme.colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AuroraAccentGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = user.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AstraTheme.colors.textPrimary
                        )
                        Text(
                            text = user.email,
                            fontSize = 13.sp,
                            color = AstraTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Member since ${dateFormat.format(Date(user.createdAt))} • Pro Plan",
                            fontSize = 11.sp,
                            color = AstraTheme.colors.accentCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AstraTheme.colors.accentIndigo,
                        unfocusedBorderColor = AstraTheme.colors.borderSubtle
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            onUpdateProfile(editName.trim())
                            showSavedNotification = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AstraTheme.colors.surfaceRaised),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderStrong),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Name Changes", color = AstraTheme.colors.textPrimary, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Appearance / Theme Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = AstraTheme.colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "APPEARANCE & THEME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = AstraTheme.colors.accentIndigo
                )
                Text(
                    text = "Select your preferred Aurora theme mode (persisted to database)",
                    fontSize = 12.sp,
                    color = AstraTheme.colors.textSecondary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isDark = user.themePreference == "DARK"

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) AstraTheme.colors.surfaceHover else AstraTheme.colors.surfaceRaised)
                            .border(
                                1.5.dp,
                                if (isDark) AstraTheme.colors.accentIndigo else AstraTheme.colors.borderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onUpdateTheme("DARK") }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Brightness4, contentDescription = null, tint = AstraTheme.colors.accentIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Aurora Dark", fontSize = 13.sp, fontWeight = if (isDark) FontWeight.Bold else FontWeight.Normal, color = AstraTheme.colors.textPrimary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isDark) AstraTheme.colors.surfaceHover else AstraTheme.colors.surfaceRaised)
                            .border(
                                1.5.dp,
                                if (!isDark) AstraTheme.colors.accentIndigo else AstraTheme.colors.borderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onUpdateTheme("LIGHT") }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Brightness7, contentDescription = null, tint = AstraTheme.colors.accentViolet, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Aurora Light", fontSize = 13.sp, fontWeight = if (!isDark) FontWeight.Bold else FontWeight.Normal, color = AstraTheme.colors.textPrimary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // AI Intelligence Configuration
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = AstraTheme.colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "AI INTELLIGENCE CONFIGURATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = AstraTheme.colors.accentCyan
                )
                Text(
                    text = "Configure preferred Google Gemini foundation model and chat persona",
                    fontSize = 12.sp,
                    color = AstraTheme.colors.textSecondary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                Text("Primary Model:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AstraTheme.colors.textPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("gemini-2.5-flash", "gemini-3.5-flash").forEach { model ->
                        val isSelected = selectedModel == model
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) AstraTheme.colors.accentIndigo else AstraTheme.colors.surfaceRaised)
                                .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedModel = model
                                    onUpdateAiPreferences(model, selectedStyle)
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = model,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else AstraTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Response Style:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AstraTheme.colors.textPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Balanced", "Concise", "Detailed").forEach { style ->
                        val isSelected = selectedStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) AstraTheme.colors.accentViolet else AstraTheme.colors.surfaceRaised)
                                .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedStyle = style
                                    onUpdateAiPreferences(selectedModel, style)
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = style,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else AstraTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = AstraTheme.colors.errorBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.error.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = AstraTheme.colors.error, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out of Astra", color = AstraTheme.colors.error, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
