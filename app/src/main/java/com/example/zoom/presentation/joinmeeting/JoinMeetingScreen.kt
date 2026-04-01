package com.example.zoom.presentation.joinmeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.R
import com.example.zoom.ui.components.MeetingAudioOption
import com.example.zoom.ui.components.MeetingSessionConfig
import com.example.zoom.ui.components.ZoomActionPageTopBar
import com.example.zoom.ui.components.ZoomInsetDivider
import com.example.zoom.ui.components.ZoomPageSurface
import com.example.zoom.ui.components.ZoomPrimaryActionButton
import com.example.zoom.ui.components.ZoomSettingSwitchRow
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomGray
import com.example.zoom.ui.theme.ZoomTextSecondary

@Composable
fun JoinMeetingScreen(
    onBackClick: () -> Unit,
    onJoinMeetingClick: (String, MeetingSessionConfig) -> Unit
) {
    var uiState by remember { mutableStateOf<JoinMeetingUiState?>(null) }

    val view = remember {
        object : JoinMeetingContract.View {
            override fun showContent(content: JoinMeetingUiState) {
                uiState = content
            }
        }
    }
    val presenter = remember(view) { JoinMeetingPresenter(view) }

    LaunchedEffect(Unit) {
        presenter.loadData()
    }

    uiState?.let { screenState ->
        var meetingId by remember { mutableStateOf("") }
        var screenName by remember { mutableStateOf("") }
        var audioOff by remember(screenState) { mutableStateOf(screenState.audioOff) }
        var videoOff by remember(screenState) { mutableStateOf(screenState.videoOff) }
        var showHistorySheet by remember { mutableStateOf(false) }
        var historyItems by remember(screenState) { mutableStateOf(screenState.historyItems) }

        val isMeetingIdValid = meetingId.length == 9

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    ZoomActionPageTopBar(
                        title = stringResource(R.string.join_meeting_title),
                        onCancelClick = onBackClick
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ZoomGray)
                        .padding(padding)
                ) {
                    ZoomPageSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        MeetingIdField(
                            value = meetingId,
                            onValueChange = { input ->
                                meetingId = input.filter { it.isDigit() }.take(9)
                            },
                            onHistoryClick = {
                                historyItems = presenter.refreshHistory()
                                showHistorySheet = true
                            }
                        )
                        Text(
                            text = stringResource(R.string.join_meeting_personal_link),
                            color = ZoomBlue,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SimpleInputField(
                        value = screenName,
                        onValueChange = { screenName = it },
                        placeholder = stringResource(R.string.join_meeting_screen_name_placeholder)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ZoomPrimaryActionButton(
                        text = stringResource(R.string.join_meeting_action_join),
                        onClick = {
                            val preparedMeetingId = presenter.prepareJoinByMeetingNumber(meetingNumber = meetingId)
                            historyItems = presenter.refreshHistory()
                            onJoinMeetingClick(
                                preparedMeetingId,
                                MeetingSessionConfig(
                                    microphoneOn = !audioOff,
                                    cameraOn = !videoOff,
                                    audioOption = if (audioOff) {
                                        MeetingAudioOption.NoAudio
                                    } else {
                                        MeetingAudioOption.WifiOrCellular
                                    }
                                )
                            )
                        },
                        enabled = isMeetingIdValid,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Text(
                        text = stringResource(R.string.join_meeting_invitation_hint),
                        color = ZoomTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = stringResource(R.string.join_meeting_options_title),
                        fontSize = 14.sp,
                        color = ZoomTextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Column(modifier = Modifier.background(Color.White)) {
                        ZoomSettingSwitchRow(
                            title = stringResource(R.string.join_meeting_option_no_audio),
                            checked = audioOff,
                            onCheckedChange = { audioOff = it }
                        )
                        ZoomInsetDivider()
                        ZoomSettingSwitchRow(
                            title = stringResource(R.string.join_meeting_option_video_off),
                            checked = videoOff,
                            onCheckedChange = { videoOff = it }
                        )
                    }
                }
            }

            if (showHistorySheet) {
                JoinMeetingHistoryOverlay(
                    items = historyItems,
                    onItemClick = { item ->
                        meetingId = item.meetingNumber
                        val preparedMeetingId = presenter.prepareJoinByHistoryItem(item)
                        historyItems = presenter.refreshHistory()
                        showHistorySheet = false
                        onJoinMeetingClick(
                            preparedMeetingId,
                            MeetingSessionConfig(
                                microphoneOn = !audioOff,
                                cameraOn = !videoOff,
                                audioOption = if (audioOff) {
                                    MeetingAudioOption.NoAudio
                                } else {
                                    MeetingAudioOption.WifiOrCellular
                                }
                            )
                        )
                    },
                    onClearHistory = {
                        presenter.clearHistory()
                        historyItems = presenter.refreshHistory()
                    },
                    onDoneClick = { showHistorySheet = false },
                    onDismiss = { showHistorySheet = false }
                )
            }
        }
    }
}

@Composable
private fun MeetingIdField(
    value: String,
    onValueChange: (String) -> Unit,
    onHistoryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                fontSize = 18.sp,
                color = Color(0xFF3A4A5F)
            ),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        stringResource(R.string.join_meeting_id_placeholder),
                        color = Color(0xFFA7B0BC),
                        fontSize = 18.sp
                    )
                }
                innerTextField()
            }
        )
        IconButton(
            onClick = onHistoryClick,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.join_meeting_open_history),
                tint = Color(0xFFB6BFCA)
            )
        }
    }
}

@Composable
private fun SimpleInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF3A4A5F)),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(placeholder, color = Color(0xFFA7B0BC), fontSize = 18.sp)
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun JoinMeetingHistoryOverlay(
    items: List<JoinMeetingHistoryItem>,
    onItemClick: (JoinMeetingHistoryItem) -> Unit,
    onClearHistory: () -> Unit,
    onDoneClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val dismissInteraction = remember { MutableInteractionSource() }
    val panelInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.28f))
            .clickable(
                indication = null,
                interactionSource = dismissInteraction,
                onClick = onDismiss
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(Color.White)
                .clickable(
                    indication = null,
                    interactionSource = panelInteraction
                ) {}
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.join_meeting_clear_history),
                    color = ZoomBlue,
                    fontSize = 17.sp,
                    modifier = Modifier.clickable(onClick = onClearHistory)
                )
                Text(
                    text = stringResource(R.string.common_done),
                    color = ZoomBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onDoneClick)
                )
            }

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.join_meeting_no_history),
                        color = ZoomTextSecondary,
                        fontSize = 15.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    items.forEachIndexed { index, item ->
                        JoinMeetingHistoryRow(
                            item = item,
                            onClick = { onItemClick(item) }
                        )
                        if (index != items.lastIndex) {
                            ZoomInsetDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JoinMeetingHistoryRow(
    item: JoinMeetingHistoryItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.title,
            color = Color(0xFF2D2F33),
            fontSize = 17.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = formatMeetingNumber(item.meetingNumber),
            color = Color(0xFF4A4D52),
            fontSize = 17.sp
        )
    }
}

private fun formatMeetingNumber(value: String): String {
    return value.chunked(3).joinToString(" ")
}
