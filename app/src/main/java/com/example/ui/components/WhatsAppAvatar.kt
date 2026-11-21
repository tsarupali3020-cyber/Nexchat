package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MetaAiBlue
import com.example.ui.theme.MetaAiCyan
import com.example.ui.theme.MetaAiPink
import com.example.ui.theme.MetaAiPurple
import com.example.ui.theme.WhatsAppGreenAccent

@Composable
fun WhatsAppAvatar(
    name: String,
    isGroup: Boolean = false,
    isAiChat: Boolean = false,
    isOnline: Boolean = false,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (isAiChat) {
            // Meta AI Animated Circular Gradient Ring
            val infiniteTransition = rememberInfiniteTransition(label = "ai_ring")
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "ai_ring_angle"
            )

            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .rotate(angle)
                    .background(
                        Brush.sweepGradient(
                            listOf(
                                MetaAiCyan,
                                MetaAiBlue,
                                MetaAiPurple,
                                MetaAiPink,
                                MetaAiCyan
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .size(size - 6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    color = MetaAiBlue,
                    fontSize = (size.value * 0.35f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            val bgColors = listOf(
                Color(0xFF00A884),
                Color(0xFF1E88E5),
                Color(0xFFE53935),
                Color(0xFF8E24AA),
                Color(0xFFFB8C00),
                Color(0xFF43A047)
            )
            val colorIndex = (name.hashCode() and 0x7FFFFFFF) % bgColors.size
            val bgColor = bgColors[colorIndex]

            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                if (isGroup) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Group",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.55f)
                    )
                } else if (name.isNotBlank()) {
                    val initials = name.split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .map { it.first().uppercase() }
                        .joinToString("")
                    Text(
                        text = initials.ifBlank { "?" },
                        color = Color.White,
                        fontSize = (size.value * 0.4f).sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Person",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.55f)
                    )
                }
            }

            if (isOnline) {
                Box(
                    modifier = Modifier
                        .size(size * 0.3f)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(WhatsAppGreenAccent)
                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }
    }
}
