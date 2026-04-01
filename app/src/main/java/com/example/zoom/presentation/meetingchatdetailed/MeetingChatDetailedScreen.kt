package com.example.zoom.presentation.meetingchatdetailed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ForwardToInbox
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.R
import com.example.zoom.ui.theme.ZoomBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MeetingChatDetailedOverlay(
    meetingTitle: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var localMessages by remember { mutableStateOf<List<ChatMessageUi>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val dismissInteraction = remember { MutableInteractionSource() }
    val panelInteraction = remember { MutableInteractionSource() }
    val view = remember {
        object : MeetingChatDetailedContract.View {
            override fun showContent(content: MeetingChatDetailedUiState) {
                localMessages = content.messages
            }
        }
    }
    val presenter = remember(view) { MeetingChatDetailedPresenter(view) }

    LaunchedEffect(Unit) {
        presenter.loadData()
    }

    LaunchedEffect(localMessages.size) {
        if (localMessages.isNotEmpty()) {
            listState.animateScrollToItem(localMessages.lastIndex)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = dismissInteraction,
                    onClick = onDismiss
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 70.dp)
                .height(440.dp)
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .background(Color(0xFF2B2B2E))
                .clickable(
                    indication = null,
                    interactionSource = panelInteraction
                ) {}
        ) {
            MeetingChatDragHandle()
            MeetingChatHeader(
                meetingTitle = meetingTitle,
                onDismiss = onDismiss
            )
            MeetingChatAudienceRow()
            MeetingChatTimeMarker()

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(localMessages, key = { it.messageId }) { message ->
                    MeetingChatMessageRow(message = message)
                }
            }

            MeetingChatComposer(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSendClick = {
                    if (inputText.isBlank()) return@MeetingChatComposer

                    presenter.sendMessage(inputText)?.let { newMessage ->
                        localMessages = localMessages + newMessage
                    }

                    inputText = ""
                }
            )
        }
    }
}

@Composable
private fun MeetingChatDragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF86868B))
        )
    }
}

@Composable
private fun MeetingChatHeader(
    meetingTitle: String,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.common_close),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = meetingTitle,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.meeting_chat_collapse),
                tint = Color.White
            )
        }
        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = stringResource(R.string.common_more),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun MeetingChatAudienceRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        MeetingAudienceChip(
            selected = true,
            icon = Icons.Default.Groups,
            label = stringResource(R.string.meeting_chat_everyone)
        )
        MeetingAudienceChip(
            selected = false,
            icon = Icons.Default.Add,
            label = stringResource(R.string.meeting_chat_new_chat)
        )
    }
}

@Composable
private fun MeetingAudienceChip(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (selected) Color(0xFF33363D) else Color(0xFF3A3A3E))
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) ZoomBlue else Color(0xFF4A4A4F),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) ZoomBlue else Color(0xFFC2C2C7),
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = label,
            color = if (selected) ZoomBlue else Color(0xFFD0D0D4),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun MeetingChatTimeMarker() {
    val timeLabel = remember {
        SimpleDateFormat("HH:mm", Locale.US).format(Date())
    }

    Text(
        text = timeLabel,
        color = Color(0xFF8E8E93),
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 10.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun MeetingChatMessageRow(message: ChatMessageUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (message.isSelf) Color(0xFF74A733) else Color(0xFF4D76D0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message.senderInitials,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.widthIn(max = 220.dp)
        ) {
            Text(
                text = if (message.isSelf) {
                    stringResource(R.string.meeting_chat_you)
                } else {
                    message.senderName
                },
                color = Color(0xFF8E8E93),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (message.isSelf) Color(0xFF34343A) else Color(0xFF303035))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                )
            }

            if (message.isSelf) {
                Row(
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MeetingChatActionIcon(
                        icon = Icons.AutoMirrored.Filled.Reply,
                        contentDescription = stringResource(R.string.meeting_chat_reply)
                    )
                    MeetingChatActionIcon(
                        icon = Icons.Default.AddReaction,
                        contentDescription = stringResource(R.string.meeting_chat_react)
                    )
                    MeetingChatActionIcon(
                        icon = Icons.Default.MoreHoriz,
                        contentDescription = stringResource(R.string.common_more)
                    )
                }
            }
        }
    }
}

@Composable
private fun MeetingChatActionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color(0xFF9A9AA0),
        modifier = Modifier.size(18.dp)
    )
}

@Composable
private fun MeetingChatComposer(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.common_add),
                    tint = Color(0xFFBDBDC2)
                )
            }

            TextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.meeting_chat_message_everyone),
                        color = Color(0xFF8E8E93),
                        fontSize = 15.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF3A3A40),
                    unfocusedContainerColor = Color(0xFF3A3A40),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = ZoomBlue,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            IconButton(onClick = {}, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = Icons.Default.SentimentSatisfied,
                    contentDescription = stringResource(R.string.common_emoji),
                    tint = Color(0xFFBDBDC2)
                )
            }
            IconButton(onClick = onSendClick, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.common_send),
                    tint = if (inputText.isNotBlank()) ZoomBlue else Color(0xFF5E5E63)
                )
            }
        }

        Row(
            modifier = Modifier.padding(start = 40.dp, top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ForwardToInbox,
                contentDescription = null,
                tint = ZoomBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.meeting_chat_visibility_tip),
                color = Color(0xFF9C9CA2),
                fontSize = 13.sp
            )
        }
    }
}
