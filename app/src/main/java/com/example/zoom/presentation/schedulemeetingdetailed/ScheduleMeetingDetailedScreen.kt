package com.example.zoom.presentation.schedulemeetingdetailed

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.presentation.schedulemeeting.ScheduleMeetingInviteeOption
import com.example.zoom.ui.components.ZoomInsetDivider
import com.example.zoom.ui.components.ZoomPageSurface
import com.example.zoom.ui.components.ZoomSettingValueRow
import com.example.zoom.ui.components.ZoomTopBarInsets
import com.example.zoom.ui.theme.ZoomBlue

private enum class ScheduleInvitePopupPage {
    MENU,
    SEND_MESSAGE,
    INVITE_CONTACTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleMeetingDetailedScreen(
    meetingId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onStartClick: (String) -> Unit,
    onChatClick: (String) -> Unit
) {
    var uiState by remember { mutableStateOf<ScheduleMeetingDetailedUiState?>(null) }
    var showInviteesPopup by remember { mutableStateOf(false) }
    var inviteePopupPage by remember { mutableStateOf(ScheduleInvitePopupPage.MENU) }
    var inviteeSearchQuery by remember { mutableStateOf("") }
    var inviteeWorkingSet by remember { mutableStateOf<Set<String>>(emptySet()) }

    val view = remember {
        object : ScheduleMeetingDetailedContract.View {
            override fun showContent(content: ScheduleMeetingDetailedUiState) {
                uiState = content
            }
        }
    }
    val presenter = remember(view) { ScheduleMeetingDetailedPresenter(view) }

    LaunchedEffect(presenter, meetingId) {
        presenter.loadData(meetingId)
    }

    val state = uiState ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Schedule meeting", fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable(onClick = onBackClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ZoomBlue
                        )
                        Text(
                            text = "Back",
                            color = ZoomBlue,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                },
                actions = {
                    if (state.canEdit) {
                        Text(
                            text = "Edit",
                            color = ZoomBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(end = 14.dp)
                                .clickable { onEditClick(state.meetingId) }
                        )
                    }
                },
                windowInsets = ZoomTopBarInsets,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            item {
                ZoomPageSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Text(
                        text = state.meetingTitle,
                        fontSize = 24.sp,
                        color = Color(0xFF313943),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Starts",
                        value = state.startsLabel,
                        showChevron = false
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Duration",
                        value = state.durationLabel,
                        showChevron = false
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Invitees",
                        value = state.inviteeSummary,
                        showChevron = false
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScheduleMeetingActionButton(
                        icon = Icons.Default.PlayArrow,
                        label = "Start",
                        modifier = Modifier.weight(1f),
                        onClick = { onStartClick(state.meetingId) }
                    )
                    ScheduleMeetingActionButton(
                        icon = Icons.Default.ChatBubbleOutline,
                        label = "Chat",
                        modifier = Modifier.weight(1f),
                        onClick = { onChatClick(state.meetingId) }
                    )
                    ScheduleMeetingActionButton(
                        icon = Icons.Default.Groups,
                        label = "Add invitees",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            inviteeWorkingSet = state.selectedInviteeUserIds
                            inviteeSearchQuery = ""
                            inviteePopupPage = ScheduleInvitePopupPage.MENU
                            showInviteesPopup = true
                        }
                    )
                }
            }

            item {
                Text(
                    text = "Recent chat",
                    color = Color(0xFF6D7785),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (state.recentMessages.isEmpty()) {
                item {
                    Text(
                        text = "No chat messages yet",
                        color = Color(0xFFA2ABB8),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(state.recentMessages, key = { it.messageId }) { message ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = message.senderName,
                                color = Color(0xFF313943),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = message.timestampLabel,
                                color = Color(0xFF98A1AE),
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = message.content,
                            color = Color(0xFF556274),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
                }
            }
        }
    }

    if (showInviteesPopup) {
        ScheduleMeetingInviteesPopup(
            inviteMessageText = state.inviteMessageText,
            invitees = state.inviteeOptions,
            selectedUserIds = inviteeWorkingSet,
            query = inviteeSearchQuery,
            popupPage = inviteePopupPage,
            onQueryChange = { inviteeSearchQuery = it },
            onToggleUser = { userId ->
                inviteeWorkingSet = if (inviteeWorkingSet.contains(userId)) {
                    inviteeWorkingSet - userId
                } else {
                    inviteeWorkingSet + userId
                }
            },
            onPageChange = { inviteePopupPage = it },
            onDismiss = { showInviteesPopup = false },
            onInviteContactsConfirm = {
                presenter.updateInvitees(meetingId = state.meetingId, inviteeUserIds = inviteeWorkingSet)
                showInviteesPopup = false
            }
        )
    }
}

@Composable
private fun ScheduleMeetingActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(Color(0xFFF5F7FA), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ZoomBlue,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = Color(0xFF4A5768),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun ScheduleMeetingInviteesPopup(
    inviteMessageText: String,
    invitees: List<ScheduleMeetingInviteeOption>,
    selectedUserIds: Set<String>,
    query: String,
    popupPage: ScheduleInvitePopupPage,
    onQueryChange: (String) -> Unit,
    onToggleUser: (String) -> Unit,
    onPageChange: (ScheduleInvitePopupPage) -> Unit,
    onDismiss: () -> Unit,
    onInviteContactsConfirm: () -> Unit
) {
    val filteredInvitees = invitees.filter {
        query.isBlank() ||
            it.name.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(if (popupPage == ScheduleInvitePopupPage.INVITE_CONTACTS) 620.dp else 430.dp)
                .background(Color(0xFF2A2A2D), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
        ) {
            when (popupPage) {
                ScheduleInvitePopupPage.MENU -> {
                    ScheduleInviteDragHandle()
                    Text(
                        text = "Invite",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ScheduleInviteMenuItem(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        label = "Send a message",
                        onClick = { onPageChange(ScheduleInvitePopupPage.SEND_MESSAGE) }
                    )
                    ScheduleInviteMenuItem(
                        icon = Icons.Default.PersonAdd,
                        label = "Invite contacts",
                        onClick = { onPageChange(ScheduleInvitePopupPage.INVITE_CONTACTS) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color(0xFF3A3A3D), RoundedCornerShape(12.dp))
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFFE53935),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                ScheduleInvitePopupPage.SEND_MESSAGE -> {
                    ScheduleInviteDragHandle()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onPageChange(ScheduleInvitePopupPage.MENU) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Send message",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .background(Color(0xFF3A3A3D), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = inviteMessageText,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color(0xFF3A3A3D), RoundedCornerShape(12.dp))
                            .clickable { onPageChange(ScheduleInvitePopupPage.MENU) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Done",
                            color = ZoomBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                ScheduleInvitePopupPage.INVITE_CONTACTS -> {
                    ScheduleInviteDragHandle()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onPageChange(ScheduleInvitePopupPage.MENU) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Invite Contacts",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (selectedUserIds.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(ZoomBlue, RoundedCornerShape(8.dp))
                                    .clickable(onClick = onInviteContactsConfirm)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Invite(${selectedUserIds.size})",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(56.dp))
                        }
                    }

                    TextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search contacts", color = Color(0xFF8E8E93)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF3A3A3D),
                            unfocusedContainerColor = Color(0xFF3A3A3D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = ZoomBlue,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredInvitees, key = { it.userId }) { invitee ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleUser(invitee.userId) }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFF4D76D0), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = invitee.name.split(" ")
                                            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                            .take(2)
                                            .joinToString("")
                                            .ifBlank { "?" },
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = invitee.name,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = invitee.email,
                                        color = Color(0xFF8E8E93),
                                        fontSize = 13.sp
                                    )
                                }
                                if (selectedUserIds.contains(invitee.userId)) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = ZoomBlue
                                    )
                                }
                            }
                            HorizontalDivider(thickness = 0.6.dp, color = Color(0xFF3D3D42))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleInviteDragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(4.dp)
                .background(Color(0xFFD0D0D0), RoundedCornerShape(2.dp))
        )
    }
}

@Composable
private fun ScheduleInviteMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}
