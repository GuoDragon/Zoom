package com.example.zoom.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue

@Composable
fun SearchTopResultsContent(
    state: SearchUiState,
    onMessageClick: (String) -> Unit,
    onChatClick: (String) -> Unit,
    onMeetingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasAnyResults = state.messageResults.isNotEmpty() ||
            state.meetingResults.isNotEmpty() ||
            state.contactResults.isNotEmpty() ||
            state.chatResults.isNotEmpty()

    if (!hasAnyResults) {
        SearchNoResultContent(modifier)
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        if (state.messageResults.isNotEmpty()) {
            item { SectionHeader("Messages") }
            items(state.messageResults.take(3)) { msg ->
                MessageResultRow(msg, onClick = { onMessageClick(msg.meetingId) })
            }
        }
        if (state.chatResults.isNotEmpty()) {
            item { SectionHeader("Chats & Channels") }
            items(state.chatResults.take(3)) { chat ->
                ChatResultRow(chat, onClick = { onChatClick(chat.meetingId) })
            }
        }
        if (state.meetingResults.isNotEmpty()) {
            item { SectionHeader("Meetings") }
            items(state.meetingResults.take(3)) { meeting ->
                MeetingResultCard(meeting, onClick = { onMeetingClick(meeting.meetingId) })
            }
        }
        if (state.contactResults.isNotEmpty()) {
            item { SectionHeader("Contacts") }
            items(state.contactResults.take(3)) { contact ->
                ContactResultRow(contact)
            }
        }
    }
}

@Composable
fun SearchMessagesContent(
    results: List<MessageResult>,
    onMessageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        SearchNoResultContent(modifier)
        return
    }
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(results) { msg ->
            MessageResultRow(msg, onClick = { onMessageClick(msg.meetingId) })
        }
    }
}

@Composable
fun SearchChatsContent(
    results: List<ChatResult>,
    onChatClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        SearchNoResultContent(modifier)
        return
    }
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(results) { chat ->
            ChatResultRow(chat, onClick = { onChatClick(chat.meetingId) })
        }
    }
}

@Composable
fun SearchMeetingsContent(
    results: List<MeetingResult>,
    onMeetingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        SearchNoResultContent(modifier)
        return
    }
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(results) { meeting ->
            MeetingResultCard(meeting, onClick = { onMeetingClick(meeting.meetingId) })
        }
    }
}

@Composable
fun SearchContactsContent(
    results: List<ContactResult>,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        SearchNoResultContent(modifier)
        return
    }
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(results) { contact ->
            ContactResultRow(contact)
        }
    }
}

@Composable
fun SearchEmptyResultContent(modifier: Modifier = Modifier) {
    SearchNoResultContent(modifier)
}

@Composable
private fun SearchNoResultContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F5F8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF8F9BAA),
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No results found",
            color = Color(0xFF5F6E80),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFF3B495A),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun MessageResultRow(msg: MessageResult, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ZoomBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = msg.senderInitial,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = msg.senderName,
                    color = Color(0xFF243447),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = msg.timeLabel,
                    color = Color(0xFF95A0AE),
                    fontSize = 12.sp
                )
            }
            Text(
                text = msg.meetingTopic,
                color = Color(0xFF6E7A89),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = msg.contentPreview,
                color = Color(0xFF95A0AE),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    HorizontalDivider(color = Color(0xFFF1F3F6))
}

@Composable
private fun MeetingResultCard(meeting: MeetingResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = meeting.topic,
                color = Color(0xFF243447),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color(0xFF6E7A89),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = meeting.dateTimeLabel,
                    color = Color(0xFF6E7A89),
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF6E7A89),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${meeting.participantCount} participants",
                    color = Color(0xFF6E7A89),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ContactResultRow(contact: ContactResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF5CB85C)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.initial,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = contact.username,
                color = Color(0xFF243447),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            if (contact.email.isNotEmpty()) {
                Text(
                    text = contact.email,
                    color = Color(0xFF95A0AE),
                    fontSize = 13.sp
                )
            }
        }
    }
    HorizontalDivider(color = Color(0xFFF1F3F6))
}

@Composable
private fun ChatResultRow(chat: ChatResult, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF2D8CFF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chat.chatName.firstOrNull()?.uppercase() ?: "#",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = chat.chatName,
                    color = Color(0xFF243447),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = chat.timeLabel,
                    color = Color(0xFF95A0AE),
                    fontSize = 12.sp
                )
            }
            Text(
                text = chat.lastMessage,
                color = Color(0xFF95A0AE),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    HorizontalDivider(color = Color(0xFFF1F3F6))
}
