package com.example.zoom.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue

private enum class SearchSheet {
    Date,
    Type
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onMessageDetailClick: (String) -> Unit = {},
    onChatDetailClick: (String) -> Unit = {},
    onMeetingDetailClick: (String) -> Unit = {}
) {
    var uiState by remember { mutableStateOf<SearchUiState?>(null) }

    val presenter = remember {
        val view = object : SearchContract.View {
            override fun showContent(content: SearchUiState) {
                uiState = content
            }
        }
        SearchPresenter(view)
    }

    LaunchedEffect(Unit) {
        presenter.loadData()
    }

    uiState?.let { screenState ->
        var query by remember(screenState.tabs) { mutableStateOf("") }
        var selectedTabIndex by remember(screenState.tabs) { mutableIntStateOf(0) }
        var selectedDate by remember(screenState.tabs) { mutableStateOf(screenState.dateOptions.first()) }
        var selectedType by remember(screenState.tabs) { mutableStateOf(screenState.typeOptions.first()) }
        var activeSheet by remember { mutableStateOf<SearchSheet?>(null) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        Scaffold(containerColor = Color.White) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
            ) {
                SearchHeader(
                    query = query,
                    onQueryChange = {
                        query = it
                        presenter.search(it)
                    },
                    onClearQuery = {
                        query = ""
                        presenter.search("")
                    },
                    onCancelClick = onBackClick
                )

                SearchTabs(
                    tabs = screenState.tabs,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SearchFilterChip(
                        label = if (selectedDate == screenState.dateOptions.first()) "Date" else selectedDate,
                        selected = selectedDate != screenState.dateOptions.first(),
                        onClick = { activeSheet = SearchSheet.Date }
                    )
                    SearchFilterChip(
                        label = if (selectedType == screenState.typeOptions.first()) "Type" else selectedType,
                        selected = selectedType != screenState.typeOptions.first(),
                        onClick = { activeSheet = SearchSheet.Type }
                    )
                }

                if (query.isEmpty()) {
                    SearchEmptyState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp)
                    )
                } else {
                    // Tab indices: 0=Top results, 1=Messages, 2=Chats and channels,
                    // 3=Meetings, 4=Contacts, 5=Files, 6=Docs, 7=Whiteboards, 8=Mail
                    when (selectedTabIndex) {
                        0 -> SearchTopResultsContent(
                            state = screenState,
                            onMessageClick = onMessageDetailClick,
                            onChatClick = onChatDetailClick,
                            onMeetingClick = onMeetingDetailClick
                        )
                        1 -> SearchMessagesContent(
                            results = screenState.messageResults,
                            onMessageClick = onMessageDetailClick
                        )
                        2 -> SearchChatsContent(
                            results = screenState.chatResults,
                            onChatClick = onChatDetailClick
                        )
                        3 -> SearchMeetingsContent(
                            results = screenState.meetingResults,
                            onMeetingClick = onMeetingDetailClick
                        )
                        4 -> SearchContactsContent(
                            results = screenState.contactResults
                        )
                        else -> SearchEmptyResultContent()
                    }
                }
            }
        }

        if (activeSheet != null) {
            ModalBottomSheet(
                onDismissRequest = { activeSheet = null },
                sheetState = sheetState,
                dragHandle = null,
                containerColor = Color.White
            ) {
                when (activeSheet) {
                    SearchSheet.Date -> SearchDateSheet(
                        options = screenState.dateOptions,
                        selectedOption = selectedDate,
                        onReset = { selectedDate = screenState.dateOptions.first() },
                        onOptionSelected = { selectedDate = it },
                        onDone = { activeSheet = null }
                    )

                    SearchSheet.Type -> SearchTypeSheet(
                        options = screenState.typeOptions,
                        selectedOption = selectedType,
                        onReset = {
                            selectedType = screenState.typeOptions.first()
                            activeSheet = null
                        },
                        onOptionSelected = {
                            selectedType = it
                            activeSheet = null
                        }
                    )

                    null -> Unit
                }
            }
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onCancelClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchInputField(
            query = query,
            onQueryChange = onQueryChange,
            onClearQuery = onClearQuery,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Cancel",
            color = ZoomBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(onClick = onCancelClick)
        )
    }
}

@Composable
private fun SearchInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF3F5F8))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color(0xFF7B8796),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = TextStyle(
                color = Color(0xFF263648),
                fontSize = 16.sp
            ),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "Search",
                        color = Color(0xFF95A0AE),
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        )
        if (query.isNotEmpty()) {
            IconButton(
                onClick = onClearQuery,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = Color(0xFF95A0AE)
                )
            }
        }
    }
}

@Composable
private fun SearchTabs(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 16.dp,
        containerColor = Color.White,
        contentColor = ZoomBlue,
        divider = { HorizontalDivider(color = Color(0xFFF0F2F5)) }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
private fun SearchFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (selected) Color(0xFFEAF2FF) else Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.border(
            width = 1.dp,
            color = if (selected) Color(0xFFBED4FF) else Color(0xFFD9DEE6),
            shape = RoundedCornerShape(18.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (selected) ZoomBlue else Color(0xFF3B495A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (selected) ZoomBlue else Color(0xFF6E7A89),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SearchEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(88.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F5F8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF8F9BAA),
                modifier = Modifier.size(42.dp)
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "Use keywords or filters to start your search",
            color = Color(0xFF5F6E80),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SearchTypeSheet(
    options: List<String>,
    selectedOption: String,
    onReset: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        SearchSheetHeader(
            title = "Type",
            showDone = false,
            onReset = onReset,
            onDone = {}
        )
        options.forEachIndexed { index, option ->
            SearchSheetOptionRow(
                label = option,
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) }
            )
            if (index != options.lastIndex) {
                HorizontalDivider(color = Color(0xFFF1F3F6))
            }
        }
    }
}

@Composable
private fun SearchDateSheet(
    options: List<String>,
    selectedOption: String,
    onReset: () -> Unit,
    onOptionSelected: (String) -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        SearchSheetHeader(
            title = "Date",
            showDone = true,
            onReset = onReset,
            onDone = onDone
        )
        options.forEachIndexed { index, option ->
            SearchSheetOptionRow(
                label = option,
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) }
            )
            if (index != options.lastIndex) {
                HorizontalDivider(color = Color(0xFFF1F3F6))
            }
        }
    }
}

@Composable
private fun SearchSheetHeader(
    title: String,
    showDone: Boolean,
    onReset: () -> Unit,
    onDone: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Reset",
            color = ZoomBlue,
            fontSize = 16.sp,
            modifier = Modifier.clickable(onClick = onReset)
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF243447)
            )
        }
        if (showDone) {
            Text(
                text = "Done",
                color = ZoomBlue,
                fontSize = 16.sp,
                modifier = Modifier.clickable(onClick = onDone)
            )
        } else {
            Spacer(modifier = Modifier.width(38.dp))
        }
    }
}

@Composable
private fun SearchSheetOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (selected) ZoomBlue else Color(0xFF2C3C4D),
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = ZoomBlue,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
