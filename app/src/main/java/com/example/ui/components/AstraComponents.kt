package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AstraTheme
import com.example.ui.theme.AuroraAccentGradient

@Composable
fun AstraLogo(
    modifier: Modifier = Modifier,
    size: Int = 32,
    showTagline: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AuroraAccentGradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Astra AI",
                tint = Color.White,
                modifier = Modifier.size((size * 0.65).dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "ASTRA",
                fontSize = (size * 0.58).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = AstraTheme.colors.textPrimary
            )
            if (showTagline) {
                Text(
                    text = "Your Intelligent Personal Assistant",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = AstraTheme.colors.textTertiary
                )
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val (color, label) = when (priority.uppercase()) {
        "HIGH" -> AstraTheme.colors.priorityHigh to "HIGH"
        "LOW" -> AstraTheme.colors.priorityLow to "LOW"
        else -> AstraTheme.colors.priorityMedium to "MEDIUM"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.8.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 7.dp, vertical = 2.5.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, label) = when (status.uppercase()) {
        "COMPLETED", "READY" -> AstraTheme.colors.success to status.replace("_", " ")
        "IN_PROGRESS", "PROCESSING" -> AstraTheme.colors.accentCyan to status.replace("_", " ")
        "FAILED" -> AstraTheme.colors.error to "FAILED"
        else -> AstraTheme.colors.textTertiary to "PENDING"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 7.dp, vertical = 2.5.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color = AstraTheme.colors.accentIndigo,
    subtitle: String = ""
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = AstraTheme.colors.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderSubtle)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = AstraTheme.colors.textTertiary
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AstraTheme.colors.textPrimary
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = AstraTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
fun StreamingShimmerIndicator(
    text: String = "Astra is thinking..."
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AstraTheme.colors.surfaceRaised)
            .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(AuroraAccentGradient, alpha = alpha)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = AstraTheme.colors.accentCyan.copy(alpha = alpha)
        )
    }
}

@Composable
fun MarkdownContentView(
    content: String,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val sections = content.split("```")

    Column(modifier = modifier) {
        sections.forEachIndexed { index, section ->
            if (index % 2 == 1) {
                // Code block
                val lines = section.trim().lines()
                val lang = if (lines.isNotEmpty() && !lines.first().contains(" ")) lines.first() else "code"
                val codeBody = if (lines.size > 1) lines.drop(1).joinToString("\n") else section

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AstraTheme.colors.surfaceRaised,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AstraTheme.colors.borderStrong),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AstraTheme.colors.surfaceHover)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.uppercase(),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AstraTheme.colors.accentIndigo,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { clipboard.setText(AnnotatedString(codeBody)) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = AstraTheme.colors.textTertiary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Text(
                            text = codeBody,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = AstraTheme.colors.textPrimary,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                // Regular markdown text
                if (section.isNotBlank()) {
                    Text(
                        text = section.trim(),
                        fontSize = 14.sp,
                        color = AstraTheme.colors.textPrimary,
                        lineHeight = 21.sp,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }
    }
}
