package com.example.zoom.presentation.schedulemeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.zoom.ui.components.ZoomTopBarInsets
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomTextPrimary
import com.example.zoom.ui.theme.ZoomTextSecondary
import kotlinx.coroutines.launch

@Composable
fun ScheduleSingleSelectPage(
    title: String,
    options: List<String>,
    selectedValue: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onSelect: (String) -> Unit
) {
    Scaffold(
        topBar = {
            ScheduleBackDoneTopBar(title = title, onBack = onBack, onDone = onDone)
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            items(options) { option ->
                ScheduleCheckRow(
                    title = option,
                    selected = option == selectedValue,
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}

@Composable
fun ScheduleEncryptionPage(
    title: String,
    options: List<ScheduleMeetingEncryptionOption>,
    selectedValue: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onSelect: (String) -> Unit
) {
    Scaffold(
        topBar = {
            ScheduleBackDoneTopBar(title = title, onBack = onBack, onDone = onDone)
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
                Text(
                    text = "SELECT ONE",
                    fontSize = 12.sp,
                    color = ZoomTextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
            items(options) { option ->
                ScheduleCheckRow(
                    title = option.value,
                    subtitle = option.detail,
                    summary = option.summary,
                    selected = option.value == selectedValue,
                    onClick = { onSelect(option.value) }
                )
            }
        }
    }
}

@Composable
fun ScheduleTimeZonePage(
    title: String,
    options: List<ScheduleMeetingTimeZoneOption>,
    selectedTimeZoneId: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onSelect: (String) -> Unit
) {
    val alphabetLetters = remember { ('A'..'Z').toList() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var highlightedLetter by remember { mutableStateOf('A') }
    val firstIndexByLetter = remember(options) {
        alphabetLetters.associateWith { letter ->
            options.indexOfFirst { zone -> zone.alphabetBucket == letter }
                .takeIf { it >= 0 }
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex, options) {
        val visibleLetter = options
            .getOrNull(listState.firstVisibleItemIndex)
            ?.alphabetBucket
            ?.takeIf { it in 'A'..'Z' }
        if (visibleLetter != null) {
            highlightedLetter = visibleLetter
        }
    }

    Scaffold(
        topBar = {
            ScheduleBackDoneTopBar(title = title, onBack = onBack, onDone = onDone)
        },
        containerColor = Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                items(options) { zone ->
                    ScheduleCheckRow(
                        title = zone.displayName,
                        summary = zone.gmtOffsetLabel,
                        selected = zone.id == selectedTimeZoneId,
                        onClick = { onSelect(zone.id) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                alphabetLetters.forEach { letter ->
                    val targetIndex = findNearestTimeZoneIndex(
                        tappedLetter = letter,
                        alphabetLetters = alphabetLetters,
                        firstIndexByLetter = firstIndexByLetter
                    )
                    val isHighlighted = highlightedLetter == letter
                    Box(
                        modifier = Modifier
                            .padding(vertical = 1.dp)
                            .size(16.dp)
                            .background(
                                color = if (isHighlighted) ZoomBlue else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable(enabled = targetIndex != null) {
                                highlightedLetter = letter
                                targetIndex?.let { index ->
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.toString(),
                            color = if (isHighlighted) Color.White else ZoomBlue,
                            fontSize = 11.sp,
                            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

private fun findNearestTimeZoneIndex(
    tappedLetter: Char,
    alphabetLetters: List<Char>,
    firstIndexByLetter: Map<Char, Int?>
): Int? {
    firstIndexByLetter[tappedLetter]?.let { return it }
    val tappedIndex = alphabetLetters.indexOf(tappedLetter)
    if (tappedIndex < 0) return null

    for (offset in 1 until alphabetLetters.size) {
        val forwardIndex = tappedIndex + offset
        if (forwardIndex < alphabetLetters.size) {
            val forwardMatch = firstIndexByLetter[alphabetLetters[forwardIndex]]
            if (forwardMatch != null) return forwardMatch
        }

        val backwardIndex = tappedIndex - offset
        if (backwardIndex >= 0) {
            val backwardMatch = firstIndexByLetter[alphabetLetters[backwardIndex]]
            if (backwardMatch != null) return backwardMatch
        }
    }
    return null
}

@Composable
fun ScheduleAddInviteesPage(
    invitees: List<ScheduleMeetingInviteeOption>,
    query: String,
    selectedUserIds: Set<String>,
    onQueryChange: (String) -> Unit,
    onToggleUser: (String) -> Unit,
    onSelectAll: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    val filtered = invitees.filter {
        query.isBlank() ||
            it.name.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true)
    }
    val alphabetLetters = remember { ('A'..'Z').toList() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var highlightedLetter by remember(filtered) {
        mutableStateOf(
            filtered.firstOrNull()
                ?.name
                ?.firstOrNull()
                ?.uppercaseChar()
                ?.takeIf { it in 'A'..'Z' }
                ?: 'A'
        )
    }
    val firstIndexByLetter = remember(filtered) {
        alphabetLetters.associateWith { letter ->
            filtered.indexOfFirst { option ->
                option.name.firstOrNull()?.uppercaseChar() == letter
            }.takeIf { it >= 0 }
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex, filtered) {
        val visibleLetter = filtered
            .getOrNull(listState.firstVisibleItemIndex)
            ?.name
            ?.firstOrNull()
            ?.uppercaseChar()
            ?.takeIf { it in 'A'..'Z' }
        if (visibleLetter != null) {
            highlightedLetter = visibleLetter
        }
    }

    Scaffold(
        topBar = {
            ScheduleCancelOkTopBar(
                title = "Add invitees",
                onCancel = onCancel,
                onConfirm = onConfirm,
                confirmLabel = "OK"
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Select all",
                        color = ZoomBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable(onClick = onSelectAll)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .background(Color(0xFFF3F5F8), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = ZoomTextPrimary,
                            fontSize = 16.sp
                        ),
                        decorationBox = { inner ->
                            if (query.isBlank()) {
                                Text(
                                    text = "Search by name or email",
                                    color = Color(0xFFA8B0BC),
                                    fontSize = 16.sp
                                )
                            }
                            inner()
                        }
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    items(filtered) { invitee ->
                        ScheduleCheckRow(
                            title = invitee.name,
                            summary = invitee.email,
                            selected = selectedUserIds.contains(invitee.userId),
                            onClick = { onToggleUser(invitee.userId) }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                alphabetLetters.forEach { letter ->
                    val targetIndex = findNearestTimeZoneIndex(
                        tappedLetter = letter,
                        alphabetLetters = alphabetLetters,
                        firstIndexByLetter = firstIndexByLetter
                    )
                    val isHighlighted = highlightedLetter == letter
                    Box(
                        modifier = Modifier
                            .padding(vertical = 1.dp)
                            .size(16.dp)
                            .background(
                                color = if (isHighlighted) ZoomBlue else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable(enabled = targetIndex != null) {
                                highlightedLetter = letter
                                targetIndex?.let { index ->
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.toString(),
                            color = if (isHighlighted) Color.White else ZoomBlue,
                            fontSize = 11.sp,
                            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleBackDoneTopBar(
    title: String,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    androidx.compose.material3.TopAppBar(
        title = {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
        },
        navigationIcon = {
            Row(
                modifier = Modifier
                    .padding(start = 2.dp)
                    .clickable(onClick = onBack),
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
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        },
        actions = {
            Text(
                text = "Done",
                color = ZoomBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(onClick = onDone)
            )
        },
        windowInsets = ZoomTopBarInsets,
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleCancelOkTopBar(
    title: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    confirmLabel: String
) {
    androidx.compose.material3.TopAppBar(
        title = { Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp) },
        navigationIcon = {
            Text(
                text = "Cancel",
                color = ZoomBlue,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable(onClick = onCancel)
            )
        },
        actions = {
            Text(
                text = confirmLabel,
                color = ZoomBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(onClick = onConfirm)
            )
        },
        windowInsets = ZoomTopBarInsets,
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
}

@Composable
private fun ScheduleCheckRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    subtitle: String? = null,
    summary: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = ZoomTextPrimary,
                fontSize = 17.sp
            )
            if (!summary.isNullOrBlank()) {
                Text(
                    text = summary,
                    color = ZoomTextSecondary,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = ZoomTextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = ZoomBlue,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
}
