package com.example.zoom.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onContactClick: (String) -> Unit
) {
    var uiState by remember { mutableStateOf<SearchUiState?>(null) }
    val searchFocusRequester = remember { FocusRequester() }

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

    uiState?.let { state ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        Scaffold(containerColor = Color.White) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
            ) {
                SearchHeader(
                    query = state.query,
                    onQueryChange = presenter::onQueryChanged,
                    onClearQuery = { presenter.onQueryChanged("") },
                    onCancelClick = onBackClick,
                    focusRequester = searchFocusRequester
                )

                SearchTabs(
                    tabs = state.tabs,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = presenter::onCategorySelected
                )

                if (state.filters.isNotEmpty()) {
                    SearchFilterRow(
                        filters = state.filters,
                        onFilterClick = presenter::onFilterChipClicked
                    )
                }

                SearchPageContent(
                    state = state,
                    onContactClick = onContactClick
                )
            }
        }

        state.activeSheet?.let { sheet ->
            ModalBottomSheet(
                onDismissRequest = presenter::onSheetDismiss,
                sheetState = sheetState,
                dragHandle = null,
                containerColor = Color.White
            ) {
                SearchFilterSheet(
                    state = sheet,
                    onReset = presenter::onSheetReset,
                    onDone = presenter::onSheetDone,
                    onSearchChange = presenter::onSheetSearchChanged,
                    onOptionSelected = presenter::onSheetOptionSelected
                )
            }
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onCancelClick: () -> Unit,
    focusRequester: FocusRequester
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
            focusRequester = focusRequester,
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
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(focusRequester) {
        delay(250)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.focusRequester(focusRequester),
        singleLine = true,
        textStyle = TextStyle(
            color = Color(0xFF263648),
            fontSize = 16.sp
        ),
        placeholder = {
            Text(
                text = "Search",
                color = Color(0xFF95A0AE),
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF7B8796),
                modifier = Modifier.size(18.dp)
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
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
        } else {
            null
        },
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF3F5F8),
            unfocusedContainerColor = Color(0xFFF3F5F8),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = ZoomBlue
        )
    )
}

@Composable
private fun SearchTabs(
    tabs: List<SearchCategory>,
    selectedCategory: SearchCategory,
    onCategorySelected: (SearchCategory) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = tabs.indexOf(selectedCategory),
        edgePadding = 16.dp,
        containerColor = Color.White,
        contentColor = ZoomBlue,
        divider = { HorizontalDivider(color = Color(0xFFF0F2F5)) }
    ) {
        tabs.forEach { category ->
            Tab(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = category.title,
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
private fun SearchFilterRow(
    filters: List<SearchFilterChipUiState>,
    onFilterClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        filters.forEach { filter ->
            SearchFilterChip(state = filter, onClick = { onFilterClick(filter.id) })
        }
    }
}

@Composable
private fun SearchFilterChip(
    state: SearchFilterChipUiState,
    onClick: () -> Unit
) {
    val backgroundColor = if (state.selected) Color(0xFFEAF2FF) else Color.White
    val borderColor = if (state.selected) Color(0xFFBED4FF) else Color(0xFFD9DEE6)
    val textColor = if (state.selected) ZoomBlue else Color(0xFF3B495A)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = backgroundColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.border(
            width = 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(18.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.label,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (state.style == SearchFilterChipStyle.Sheet) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchFilterSheet(
    state: SearchFilterSheetUiState,
    onReset: () -> Unit,
    onDone: () -> Unit,
    onSearchChange: (String) -> Unit,
    onOptionSelected: (Int, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        SearchSheetHeader(
            title = state.title,
            showDone = state.showDone,
            onReset = onReset,
            onDone = onDone
        )

        if (state.showSearchField) {
            SearchSheetSearchField(
                query = state.searchQuery,
                onQueryChange = onSearchChange
            )
        }

        state.groups.forEachIndexed { index, group ->
            group.options.forEachIndexed { optionIndex, option ->
                SearchSheetOptionRow(
                    option = option,
                    groupStyle = group.style,
                    onClick = { onOptionSelected(index, option.id) }
                )
                if (optionIndex != group.options.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFFF1F3F6),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
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
private fun SearchSheetSearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F7FA))
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color(0xFF95A0AE),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = TextStyle(
                color = Color(0xFF263648),
                fontSize = 15.sp
            ),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "Search",
                        color = Color(0xFF95A0AE),
                        fontSize = 15.sp
                    )
                }
                innerTextField()
            }
        )
    }
    Spacer(modifier = Modifier.size(12.dp))
}

@Composable
private fun SearchSheetOptionRow(
    option: SearchSheetOptionUiState,
    groupStyle: SearchSheetGroupStyle,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (val leading = option.leading) {
            SearchSheetOptionLeading.Conversation -> SearchSheetIconBubble("C")
            SearchSheetOptionLeading.Bookmark -> SearchSheetIconBubble("B")
            SearchSheetOptionLeading.Person -> SearchSheetIconBubble("P")
            is SearchSheetOptionLeading.Initials -> SearchSheetInitialBubble(
                text = leading.text,
                backgroundColor = leading.backgroundColor
            )
            null -> if (groupStyle == SearchSheetGroupStyle.Radio) {
                SearchRadioIndicator(selected = option.selected)
            }
        }

        if (option.leading != null || groupStyle == SearchSheetGroupStyle.Radio) {
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(
            text = option.label,
            color = Color(0xFF2C3C4D),
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        if (groupStyle == SearchSheetGroupStyle.List && option.selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = ZoomBlue,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SearchSheetIconBubble(symbol: String) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF5F7FA)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = symbol, fontSize = 14.sp)
    }
}

@Composable
private fun SearchSheetInitialBubble(
    text: String,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun SearchRadioIndicator(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = if (selected) ZoomBlue else Color(0xFFC8D0DB),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ZoomBlue)
            )
        }
    }
}
