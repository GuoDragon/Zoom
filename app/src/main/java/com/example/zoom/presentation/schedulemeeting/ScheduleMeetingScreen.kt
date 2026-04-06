package com.example.zoom.presentation.schedulemeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ZoomActionPageTopBar
import com.example.zoom.ui.components.ZoomInsetDivider
import com.example.zoom.ui.components.ZoomPageSurface
import com.example.zoom.ui.components.ZoomSettingSwitchRow
import com.example.zoom.ui.components.ZoomSettingValueRow
import com.example.zoom.ui.components.ZoomSettingsSectionTitle
import com.example.zoom.ui.theme.ZoomTextSecondary

@Composable
fun ScheduleMeetingScreen(
    onBackClick: () -> Unit,
    editingMeetingId: String?,
    onSaveSuccess: (String) -> Unit
) {
    var initialState by remember { mutableStateOf<ScheduleMeetingInitialState?>(null) }
    var draft by remember { mutableStateOf<ScheduleMeetingDraft?>(null) }
    var subPage by remember { mutableStateOf(ScheduleMeetingSubPage.MAIN) }
    var showStartsDialog by remember { mutableStateOf(false) }
    var showDurationDialog by remember { mutableStateOf(false) }
    var inviteeWorkingSet by remember { mutableStateOf<Set<String>>(emptySet()) }
    var inviteeSearchQuery by remember { mutableStateOf("") }

    val view = remember(onSaveSuccess) {
        object : ScheduleMeetingContract.View {
            override fun showInitialState(state: ScheduleMeetingInitialState) {
                initialState = state
                draft = state.draft
            }

            override fun onMeetingSaved(meetingId: String) {
                onSaveSuccess(meetingId)
            }
        }
    }
    val presenter = remember(view) { ScheduleMeetingPresenter(view) }

    LaunchedEffect(presenter, editingMeetingId) {
        presenter.loadData(editingMeetingId)
    }

    val screenState = initialState ?: return
    val screenDraft = draft ?: return
    val selectedTimeZone = screenState.timeZoneOptions.find { it.id == screenDraft.timeZoneId }

    when (subPage) {
        ScheduleMeetingSubPage.TIME_ZONE -> {
            ScheduleTimeZonePage(
                title = selectedTimeZone?.displayName ?: "Time zone",
                options = screenState.timeZoneOptions,
                selectedTimeZoneId = screenDraft.timeZoneId,
                onBack = { subPage = ScheduleMeetingSubPage.MAIN },
                onDone = { subPage = ScheduleMeetingSubPage.MAIN },
                onSelect = { selectedId ->
                    draft = draft?.copy(timeZoneId = selectedId)
                }
            )
            return
        }
        ScheduleMeetingSubPage.REPEAT -> {
            ScheduleSingleSelectPage(
                title = "Repeat",
                options = screenState.repeatOptions,
                selectedValue = screenDraft.repeat,
                onBack = { subPage = ScheduleMeetingSubPage.MAIN },
                onDone = { subPage = ScheduleMeetingSubPage.MAIN },
                onSelect = { option -> draft = draft?.copy(repeat = option) }
            )
            return
        }
        ScheduleMeetingSubPage.CALENDAR -> {
            ScheduleSingleSelectPage(
                title = "Calendar",
                options = screenState.calendarOptions,
                selectedValue = screenDraft.calendar,
                onBack = { subPage = ScheduleMeetingSubPage.MAIN },
                onDone = { subPage = ScheduleMeetingSubPage.MAIN },
                onSelect = { option -> draft = draft?.copy(calendar = option) }
            )
            return
        }
        ScheduleMeetingSubPage.ENCRYPTION -> {
            ScheduleEncryptionPage(
                title = "Encryption",
                options = screenState.encryptionOptions,
                selectedValue = screenDraft.encryption,
                onBack = { subPage = ScheduleMeetingSubPage.MAIN },
                onDone = { subPage = ScheduleMeetingSubPage.MAIN },
                onSelect = { option -> draft = draft?.copy(encryption = option) }
            )
            return
        }
        ScheduleMeetingSubPage.ADD_INVITEES -> {
            ScheduleAddInviteesPage(
                invitees = screenState.inviteeOptions,
                query = inviteeSearchQuery,
                selectedUserIds = inviteeWorkingSet,
                onQueryChange = { inviteeSearchQuery = it },
                onToggleUser = { userId ->
                    inviteeWorkingSet = if (inviteeWorkingSet.contains(userId)) {
                        inviteeWorkingSet - userId
                    } else {
                        inviteeWorkingSet + userId
                    }
                },
                onSelectAll = {
                    inviteeWorkingSet = screenState.inviteeOptions.map { it.userId }.toSet()
                },
                onCancel = {
                    inviteeWorkingSet = screenDraft.inviteeUserIds
                    subPage = ScheduleMeetingSubPage.MAIN
                },
                onConfirm = {
                    draft = draft?.copy(inviteeUserIds = inviteeWorkingSet)
                    subPage = ScheduleMeetingSubPage.MAIN
                }
            )
            return
        }
        ScheduleMeetingSubPage.MAIN -> Unit
    }

    Scaffold(
        topBar = {
            ZoomActionPageTopBar(
                title = "Schedule meeting",
                onCancelClick = onBackClick,
                actionText = "Save",
                onActionClick = { presenter.saveMeeting(screenDraft, editingMeetingId) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            item {
                ZoomPageSurface(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                    Text(
                        text = screenDraft.meetingTitle,
                        fontSize = 24.sp,
                        color = Color(0xFFC7CCD4),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Starts",
                        value = formatScheduleStartLabel(
                            startTimeMillis = screenDraft.startTimeMillis,
                            timeZoneId = screenDraft.timeZoneId
                        ),
                        modifier = Modifier.clickable { showStartsDialog = true }
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Duration",
                        value = formatDurationLabel(screenDraft.durationMinutes),
                        modifier = Modifier.clickable { showDurationDialog = true }
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Time zone",
                        value = selectedTimeZone?.displayName ?: screenDraft.timeZoneId,
                        modifier = Modifier.clickable { subPage = ScheduleMeetingSubPage.TIME_ZONE }
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Repeat",
                        value = screenDraft.repeat,
                        modifier = Modifier.clickable { subPage = ScheduleMeetingSubPage.REPEAT }
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Calendar",
                        value = screenDraft.calendar,
                        modifier = Modifier.clickable { subPage = ScheduleMeetingSubPage.CALENDAR }
                    )
                    ZoomInsetDivider()
                    ZoomSettingSwitchRow(
                        title = "Use personal meeting ID",
                        subtitle = screenState.personalMeetingId,
                        checked = screenDraft.usePersonalMeetingId,
                        onCheckedChange = { enabled ->
                            draft = draft?.copy(usePersonalMeetingId = enabled)
                        }
                    )
                }
            }

            item {
                Text(
                    text = "If this option is enabled, any meeting options that you change here will be applied to all meetings that use your personal meeting ID",
                    color = ZoomTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            item {
                ZoomSettingsSectionTitle(text = "SECURITY")
                ZoomPageSurface(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                    ZoomSettingSwitchRow(
                        title = "Require meeting passcode",
                        subtitle = "Only users who have the invite link or passcode can join the meeting",
                        checked = screenDraft.requirePasscode,
                        onCheckedChange = { enabled ->
                            draft = draft?.copy(requirePasscode = enabled)
                        }
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Passcode",
                        value = screenDraft.passcode,
                        showChevron = false
                    )
                    ZoomInsetDivider()
                    ZoomSettingSwitchRow(
                        title = "Enable waiting room",
                        subtitle = "Only users admitted by the host can join the meeting",
                        checked = screenDraft.waitingRoom,
                        onCheckedChange = { enabled ->
                            draft = draft?.copy(waitingRoom = enabled)
                        }
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Encryption",
                        value = screenDraft.encryption,
                        modifier = Modifier.clickable { subPage = ScheduleMeetingSubPage.ENCRYPTION }
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Invitees",
                        value = formatInviteesLabel(
                            selectedInvitees = screenDraft.inviteeUserIds,
                            allInvitees = screenState.inviteeOptions
                        ),
                        modifier = Modifier.clickable {
                            inviteeWorkingSet = screenDraft.inviteeUserIds
                            inviteeSearchQuery = ""
                            subPage = ScheduleMeetingSubPage.ADD_INVITEES
                        }
                    )
                    ZoomInsetDivider()
                    ZoomSettingSwitchRow(
                        title = "Continuous meeting chat",
                        subtitle = "Chat will continue before and after the meeting",
                        checked = screenDraft.continuousMeetingChat,
                        onCheckedChange = { enabled ->
                            draft = draft?.copy(continuousMeetingChat = enabled)
                        }
                    )
                }
            }

            item {
                ZoomSettingsSectionTitle(text = "MEETING OPTIONS")
                ZoomPageSurface(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                    ZoomSettingSwitchRow(
                        title = "Host video on",
                        checked = screenDraft.hostVideoOn,
                        onCheckedChange = { enabled ->
                            draft = draft?.copy(hostVideoOn = enabled)
                        }
                    )
                    ZoomInsetDivider()
                    ZoomSettingSwitchRow(
                        title = "Participant video on",
                        checked = screenDraft.participantVideoOn,
                        onCheckedChange = { enabled ->
                            draft = draft?.copy(participantVideoOn = enabled)
                        }
                    )
                    ZoomInsetDivider()
                    ZoomSettingValueRow(
                        title = "Advanced options",
                        value = "",
                        showChevron = true
                    )
                }
            }
        }
    }

    if (showStartsDialog) {
        ScheduleStartsTimeDialog(
            selectedTimeMillis = screenDraft.startTimeMillis,
            timeZoneId = screenDraft.timeZoneId,
            onSelect = { selectedTime ->
                draft = draft?.copy(startTimeMillis = selectedTime)
            },
            onDone = { showStartsDialog = false },
            onDismiss = { showStartsDialog = false }
        )
    }

    if (showDurationDialog) {
        ScheduleDurationDialog(
            selectedDurationMinutes = screenDraft.durationMinutes,
            onSelect = { selectedDuration ->
                draft = draft?.copy(durationMinutes = selectedDuration)
            },
            onDone = { showDurationDialog = false },
            onDismiss = { showDurationDialog = false }
        )
    }
}

private fun formatInviteesLabel(
    selectedInvitees: Set<String>,
    allInvitees: List<ScheduleMeetingInviteeOption>
): String {
    if (selectedInvitees.isEmpty()) return "None"
    val names = allInvitees.filter { selectedInvitees.contains(it.userId) }.map { it.name }
    return if (names.size == 1) {
        names.first()
    } else {
        "${names.first()} +${names.size - 1}"
    }
}
