package com.example.zoom.presentation.meetingdetailed

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.MeetingAudioMenu
import com.example.zoom.ui.components.MeetingAudioOption
import kotlinx.coroutines.delay

@Composable
fun MeetingSafeDrivingModeScreen(
    microphoneOn: Boolean,
    cameraOn: Boolean,
    selectedAudioOption: MeetingAudioOption,
    showAudioMenu: Boolean,
    onSpeakerClick: () -> Unit,
    onAudioOptionSelected: (MeetingAudioOption) -> Unit,
    speechHint: String?,
    onSpeakHello: () -> Unit,
    onEndClick: () -> Unit,
    onSwipeBack: () -> Unit
) {
    var totalHorizontalDrag by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D1D1F))
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { totalHorizontalDrag = 0f },
                    onDragCancel = { totalHorizontalDrag = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        totalHorizontalDrag += dragAmount
                    },
                    onDragEnd = {
                        if (totalHorizontalDrag < -90f) {
                            onSwipeBack()
                        }
                        totalHorizontalDrag = 0f
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSpeakerClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speaker",
                        tint = if (selectedAudioOption == MeetingAudioOption.NoAudio) {
                            Color(0xFFE65B5B)
                        } else {
                            Color.White
                        }
                    )
                }
                Text(
                    text = "Safe driving mode",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE7344C))
                        .clickable(onClick = onEndClick)
                        .padding(horizontal = 18.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "End",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF2A2A2D))
            )

            Spacer(modifier = Modifier.height(132.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (microphoneOn) "Your microphone is unmuted" else "Your microphone is muted",
                    color = Color(0xFFD9D9DB),
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = if (cameraOn) "Your video is started" else "Your video is stopped",
                    color = Color(0xFFD9D9DB),
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            SafeDrivingSpeakButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onSpeak = onSpeakHello
            )

            if (!speechHint.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = speechHint,
                    color = Color(0xFFE2A330),
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 34.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PageDot(active = true)
                PageDot(active = false)
            }
        }

        if (showAudioMenu) {
            MeetingAudioMenu(
                selectedOption = selectedAudioOption,
                onOptionSelected = onAudioOptionSelected,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 66.dp, start = 12.dp)
            )
        }
    }
}

@Composable
private fun SafeDrivingSpeakButton(
    modifier: Modifier = Modifier,
    onSpeak: () -> Unit
) {
    var pulseNonce by remember { mutableIntStateOf(0) }
    var pulseActive by remember { mutableStateOf(false) }

    LaunchedEffect(pulseNonce) {
        if (pulseNonce == 0) return@LaunchedEffect
        pulseActive = true
        delay(820)
        pulseActive = false
    }

    val outerScale by animateFloatAsState(
        targetValue = if (pulseActive) 1.22f else 1f,
        animationSpec = tween(820),
        label = "outerScale"
    )
    val outerAlpha by animateFloatAsState(
        targetValue = if (pulseActive) 0f else 0.45f,
        animationSpec = tween(820),
        label = "outerAlpha"
    )
    val innerScale by animateFloatAsState(
        targetValue = if (pulseActive) 1.04f else 1f,
        animationSpec = tween(220),
        label = "innerScale"
    )

    Box(
        modifier = modifier
            .size(210.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(210.dp)
                .graphicsLayer {
                    scaleX = outerScale
                    scaleY = outerScale
                    alpha = outerAlpha
                }
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
        )

        Box(
            modifier = Modifier
                .size(186.dp)
                .graphicsLayer {
                    scaleX = innerScale
                    scaleY = innerScale
                }
                .clip(CircleShape)
                .background(Color.Transparent)
                .clickable {
                    pulseNonce += 1
                    onSpeak()
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Transparent)
            )
            Text(
                text = "Tap to\nspeak",
                color = Color(0xFFE2A330),
                fontSize = 26.sp,
                lineHeight = 31.sp,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .size(186.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .graphicsLayer {
                    alpha = 1f
                }
                .border(2.dp, Color.White, CircleShape)
        )
    }
}

@Composable
private fun PageDot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(if (active) 9.dp else 8.dp)
            .clip(CircleShape)
            .background(if (active) Color.White else Color(0x80FFFFFF))
    )
}
