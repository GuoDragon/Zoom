package com.example.zoom.presentation.search

import androidx.compose.ui.graphics.Color

enum class SearchCategory(val title: String) {
    TopResults("Top results"),
    Messages("Messages"),
    Chats("Chats and channels"),
    Meetings("Meetings"),
    Contacts("Contacts"),
    Files("Files"),
    Docs("Docs"),
    Whiteboards("Whiteboards"),
    Mail("Mail")
}

enum class SearchFilterChipStyle {
    Sheet,
    Toggle
}

enum class SearchSheetGroupStyle {
    List,
    Radio
}

data class SearchUiState(
    val query: String,
    val tabs: List<SearchCategory>,
    val selectedCategory: SearchCategory,
    val filters: List<SearchFilterChipUiState>,
    val activeSheet: SearchFilterSheetUiState?,
    val topResults: SearchTopResultsUiState,
    val messageResults: List<MessageResult>,
    val chatResults: List<ChatResult>,
    val meetingResults: List<MeetingResult>,
    val contactResults: List<ContactResult>
)

data class SearchFilterChipUiState(
    val id: String,
    val label: String,
    val selected: Boolean,
    val style: SearchFilterChipStyle
)

data class SearchFilterSheetUiState(
    val title: String,
    val showDone: Boolean,
    val showSearchField: Boolean,
    val searchQuery: String,
    val groups: List<SearchSheetGroupUiState>
)

data class SearchSheetGroupUiState(
    val style: SearchSheetGroupStyle,
    val options: List<SearchSheetOptionUiState>
)

data class SearchSheetOptionUiState(
    val id: String,
    val label: String,
    val selected: Boolean,
    val leading: SearchSheetOptionLeading? = null
)

sealed interface SearchSheetOptionLeading {
    data object Conversation : SearchSheetOptionLeading
    data object Bookmark : SearchSheetOptionLeading
    data object Person : SearchSheetOptionLeading
    data class Initials(
        val text: String,
        val backgroundColor: Color = Color(0xFF72B646)
    ) : SearchSheetOptionLeading
}

data class SearchTopResultsUiState(
    val messages: List<MessageResult> = emptyList(),
    val chats: List<ChatResult> = emptyList(),
    val meetings: List<MeetingResult> = emptyList(),
    val contacts: List<ContactResult> = emptyList()
)

data class MessageResult(
    val messageId: String,
    val meetingId: String,
    val senderId: String,
    val senderName: String,
    val senderInitial: String,
    val meetingTopic: String,
    val contentPreview: String,
    val timeLabel: String
)

data class MeetingResult(
    val meetingId: String,
    val topic: String,
    val dateTimeLabel: String,
    val participantCount: Int
)

data class ContactResult(
    val userId: String,
    val username: String,
    val email: String,
    val initial: String
)

data class ChatResult(
    val meetingId: String,
    val chatName: String,
    val lastMessage: String,
    val timeLabel: String
)
