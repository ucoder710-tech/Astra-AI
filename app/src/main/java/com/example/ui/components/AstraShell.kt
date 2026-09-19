package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.AstraTheme
import com.example.ui.theme.AuroraAccentGradient
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

@Composable
fun AstraShell(
    currentUser: UserEntity,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = AstraTheme.colors.surface,
                modifier = Modifier.width(280.dp)
            ) {
                SidebarContent(
                    currentUser = currentUser,
                    currentScreen = currentScreen,
                    onNavigate = {
                        onNavigate(it)
                        scope.launch { drawerState.close() }
                    },
                    onLogout = onLogout
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface(
                    color = AstraTheme.colors.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = AstraTheme.colors.textPrimary)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            AstraLogo(size = 28)
                        }

                        // Right screen badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AstraTheme.colors.surfaceRaised)
                                .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentScreen.name.replace("_", " "),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AstraTheme.colors.accentCyan
                            )
                        }
                    }
                }
            },
            containerColor = AstraTheme.colors.canvas
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SidebarContent(
    currentUser: UserEntity,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(18.dp)
    ) {
        // Logo & Tagline
        AstraLogo(size = 32, showTagline = true)

        Spacer(modifier = Modifier.height(26.dp))

        // ASSIST section
        Text(
            text = "ASSIST",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = AstraTheme.colors.textTertiary
        )
        Spacer(modifier = Modifier.height(8.dp))

        NavItem(
            label = "Dashboard",
            icon = Icons.Default.Dashboard,
            isSelected = currentScreen == Screen.DASHBOARD,
            onClick = { onNavigate(Screen.DASHBOARD) }
        )
        NavItem(
            label = "AI Chat",
            icon = Icons.Default.ChatBubbleOutline,
            isSelected = currentScreen == Screen.CHAT,
            onClick = { onNavigate(Screen.CHAT) }
        )
        NavItem(
            label = "Documents & RAG",
            icon = Icons.Default.Description,
            isSelected = currentScreen == Screen.DOCUMENTS,
            onClick = { onNavigate(Screen.DOCUMENTS) }
        )
        NavItem(
            label = "AI Tools",
            icon = Icons.Default.AutoAwesome,
            isSelected = currentScreen == Screen.AI_TOOLS,
            onClick = { onNavigate(Screen.AI_TOOLS) }
        )
        NavItem(
            label = "Tasks",
            icon = Icons.Default.CheckCircle,
            isSelected = currentScreen == Screen.TASKS,
            onClick = { onNavigate(Screen.TASKS) }
        )
        NavItem(
            label = "History",
            icon = Icons.Default.History,
            isSelected = currentScreen == Screen.HISTORY,
            onClick = { onNavigate(Screen.HISTORY) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ACCOUNT section
        Text(
            text = "ACCOUNT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = AstraTheme.colors.textTertiary
        )
        Spacer(modifier = Modifier.height(8.dp))

        NavItem(
            label = "Settings & Profile",
            icon = Icons.Default.Settings,
            isSelected = currentScreen == Screen.SETTINGS || currentScreen == Screen.PROFILE,
            onClick = { onNavigate(Screen.SETTINGS) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // User Avatar pill at bottom
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = AstraTheme.colors.surfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AuroraAccentGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = currentUser.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AstraTheme.colors.textPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "Pro Account",
                            fontSize = 10.sp,
                            color = AstraTheme.colors.accentCyan
                        )
                    }
                }
                IconButton(onClick = onLogout, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = AstraTheme.colors.textTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AstraTheme.colors.surfaceHover else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) AstraTheme.colors.borderStrong else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) AstraTheme.colors.accentIndigo else AstraTheme.colors.textTertiary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) AstraTheme.colors.textPrimary else AstraTheme.colors.textSecondary
        )
    }
}
