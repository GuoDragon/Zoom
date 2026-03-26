package com.example.zoom.presentation.meetingmoredetailed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

@Composable
fun MeetingAnimatedEmojiButton(
    emoji: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    fontSize: TextUnit = 22.sp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(containerColor)
        )

        Text(
            text = emoji,
            fontSize = fontSize
        )
    }
}

@Composable
fun MeetingFloatingEmojiReaction(
    emoji: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit,
    onFinished: () -> Unit
) {
    var launched by remember { mutableStateOf(false) }

    val translationYAnim by animateFloatAsState(
        targetValue = if (launched) -56f else -6f,
        animationSpec = tween(durationMillis = 720, easing = FastOutSlowInEasing),
        finishedListener = {
            if (launched) onFinished()
        },
        label = "emojiTranslation"
    )
    val alphaAnim by animateFloatAsState(
        targetValue = if (launched) 0f else 1f,
        animationSpec = tween(durationMillis = 720, easing = FastOutSlowInEasing),
        label = "emojiAlpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (launched) 1.3f else 1f,
        animationSpec = tween(durationMillis = 720, easing = FastOutSlowInEasing),
        label = "emojiScale"
    )

    LaunchedEffect(Unit) {
        launched = true
    }

    Text(
        text = emoji,
        fontSize = fontSize,
        modifier = modifier
            .graphicsLayer {
                translationY = translationYAnim
                alpha = alphaAnim
                scaleX = scaleAnim
                scaleY = scaleAnim
            }
    )
}
