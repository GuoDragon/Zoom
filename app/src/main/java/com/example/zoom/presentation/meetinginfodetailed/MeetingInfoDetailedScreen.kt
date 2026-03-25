package com.example.zoom.presentation.meetinginfodetailed

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MeetingInfoDetailedScreen(
    onBackClick: () -> Unit
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

    uiState?.let { state ->
        Scaffold(
            containerColor = Color(0xFF161616),
            contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
            topBar = {
                InfoTopBar(onBackClick = onBackClick)
            },
            bottomBar = {
                CopyInvitationButton(
                    onClick = {
                        val text = buildString {
                            appendLine("${state.topic}")
                            appendLine("Meeting ID: ${state.meetingId}")
                            appendLine("Passcode: ${state.passcode}")
                            appendLine("Join: ${state.inviteLink}")
                        }
                        copyToClipboard(context, text)
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                InfoRow(label = "Meeting Topic", value = state.topic)
                InfoDivider()
                InfoRow(
                    label = "Meeting ID",
                    value = state.meetingId,
                    copyable = true,
                    onCopy = { copyToClipboard(context, state.meetingId) }
                )
                InfoDivider()
                InfoRow(
                    label = "Passcode",
                    value = state.passcode,
                    copyable = true,
                    onCopy = { copyToClipboard(context, state.passcode) }
                )
                InfoDivider()
                InfoRow(label = "Host", value = state.host)
                InfoDivider()
                InfoRow(
                    label = "Invite Link",
                    value = state.inviteLink,
                    copyable = true,
                    onCopy = { copyToClipboard(context, state.inviteLink) }
                )
                InfoDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Encryption row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = Color(0xFF5CB85C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "End-to-end Encrypted",
                        color = Color(0xFF5CB85C),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161616))
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = "Meeting Information",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    copyable: Boolean = false,
    onCopy: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF888888),
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            if (copyable && onCopy != null) {
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color(0xFF888888),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoDivider() {
    HorizontalDivider(color = Color(0xFF2A2A2D), thickness = 1.dp)
}

@Composable
private fun CopyInvitationButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161616))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent)
                .clickable(onClick = onClick)
                .then(
                    Modifier
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(12.dp))
                )
                .padding(vertical = 14.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
                tint = Color(0xFF2D8CFF),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Copy Invitation",
                color = Color(0xFF2D8CFF),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Zoom Meeting", text))
}
