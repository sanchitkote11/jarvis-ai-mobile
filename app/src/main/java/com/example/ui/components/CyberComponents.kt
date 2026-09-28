package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackgroundElevated
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardBorderGlow
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
    headerAction: (@Composable () -> Unit)? = null,
    borderColor: Color = CyberCardBorder,
    glowBorder: Boolean = false,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_card_liquid_flow")
    val liquidPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "liquid_phase"
    )

    val borderBrush = if (glowBorder) {
        Brush.horizontalGradient(
            colors = listOf(
                CyberCardBorderGlow,
                CyberCardBorder,
                NeonCyanLight,
                CyberCardBorder,
                CyberCardBorderGlow
            ),
            startX = liquidPhase * 600f,
            endX = liquidPhase * 600f + 600f
        )
    } else {
        Brush.linearGradient(
            listOf(borderColor, borderColor.copy(alpha = 0.45f))
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(1.dp, borderBrush),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = CyberSurface.copy(alpha = 0.85f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            if (title != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    headerAction?.invoke()
                }
            }
            content()
        }
    }
}

@Composable
fun CyberActionButton(
    icon: ImageVector,
    label: String,
    subLabel: String,
    accentColor: Color = NeonCyan,
    modifier: Modifier = Modifier,
    testTag: String = "action_btn_$label",
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 64.dp)
            .testTag(testTag)
            .clip(RoundedCornerShape(14.dp))
            .border(
                BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accentColor),
                onClick = onClick
            ),
        shape = RoundedCornerShape(14.dp),
        color = CyberBackgroundElevated.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = subLabel,
                color = TextSecondary,
                fontSize = 8.5.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CyberToolTile(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    activeColor: Color = NeonCyan,
    modifier: Modifier = Modifier,
    testTag: String = "tool_$label",
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .border(
                BorderStroke(
                    1.dp,
                    if (isActive) activeColor else CyberCardBorder.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = activeColor),
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) activeColor.copy(alpha = 0.15f) else CyberSurface.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = if (isActive) TextPrimary else TextSecondary,
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CyberBadge(
    text: String,
    color: Color = NeonCyan,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(BorderStroke(0.8.dp, color.copy(alpha = 0.4f)), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
        }
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
