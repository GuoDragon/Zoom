package com.example.zoom.presentation.meetingdetailed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.common.constants.MeetingMediaChangeSources
import com.example.zoom.common.constants.MeetingActionTypes
import com.example.zoom.data.DataRepository
import com.example.zoom.presentation.meetingchatdetailed.MeetingChatDetailedOverlay
import com.example.zoom.presentation.meetinginfodetailed.MeetingInfoOverlay
import com.example.zoom.presentation.meetingmorepages.MeetingAppsScreen
import com.example.zoom.presentation.meetingmorepages.MeetingHostToolsOverlay
import com.example.zoom.presentation.meetingmorepages.MeetingMorePage
import com.example.zoom.presentation.meetingmorepages.MeetingNotesScreen
import com.example.zoom.presentation.meetingmorepages.MeetingSettingsOverlay
import com.example.zoom.presentation.meetingmorepages.MeetingShareOverlay
import com.example.zoom.presentation.meetingmorepages.MeetingShowCcToast
import com.example.zoom.presentation.meetingmoredetailed.MeetingFloatingEmojiReaction
import com.example.zoom.presentation.meetingmoredetailed.MeetingMoreDetailedOverlay
import com.example.zoom.presentation.meetingparticipantsdetailed.MeetingParticipantsDetailedOverlay
import com.example.zoom.presentation.meetingspeakerdetailed.ParticipantUi
import com.example.zoom.ui.components.MeetingAudioMenu
import com.example.zoom.ui.components.MeetingAudioOption
import com.example.zoom.ui.components.MeetingMediaToggleButton
import com.example.zoom.ui.components.MeetingSessionConfig
import com.example.zoom.ui.theme.ZoomBlue

private enum class MeetingPageMode {
    MAIN,
    SAFE_DRIVING
}

private data class MeetingEmojiReaction(
    val id: Int,
    val emoji: String
)

@Composable
fun MeetingDetailedScreen(
    initialConfig: MeetingSessionConfig,
    onMinimizeClick: () -> Unit,
    onEndClick: () -> Unit,
    onInfoClick: () -> Unit = {}
) {
    var uiState by remember { mutableStateOf<MeetingDetailedUiState?>(null) }
    var showChatOverlay by remember { mutableStateOf(false) }
    var showMoreOverlay by remember { mutableStateOf(false) }
    var showParticipantsOverlay by remember { mutableStateOf(false) }
    var showMeetingInfoOverlay by remember { mutableStateOf(false) }
    var activeMeetingMorePage by remember { mutableStateOf<MeetingMorePage?>(null) }
    var showClosedCaptionToast by remember { mutableStateOf(false) }
    var pageMode by remember { mutableStateOf(MeetingPageMode.MAIN) }
    var isSpeakerView by remember { mutableStateOf(false) }
    var isHandRaised by remember { mutableStateOf(false) }
    var nextEmojiReactionId by remember { mutableIntStateOf(0) }
    var activeEmojiReaction by remember { mutableStateOf<MeetingEmojiReaction?>(null) }
    var safeDrivingSpeechHint by remember { mutableStateOf<String?>(null) }

    val view = remember {
        object : MeetingDetailedContract.View {
            override fun showContent(content: MeetingDetailedUiState) {
                uiState = content
                safeDrivingSpeechHint = null
            }
        }
    }

    LaunchedEffect(Unit) {
        MeetingDetailedPresenter(view).loadData()
    }

    uiState?.let { screenState ->
        val meetingId = DataRepository.getCurrentMeeting().meetingId
        var microphoneOn by remember(initialConfig) { mutableStateOf(initialConfig.microphoneOn) }
        var cameraOn by remember(initialConfig) { mutableStateOf(initialConfig.cameraOn) }
        var selectedAudioOption by remember(initialConfig) { mutableStateOf(initialConfig.audioOption) }
        var showAudioMenu by remember { mutableStateOf(false) }
        fun recordMediaStateChange(
            updatedMicrophoneOn: Boolean = microphoneOn,
            updatedCameraOn: Boolean = cameraOn,
            updatedAudioOption: MeetingAudioOption = selectedAudioOption,
            mediaChangeSource: String
        ) {
            DataRepository.recordCurrentMeetingMediaStateChanged(
                microphoneOn = updatedMicrophoneOn,
                cameraOn = updatedCameraOn,
                audioOption = updatedAudioOption.routeValue,
                mediaChangeSource = mediaChangeSource
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = Color.Black,
                contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                bottomBar = {
                    if (pageMode == MeetingPageMode.MAIN) {
                        MeetingControlBar(
                            microphoneOn = microphoneOn,
                            cameraOn = cameraOn,
                            onMicrophoneClick = {
                                val updatedMicrophoneOn = !microphoneOn
                                microphoneOn = updatedMicrophoneOn
                                recordMediaStateChange(
                                    updatedMicrophoneOn = updatedMicrophoneOn,
                                    mediaChangeSource = MeetingMediaChangeSources.MAIN_MICROPHONE_BUTTON
                                )
                            },
                            onCameraClick = {
                                val updatedCameraOn = !cameraOn
                                cameraOn = updatedCameraOn
                                recordMediaStateChange(
                                    updatedCameraOn = updatedCameraOn,
                                    mediaChangeSource = MeetingMediaChangeSources.MAIN_CAMERA_BUTTON
                                )
                            },
                            onChatClick = { showChatOverlay = true },
                            onMoreClick = { showMoreOverlay = true },
                            onEndClick = onEndClick
                        )
                    }
                }
            ) { padding ->
                when (pageMode) {
                    MeetingPageMode.MAIN -> MeetingMainPageContent(
                        screenState = screenState,
                        padding = padding,
                        showAudioMenu = showAudioMenu,
                        selectedAudioOption = selectedAudioOption,
                        microphoneOn = microphoneOn,
                        isSpeakerView = isSpeakerView,
                        onMinimizeClick = onMinimizeClick,
                        onTitleClick = onInfoClick,
                        onSpeakerClick = { showAudioMenu = !showAudioMenu },
                        onAudioOptionSelected = { option ->
                            if (option != selectedAudioOption) {
                                recordMediaStateChange(
                                    updatedAudioOption = option,
                                    mediaChangeSource = MeetingMediaChangeSources.MAIN_AUDIO_MENU
                                )
                            }
                            selectedAudioOption = option
                            showAudioMenu = false
                        },
                        onSafeDrivingClick = { pageMode = MeetingPageMode.SAFE_DRIVING },
                        onSwipeRight = { pageMode = MeetingPageMode.SAFE_DRIVING }
                    )
                    MeetingPageMode.SAFE_DRIVING -> MeetingSafeDrivingModeScreen(
                        microphoneOn = microphoneOn,
                        cameraOn = cameraOn,
                        selectedAudioOption = selectedAudioOption,
                        showAudioMenu = showAudioMenu,
                        onSpeakerClick = { showAudioMenu = !showAudioMenu },
                        onAudioOptionSelected = { option ->
                            if (option != selectedAudioOption) {
                                recordMediaStateChange(
                                    updatedAudioOption = option,
                                    mediaChangeSource = MeetingMediaChangeSources.SAFE_DRIVING_AUDIO_MENU
                                )
                            }
                            selectedAudioOption = option
                            showAudioMenu = false
                        },
                        speechHint = safeDrivingSpeechHint,
                        onSpeakHello = {
                            safeDrivingSpeechHint = "Last voice input: Hello"
                            DataRepository.recordMeetingAction(
                                actionType = MeetingActionTypes.SAFE_DRIVING_VOICE_NOTE,
                                meetingId = meetingId,
                                note = "Hello"
                            )
                        },
                        onEndClick = onEndClick,
                        onSwipeBack = { pageMode = MeetingPageMode.MAIN }
                    )
                }
            }

            // More overlay
            if (showChatOverlay) {
                MeetingChatDetailedOverlay(
                    meetingTitle = screenState.title,
                    onDismiss = { showChatOverlay = false }
                )
            }

            if (showMoreOverlay) {
                MeetingMoreDetailedOverlay(
                    isHandRaised = isHandRaised,
                    onHandToggle = {
                        val raisingHand = !isHandRaised
                        isHandRaised = raisingHand
                        DataRepository.recordMeetingAction(
                            actionType = if (raisingHand) {
                                MeetingActionTypes.RAISE_HAND
                            } else {
                                MeetingActionTypes.LOWER_HAND
                            },
                            meetingId = meetingId
                        )
                    },
                    onEmojiSelected = { emoji ->
                        nextEmojiReactionId += 1
                        activeEmojiReaction = MeetingEmojiReaction(
                            id = nextEmojiReactionId,
                            emoji = emoji
                        )
                        DataRepository.recordMeetingAction(
                            actionType = MeetingActionTypes.EMOJI_REACTION,
                            meetingId = meetingId,
                            emoji = emoji
                        )
                        showMoreOverlay = false
                    },
                    onParticipantsClick = { showMoreOverlay = false; showParticipantsOverlay = true },
                    onShareClick = {
                        showMoreOverlay = false
                        activeMeetingMorePage = MeetingMorePage.SHARE
                    },
                    onShowCcClick = {
                        showMoreOverlay = false
                        showClosedCaptionToast = true
                    },
                    onNotesClick = {
                        showMoreOverlay = false
                        activeMeetingMorePage = MeetingMorePage.NOTES
                    },
                    onAppsClick = {
                        showMoreOverlay = false
                        activeMeetingMorePage = MeetingMorePage.APPS
                    },
                    onMeetingInfoClick = { showMoreOverlay = false; showMeetingInfoOverlay = true },
                    onHostToolsClick = {
                        showMoreOverlay = false
                        activeMeetingMorePage = MeetingMorePage.HOST_TOOLS
                    },
                    onSettingsClick = {
                        showMoreOverlay = false
                        activeMeetingMorePage = MeetingMorePage.SETTINGS
                    },
                    onDismiss = { showMoreOverlay = false }
                )
            }

            // Participants overlay
            if (showParticipantsOverlay) {
                MeetingParticipantsDetailedOverlay(
                    onDismiss = { showParticipantsOverlay = false }
                )
            }

            if (showMeetingInfoOverlay) {
                MeetingInfoOverlay(
                    onDismiss = { showMeetingInfoOverlay = false }
                )
            }

            when (activeMeetingMorePage) {
                MeetingMorePage.SHARE -> MeetingShareOverlay(
                    onDismiss = { activeMeetingMorePage = null },
                    onShareScreenChanged = { enabled ->
                        DataRepository.setCurrentMeetingScreenShareEnabled(enabled)
                    }
                )
                MeetingMorePage.NOTES -> MeetingNotesScreen(
                    onClose = { activeMeetingMorePage = null }
                )
                MeetingMorePage.APPS -> MeetingAppsScreen(
                    onClose = { activeMeetingMorePage = null }
                )
                MeetingMorePage.HOST_TOOLS -> MeetingHostToolsOverlay(
                    onDismiss = { activeMeetingMorePage = null }
                )
                MeetingMorePage.SETTINGS -> MeetingSettingsOverlay(
                    onDismiss = { activeMeetingMorePage = null }
                )
                null -> Unit
            }

            MeetingShowCcToast(
                visible = showClosedCaptionToast,
                onAutoDismiss = { showClosedCaptionToast = false }
            )

            activeEmojiReaction?.let { reaction ->
                key(reaction.id) {
                    MeetingFloatingEmojiReaction(
                        emoji = reaction.emoji,
                        fontSize = 42.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 176.dp),
                        onFinished = {
                            if (activeEmojiReaction?.id == reaction.id) {
                                activeEmojiReaction = null
                            }
                        }
                    )
                }
            }

            // Floating "Lower hand" capsule
            if (isHandRaised) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF2A2A2D))
                        .clickable {
                            isHandRaised = false
                            DataRepository.recordMeetingAction(
                                actionType = MeetingActionTypes.LOWER_HAND,
                                meetingId = meetingId
                            )
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Lower hand",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MeetingMainPageContent(
    screenState: MeetingDetailedUiState,
    padding: androidx.compose.foundation.layout.PaddingValues,
    showAudioMenu: Boolean,
    selectedAudioOption: MeetingAudioOption,
    microphoneOn: Boolean,
    isSpeakerView: Boolean,
    onMinimizeClick: () -> Unit,
    onTitleClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    onAudioOptionSelected: (MeetingAudioOption) -> Unit,
    onSafeDrivingClick: () -> Unit,
    onSwipeRight: () -> Unit
) {
    var totalHorizontalDrag by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(padding)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { totalHorizontalDrag = 0f },
                    onDragCancel = { totalHorizontalDrag = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        totalHorizontalDrag += dragAmount
                    },
                    onDragEnd = {
                        if (totalHorizontalDrag > 90f) {
                            onSwipeRight()
                        }
                        totalHorizontalDrag = 0f
                    }
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MeetingDetailedTopBar(
                title = screenState.title,
                showAudioMenu = showAudioMenu,
                selectedAudioOption = selectedAudioOption,
                onMinimizeClick = onMinimizeClick,
                onTitleClick = onTitleClick,
                onSpeakerClick = onSpeakerClick,
                onAudioOptionSelected = onAudioOptionSelected
            )
            TopStatusRow(microphoneOn = microphoneOn)
        }

        if (isSpeakerView) {
            SpeakerViewContent(
                participants = screenState.participants,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(124.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF78A93A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = screenState.participantInitials,
                        color = Color.White,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = screenState.participantLabel,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 84.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 112.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2A2A2D))
                    .clickable(onClick = onSafeDrivingClick)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Safe driving",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SpeakerViewContent(
    participants: List<ParticipantUi>,
    modifier: Modifier = Modifier
) {
    val activeSpeaker = participants.firstOrNull { it.isActiveSpeaker } ?: participants.firstOrNull()
    val otherParticipants = participants.filter { it.userId != activeSpeaker?.userId }

    Column(
        modifier = modifier.padding(top = 90.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Large speaker tile
        activeSpeaker?.let { speaker ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF78A93A))
                            .border(3.dp, Color(0xFF5CB85C), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = speaker.initials,
                            color = Color.White,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = speaker.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Thumbnail strip
        if (otherParticipants.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(otherParticipants, key = { it.userId }) { participant ->
                    ParticipantThumbnail(participant = participant)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ParticipantThumbnail(participant: ParticipantUi) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 64.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1E1E1E))
                .then(
                    if (participant.isActiveSpeaker) {
                        Modifier.border(2.dp, Color(0xFF5CB85C), RoundedCornerShape(10.dp))
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = participant.initials,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = participant.name.split(" ").first(),
            color = Color(0xFFAAAAAA),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MeetingDetailedTopBar(
    title: String,
    showAudioMenu: Boolean,
    selectedAudioOption: MeetingAudioOption,
    onMinimizeClick: () -> Unit,
    onTitleClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    onAudioOptionSelected: (MeetingAudioOption) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161616))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMinimizeClick) {
                Icon(
                    imageVector = Icons.Default.CloseFullscreen,
                    contentDescription = "Minimize",
                    tint = Color.White
                )
            }
            Text(
                text = title,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onTitleClick)
            )
            IconButton(
                onClick = onSpeakerClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF2E2E31))
            ) {
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
        }

        if (showAudioMenu) {
            MeetingAudioMenu(
                selectedOption = selectedAudioOption,
                onOptionSelected = onAudioOptionSelected,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 64.dp, end = 14.dp)
                    .width(208.dp)
            )
        }
    }
}

@Composable
private fun TopStatusRow(microphoneOn: Boolean) {
    Row(
        modifier = Modifier
            .padding(start = 14.dp, top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusBadge(
            icon = if (microphoneOn) Icons.Default.Mic else Icons.Default.MicOff,
            containerColor = Color(0xFF1E1E1E),
            tint = if (microphoneOn) ZoomBlue else Color(0xFFE75563)
        )
        StatusBadge(
            icon = Icons.Default.CheckCircle,
            containerColor = Color(0xFF0F2213),
            tint = Color(0xFF5BC26B)
        )
    }
}

@Composable
private fun StatusBadge(
    icon: ImageVector,
    containerColor: Color,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun MeetingControlBar(
    microphoneOn: Boolean,
    cameraOn: Boolean,
    onMicrophoneClick: () -> Unit,
    onCameraClick: () -> Unit,
    onChatClick: () -> Unit,
    onMoreClick: () -> Unit,
    onEndClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161616))
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MeetingAudioControlItem(
            label = if (microphoneOn) "Mute" else "Unmute",
            enabled = microphoneOn,
            activeIcon = Icons.Default.Mic,
            inactiveIcon = Icons.Default.MicOff,
            onClick = onMicrophoneClick
        )
        MeetingAudioControlItem(
            label = if (cameraOn) "Stop video" else "Start video",
            enabled = cameraOn,
            activeIcon = Icons.Default.Videocam,
            inactiveIcon = Icons.Default.VideocamOff,
            onClick = onCameraClick
        )
        MeetingStaticControlItem(
            label = "Chat",
            icon = Icons.AutoMirrored.Filled.Chat,
            onClick = onChatClick
        )
        MeetingStaticControlItem(
            label = "More",
            icon = Icons.Default.MoreHoriz,
            onClick = onMoreClick
        )
        MeetingEndControlItem(onClick = onEndClick)
    }
}

@Composable
private fun MeetingAudioControlItem(
    label: String,
    enabled: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MeetingMediaToggleButton(
            enabled = enabled,
            activeIcon = activeIcon,
            inactiveIcon = inactiveIcon,
            onClick = onClick,
            modifier = Modifier
                .background(Color.Transparent),
            size = 40.dp,
            iconSize = 22.dp,
            containerColor = Color.Transparent
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun MeetingStaticControlItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun MeetingEndControlItem(onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "End",
                    tint = Color(0xFFE73561),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "End",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
