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
import androidx.compose.material.icons.filled.Description
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue

@Composable
fun SearchPageContent(
    state: SearchUiState,
    modifier: Modifier = Modifier
) {
    when (state.selectedCategory) {
        SearchCategory.TopResults -> if (state.query.isBlank()) {
            SearchPromptContent("Type keywords to start your search", modifier)
        } else {
            SearchTopResultsContent(state.topResults, modifier)
        }

        SearchCategory.Messages -> if (state.query.isBlank()) {
            SearchPromptContent("Use keywords or filters to start your search", modifier)
        } else {
            SearchMessagesContent(state.messageResults, modifier)
        }

        SearchCategory.Chats -> if (state.query.isBlank()) {
            SearchPromptContent("Use keywords or filters to start your search", modifier)
        } else {
            SearchChatsContent(state.chatResults, modifier)
        }

        SearchCategory.Meetings -> if (state.query.isBlank()) {
            SearchPromptContent("Use keywords or filters to start your search", modifier)
        } else {
            SearchMeetingsContent(state.meetingResults, modifier)
        }

        SearchCategory.Contacts -> if (state.query.isBlank()) {
            SearchPromptContent("Type keywords to start your search", modifier)
        } else {
            SearchContactsContent(state.contactResults, modifier)
        }

        SearchCategory.Files,
        SearchCategory.Docs,
        SearchCategory.Whiteboards -> SearchStaticCategoryContent(
            category = state.selectedCategory,
            query = state.query,
            modifier = modifier
        )

        SearchCategory.Mail -> SearchMailConnectContent(modifier)
    }
}

@Composable
fun SearchTopResultsContent(
    state: SearchTopResultsUiState,
    modifier: Modifier = Modifier
) {
    val hasAnyResults = state.messages.isNotEmpty() ||
        state.chats.isNotEmpty() ||
        state.meetings.isNotEmpty() ||
        state.contacts.isNotEmpty()

    if (!hasAnyResults) {
        SearchNoResultContent("No results found", modifier)
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (state.messages.isNotEmpty()) {
            item { SectionHeader("Messages") }
            items(state.messages) { MessageResultRow(it) }
        }
        if (state.chats.isNotEmpty()) {
            item { SectionHeader("Chats and channels") }
            items(state.chats) { ChatResultRow(it) }
        }
        if (state.meetings.isNotEmpty()) {
            item { SectionHeader("Meetings") }
            items(state.meetings) { MeetingResultCard(it) }
        }
        if (state.contacts.isNotEmpty()) {
            item { SectionHeader("Contacts") }
            items(state.contacts) { ContactResultRow(it) }
        }
    }
}

@Composable
fun SearchMessagesContent(
    results: List<MessageResult>,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        SearchNoResultContent("No messages found", modifier)
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(results) { MessageResultRow(it) }
    }
}

@Composable
fun SearchChatsContent(
    results: List<ChatResult>,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        SearchNoResultContent("No chats found", modifier)
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(results) { ChatResultRow(it) }
    }
}

@Composable
fun SearchMeetingsContent(
    results: List<MeetingResult>,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        SearchNoResultContent("No meetings found", modifier)
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(results) { MeetingResultCard(it) }
    }
}

@Composable
fun SearchContactsContent(
    results: List<ContactResult>,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        SearchNoResultContent("No contacts found", modifier)
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(results) { ContactResultRow(it) }
    }
}

@Composable
private fun SearchStaticCategoryContent(
    category: SearchCategory,
    query: String,
    modifier: Modifier = Modifier
) {
    if (query.isBlank()) {
        SearchPromptContent("Use keywords or filters to start your search", modifier)
        return
    }

    val label = when (category) {
        SearchCategory.Files -> "No files found"
        SearchCategory.Docs -> "No docs found"
        SearchCategory.Whiteboards -> "No whiteboards found"
        else -> "No results found"
    }
    SearchNoResultContent(label, modifier)
}

@Composable
private fun SearchPromptContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SearchIllustration()
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = message,
            color = Color(0xFF5F6E80),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchNoResultContent(
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SearchIllustration()
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = label,
            color = Color(0xFF5F6E80),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchMailConnectContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Connect your Mail",
            color = Color(0xFF1F2C3A),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Set up your mail account to see search result from Mail",
            color = Color(0xFF5F6E80),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        SearchMailIllustration()
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "Connect",
            color = ZoomBlue,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(enabled = false) {}
        )
    }
}

@Composable
private fun SearchIllustration() {
    Box(
        modifier = Modifier.size(width = 182.dp, height = 148.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 92.dp, height = 112.dp)
                .rotate(-8f)
                .background(Color(0xFF4E7BFF), RoundedCornerShape(18.dp))
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier
                .size(width = 82.dp, height = 96.dp)
                .rotate(8f)
                .background(Color(0xFFF8FBFF), RoundedCornerShape(16.dp))
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .padding(top = (22 + index * 18).dp, start = 16.dp)
                        .size(width = 42.dp, height = 6.dp)
                        .background(Color(0xFFD8E4FF), RoundedCornerShape(4.dp))
                )
            }
        }
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = ZoomBlue,
            modifier = Modifier
                .size(74.dp)
                .align(Alignment.BottomEnd)
        )
    }
}

@Composable
private fun SearchMailIllustration() {
    Box(
        modifier = Modifier.size(width = 164.dp, height = 138.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 112.dp, height = 92.dp)
                .background(Color(0xFFDCE7FF), RoundedCornerShape(28.dp))
        )
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = ZoomBlue,
            modifier = Modifier.size(74.dp)
        )
        Box(
            modifier = Modifier
                .size(22.dp)
                .align(Alignment.BottomEnd)
                .background(Color(0xFFF3C751), CircleShape)
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
private fun MessageResultRow(msg: MessageResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(ZoomBlue, CircleShape),
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
private fun ChatResultRow(chat: ChatResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF2D8CFF), CircleShape),
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

@Composable
private fun MeetingResultCard(meeting: MeetingResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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
            Text(
                text = meeting.dateTimeLabel,
                color = Color(0xFF6E7A89),
                fontSize = 13.sp
            )
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
                .background(Color(0xFF5CB85C), CircleShape),
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
