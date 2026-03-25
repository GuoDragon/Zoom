package com.example.zoom.presentation.meetingchatdetailed

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MeetingChatDetailedScreen(
    onBackClick: () -> Unit
) {
    var uiState by remember { mutableStateOf<MeetingChatDetailedUiState?>(null) }
    var localMessages by remember { mutableStateOf<List<ChatMessageUi>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }

    val view = remember {
        object : MeetingChatDetailedContract.View {
            override fun showContent(content: MeetingChatDetailedUiState) {
                uiState = content
                localMessages = content.messages
            }
        }
    }

    LaunchedEffect(Unit) {
        MeetingChatDetailedPresenter(view).loadData()
    }

    Scaffold(
        containerColor = Color(0xFF161616),
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        topBar = {
            ChatTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            ChatInputBar(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSendClick = {
                    if (inputText.isNotBlank()) {
                        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
                        val newMsg = ChatMessageUi(
                            messageId = "local_${System.currentTimeMillis()}",
                            senderName = "James Wilson",
                            senderInitials = "JW",
                            content = inputText.trim(),
                            timestamp = timeFormat.format(Date()),
                            isSelf = true
                        )
                        localMessages = localMessages + newMsg
                        inputText = ""
                    }
                }
            )
        }
    ) { padding ->
        val listState = rememberLazyListState()

        LaunchedEffect(localMessages.size) {
            if (localMessages.isNotEmpty()) {
                listState.animateScrollToItem(localMessages.size - 1)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
        ) {
            item {
                ChatDateSeparator(label = "Today")
            }
            items(localMessages, key = { it.messageId }) { message ->
                ChatBubble(message = message)
            }
        }
    }
}

@Composable
private fun ChatTopBar(onBackClick: () -> Unit) {
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
            text = "Meeting Chat",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = "Participants",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessageUi) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isSelf) Alignment.End else Alignment.Start
    ) {
        if (!message.isSelf) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF78A93A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.senderInitials,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message.senderName,
                    color = Color(0xFFAAAAAA),
                    fontSize = 13.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (message.isSelf) ZoomBlue else Color(0xFF2A2A2D)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }

        Text(
            text = message.timestamp,
            color = Color(0xFF888888),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.Default.SentimentSatisfied,
                contentDescription = "Emoji",
                tint = Color(0xFF888888),
                modifier = Modifier.size(24.dp)
            )
        }

        TextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            placeholder = {
                Text(
                    text = "Type message here...",
                    color = Color(0xFF666666),
                    fontSize = 15.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A2A2D),
                unfocusedContainerColor = Color(0xFF2A2A2D),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = ZoomBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(22.dp),
            singleLine = true
        )

        IconButton(
            onClick = onSendClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (inputText.isNotBlank()) ZoomBlue else Color(0xFF555555),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ChatDateSeparator(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color(0xFF3A3A3D)
        )
        Text(
            text = label,
            color = Color(0xFF888888),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color(0xFF3A3A3D)
        )
    }
}
