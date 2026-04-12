package com.example.zoom.presentation.meetinginfodetailed

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.common.constants.MeetingActionTypes
import com.example.zoom.data.DataRepository

@Composable
fun MeetingInfoOverlay(onDismiss: () -> Unit) {
    MeetingInfoPage(
        onDismiss = onDismiss,
        showScrim = true
    )
}

@Composable
fun MeetingInfoPage(
    onDismiss: () -> Unit,
    showScrim: Boolean
) {
    var uiState by remember { mutableStateOf<MeetingInfoDetailedUiState?>(null) }
    val context = LocalContext.current

    val view = remember {
        object : MeetingInfoDetailedContract.View {
            override fun showContent(content: MeetingInfoDetailedUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        MeetingInfoDetailedPresenter(view).loadData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (showScrim) Color.Black.copy(alpha = 0.5f) else Color.Black)
            .then(
                if (showScrim) {
                    Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() }
                } else {
                    Modifier
                }
            )
    ) {
        uiState?.let { state ->
            MeetingInfoSheet(
                state = state,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.62f),
                onDismiss = onDismiss,
                onCopyMeetingLink = {
                    copyToClipboard(context, state.inviteLink)
                    DataRepository.recordMeetingAction(
                        actionType = MeetingActionTypes.COPY_INVITE_LINK,
                        meetingId = DataRepository.getCurrentMeeting().meetingId,
                        note = state.inviteLink
                    )
                },
                onCopyMeetingNumber = {
                    copyToClipboard(context, state.meetingId)
                    DataRepository.recordMeetingAction(
                        actionType = MeetingActionTypes.COPY_MEETING_NUMBER,
                        meetingId = DataRepository.getCurrentMeeting().meetingId,
                        note = state.meetingId
                    )
                },
                onShare = { shareMeetingInfo(context, buildMeetingShareText(state)) }
            )
        }
    }
}

@Composable
private fun MeetingInfoSheet(
    state: MeetingInfoDetailedUiState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onCopyMeetingLink: () -> Unit,
    onCopyMeetingNumber: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color(0xFF2A2A2D))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {}
            .padding(top = 8.dp, bottom = 20.dp)
    ) {
        DragHandle()

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            Text(
                text = "Meeting info",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = onShare,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = state.topic,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF323840))
                    .clickable(onClick = onCopyMeetingLink)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Copy meeting link",
                    color = Color(0xFF4A90E2),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF323840))
                    .clickable(onClick = onCopyMeetingNumber)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Copy meeting number",
                    color = Color(0xFF4A90E2),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            MeetingInfoField(label = "Meeting ID", value = state.meetingId)
            MeetingInfoField(label = "Passcode", value = state.passcode)
            MeetingInfoField(label = "Host", value = state.host)
            MeetingInfoField(label = "Participant ID", value = state.participantId)
            MeetingInfoField(label = "Encryption", value = state.encryptionLabel)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.connectionSummary,
                color = Color(0xFF9A9A9E),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = state.securityOverviewLabel,
                color = Color(0xFF4A90E2),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { }
            )
        }
    }
}

@Composable
private fun MeetingInfoField(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF9A9A9E),
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(38.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFD0D0D0))
        )
    }
}

private fun buildMeetingShareText(state: MeetingInfoDetailedUiState): String {
    return buildString {
        appendLine(state.topic)
        appendLine("Meeting ID: ${state.meetingId}")
        appendLine("Passcode: ${state.passcode}")
        appendLine("Host: ${state.host}")
        appendLine("Join: ${state.inviteLink}")
    }.trim()
}

private fun shareMeetingInfo(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(
        Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Zoom Meeting", text))
}
