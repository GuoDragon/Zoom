package com.example.zoom.presentation.meetingparticipantsdetailed

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.data.DataRepository

@Composable
fun MeetingParticipantsDetailedOverlay(onDismiss: () -> Unit) {
    var uiState by remember { mutableStateOf<MeetingParticipantsDetailedUiState?>(null) }
    var participants by remember { mutableStateOf<List<MeetingParticipantUi>>(emptyList()) }
    var currentPage by remember { mutableStateOf(ParticipantsSubPage.PARTICIPANTS) }
    var selectedParticipantId by remember { mutableStateOf<String?>(null) }
    var selectedContactIds by remember { mutableStateOf(setOf<String>()) }
    var contactSearchQuery by remember { mutableStateOf("") }
    var showParticipantsMoreMenu by remember { mutableStateOf(false) }

    val view = remember {
        object : MeetingParticipantsDetailedContract.View {
            override fun showContent(content: MeetingParticipantsDetailedUiState) {
                uiState = content
                participants = content.participants
            }
        }
    }

    LaunchedEffect(Unit) {
        MeetingParticipantsDetailedPresenter(view).loadData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        uiState?.let { state ->
            val displayState = state.copy(
                participantCount = participants.size,
                participants = participants
            )
            when (currentPage) {
                ParticipantsSubPage.PARTICIPANTS -> ParticipantsListPage(
                    state = displayState,
                    onDismiss = onDismiss,
                    showMoreMenu = showParticipantsMoreMenu,
                    onDismissMoreMenu = { showParticipantsMoreMenu = false },
                    onInviteClick = {
                        showParticipantsMoreMenu = false
                        currentPage = ParticipantsSubPage.ADD_INVITE
                    },
                    onParticipantClick = { id ->
                        showParticipantsMoreMenu = false
                        selectedParticipantId = id
                        currentPage = ParticipantsSubPage.PARTICIPANT_MORE
                    },
                    onMoreClick = { showParticipantsMoreMenu = !showParticipantsMoreMenu },
                    onMuteAllClick = {
                        val targetIds = participants.filterNot { it.isSelf }.map { it.userId }
                        participants = participants.map { participant ->
                            if (participant.isSelf) participant else participant.copy(isMuted = true)
                        }
                        DataRepository.recordMeetingAction(
                            actionType = "MUTE_ALL",
                            meetingId = displayState.meetingId,
                            targetUserIds = targetIds
                        )
                        showParticipantsMoreMenu = false
                    },
                    onAskAllToUnmuteClick = {
                        val targetIds = participants.filterNot { it.isSelf }.map { it.userId }
                        participants = participants.map { participant ->
                            if (participant.isSelf) participant else participant.copy(isMuted = false)
                        }
                        DataRepository.recordMeetingAction(
                            actionType = "ASK_ALL_TO_UNMUTE",
                            meetingId = displayState.meetingId,
                            targetUserIds = targetIds
                        )
                        showParticipantsMoreMenu = false
                    }
                )
                ParticipantsSubPage.ADD_INVITE -> AddInvitePage(
                    options = displayState.inviteOptions,
                    onOptionClick = { label ->
                        when (label) {
                            "Send a message" -> currentPage = ParticipantsSubPage.SEND_MESSAGE
                            "Invite contacts" -> {
                                selectedContactIds = emptySet()
                                contactSearchQuery = ""
                                currentPage = ParticipantsSubPage.INVITE_CONTACTS
                            }
                        }
                    },
                    onCancel = { currentPage = ParticipantsSubPage.PARTICIPANTS }
                )
                ParticipantsSubPage.SEND_MESSAGE -> SendMessagePage(
                    messageText = displayState.inviteMessageText,
                    onBack = { currentPage = ParticipantsSubPage.ADD_INVITE }
                )
                ParticipantsSubPage.INVITE_CONTACTS -> InviteContactsPage(
                    contacts = displayState.allContacts,
                    selectedIds = selectedContactIds,
                    searchQuery = contactSearchQuery,
                    onSearchQueryChange = { contactSearchQuery = it },
                    onToggleContact = { id ->
                        selectedContactIds = if (selectedContactIds.contains(id)) {
                            selectedContactIds - id
                        } else {
                            selectedContactIds + id
                        }
                    },
                    onBack = { currentPage = ParticipantsSubPage.ADD_INVITE },
                    onInvite = {
                        DataRepository.recordMeetingAction(
                            actionType = "INVITE_CONTACTS",
                            meetingId = displayState.meetingId,
                            targetUserIds = selectedContactIds.toList(),
                            note = if (selectedContactIds.isEmpty()) {
                                "No contacts selected"
                            } else {
                                "Selected contacts: ${selectedContactIds.joinToString(",")}"
                            }
                        )
                        currentPage = ParticipantsSubPage.ADD_INVITE
                    }
                )
                ParticipantsSubPage.PARTICIPANT_MORE -> {
                    val participant = displayState.participants.find { it.userId == selectedParticipantId }
                    if (participant != null) {
                        ParticipantMorePage(
                            participant = participant,
                            onCancel = { currentPage = ParticipantsSubPage.PARTICIPANTS }
                        )
                    }
                }
            }
        }
    }
}

// ── Page 1: Participants List ────────────────────────────────────────────

@Composable
private fun ParticipantsListPage(
    state: MeetingParticipantsDetailedUiState,
    onDismiss: () -> Unit,
    showMoreMenu: Boolean,
    onDismissMoreMenu: () -> Unit,
    onInviteClick: () -> Unit,
    onParticipantClick: (String) -> Unit,
    onMoreClick: () -> Unit,
    onMuteAllClick: () -> Unit,
    onAskAllToUnmuteClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismissMoreMenu() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFF2A2A2D))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {}
                    .padding(top = 8.dp, bottom = 24.dp)
            ) {
                DragHandle()

                Spacer(modifier = Modifier.height(8.dp))

                // Header: X left + title center + PersonAdd + MoreHoriz right
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
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Participants (${state.participantCount})",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                        IconButton(onClick = onInviteClick) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Invite",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = onMoreClick) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "More",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Participant list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(state.participants, key = { it.userId }) { participant ->
                        ParticipantRow(
                            participant = participant,
                            onClick = { onParticipantClick(participant.userId) }
                        )
                    }
                }
            }

            if (showMoreMenu) {
                ParticipantsMoreMenu(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 56.dp, end = 14.dp),
                    onMuteAllClick = onMuteAllClick,
                    onAskAllToUnmuteClick = onAskAllToUnmuteClick
                )
            }
        }
    }
}

@Composable
private fun ParticipantsMoreMenu(
    modifier: Modifier = Modifier,
    onMuteAllClick: () -> Unit,
    onAskAllToUnmuteClick: () -> Unit
) {
    Column(
        modifier = modifier
            .width(188.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A3A3D))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {}
    ) {
        ParticipantsMoreMenuItem(
            label = "Mute all",
            onClick = onMuteAllClick
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF4A4A4D))
        )
        ParticipantsMoreMenuItem(
            label = "Ask all to unmute",
            onClick = onAskAllToUnmuteClick
        )
    }
}

@Composable
private fun ParticipantsMoreMenuItem(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun ParticipantRow(
    participant: MeetingParticipantUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(participant.avatarColor)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = participant.initials,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name + role tag
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = participant.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (participant.roleTag.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = participant.roleTag,
                        color = Color(0xFF8E8E93),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Mic icon
        Icon(
            imageVector = if (participant.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = if (participant.isMuted) "Muted" else "Unmuted",
            tint = if (participant.isMuted) Color(0xFFE57373) else Color.White,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Camera icon
        Icon(
            imageVector = if (participant.isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
            contentDescription = if (participant.isVideoOff) "Video off" else "Video on",
            tint = if (participant.isVideoOff) Color(0xFFE57373) else Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Page 2: Add / Invite ─────────────────────────────────────────────────

@Composable
private fun AddInvitePage(
    options: List<InviteOptionUi>,
    onOptionClick: (String) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFF2A2A2D))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            DragHandle()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Invite",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            options.forEach { option ->
                InviteOptionRow(
                    option = option,
                    onClick = { onOptionClick(option.label) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3A3A3D))
                    .clickable(onClick = onCancel)
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
    }
}

@Composable
private fun InviteOptionRow(
    option: InviteOptionUi,
    onClick: () -> Unit
) {
    val icon = when (option.iconName) {
        "Link" -> Icons.Default.Link
        "Chat" -> Icons.AutoMirrored.Filled.Chat
        "Email" -> Icons.Default.Email
        "Contacts" -> Icons.Default.PersonAdd
        "Room" -> Icons.Default.MeetingRoom
        else -> Icons.Default.Link
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = option.label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = option.label,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

// ── Page 3: Send Message ─────────────────────────────────────────────────

@Composable
private fun SendMessagePage(
    messageText: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFF2A2A2D))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            DragHandle()

            Spacer(modifier = Modifier.height(8.dp))

            // Header: back arrow + title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Send message",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                // Spacer to balance the back button
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Message card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3A3A3D))
                    .padding(16.dp)
            ) {
                Text(
                    text = messageText,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Share icon row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShareIconItem(icon = Icons.AutoMirrored.Filled.Send, label = "Messages")
                ShareIconItem(icon = Icons.Default.Email, label = "Gmail")
                ShareIconItem(icon = Icons.Default.ContentCopy, label = "Copy")
            }
        }
    }
}

@Composable
private fun ShareIconItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A3A3D)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color(0xFF8E8E93),
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ── Page 4: Invite Contacts ──────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InviteContactsPage(
    contacts: List<ContactUi>,
    selectedIds: Set<String>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleContact: (String) -> Unit,
    onBack: () -> Unit,
    onInvite: () -> Unit
) {
    val filteredContacts = if (searchQuery.isBlank()) {
        contacts
    } else {
        val q = searchQuery.lowercase()
        contacts.filter {
            it.name.lowercase().contains(q) || it.phone.lowercase().contains(q)
        }
    }

    val grouped = filteredContacts.groupBy {
        it.name.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
    }.toSortedMap()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFF2A2A2D))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            DragHandle()

            Spacer(modifier = Modifier.height(8.dp))

            // Header: back + title + Invite(N) button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Invite Contacts",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                if (selectedIds.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2D8CFF))
                            .clickable(onClick = onInvite)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Invite(${selectedIds.size})",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search bar
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text("Search contacts", color = Color(0xFF8E8E93))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF8E8E93)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF3A3A3D),
                    unfocusedContainerColor = Color(0xFF3A3A3D),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF2D8CFF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contact list with sticky headers
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                grouped.forEach { (letter, contactsInGroup) ->
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2A2A2D))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = letter,
                                color = Color(0xFF8E8E93),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    items(contactsInGroup, key = { it.userId }) { contact ->
                        ContactRow(
                            contact = contact,
                            isSelected = selectedIds.contains(contact.userId),
                            onToggle = { onToggleContact(contact.userId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    contact: ContactUi,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(contact.avatarColor)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.initials,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name + phone
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                color = Color.White,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (contact.phone.isNotEmpty()) {
                Text(
                    text = contact.phone,
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
            }
        }

        // Checkbox circle
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(0xFF2D8CFF) else Color.Transparent
                )
                .then(
                    if (!isSelected) Modifier
                        .background(Color.Transparent)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3A3D))
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ── Page 5: Participant More ─────────────────────────────────────────────

@Composable
private fun ParticipantMorePage(
    participant: MeetingParticipantUi,
    onCancel: () -> Unit
) {
    val actions = listOf(
        MoreAction("Chat", Icons.AutoMirrored.Filled.Chat, false),
        MoreAction("Mute", Icons.Default.MicOff, false),
        MoreAction("Stop Video", Icons.Default.VideocamOff, false),
        MoreAction("Make Host", Icons.Default.PersonAdd, false),
        MoreAction("Make Co-Host", Icons.Default.PersonAdd, false),
        MoreAction("Put in Waiting Room", Icons.Default.PersonOff, false),
        MoreAction("Remove", Icons.Default.PersonRemove, true),
        MoreAction("Report", Icons.Default.Report, true)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFF2A2A2D))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            DragHandle()

            Spacer(modifier = Modifier.height(16.dp))

            // Participant avatar + name centered
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(participant.avatarColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = participant.initials,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = participant.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action list
            actions.forEach { action ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.label,
                        tint = if (action.isDestructive) Color(0xFFE53935) else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = action.label,
                        color = if (action.isDestructive) Color(0xFFE53935) else Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3A3A3D))
                    .clickable(onClick = onCancel)
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
    }
}

private data class MoreAction(
    val label: String,
    val icon: ImageVector,
    val isDestructive: Boolean
)

// ── Shared Components ────────────────────────────────────────────────────

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFD0D0D0))
        )
    }
}
