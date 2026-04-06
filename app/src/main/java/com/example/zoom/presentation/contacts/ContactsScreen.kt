package com.example.zoom.presentation.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.zoom.ui.components.ZoomTopBarInsets
import com.example.zoom.ui.theme.ZoomBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onBackClick: () -> Unit,
    onContactClick: (String) -> Unit
) {
    var uiState by remember { mutableStateOf<ContactsUiState?>(null) }
    var query by remember { mutableStateOf("") }

    val view = remember {
        object : ContactsContract.View {
            override fun showContent(content: ContactsUiState) {
                uiState = content
            }
        }
    }
    val presenter = remember(view) { ContactsPresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    val state = uiState ?: return
    val filteredContacts = state.contacts.filter {
        query.isBlank() ||
            it.name.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true) ||
            it.phone.contains(query, ignoreCase = true)
    }
    val quickAccessContacts = remember(state.contacts, query) {
        if (query.isNotBlank()) {
            emptyList()
        } else {
            state.contacts
                .sortedWith(
                    compareBy<ContactListItemUi> { if (it.name == "Natalie Cox") 0 else 1 }
                        .thenBy { it.name }
                )
                .take(4)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ZoomBlue
                        )
                    }
                },
                windowInsets = ZoomTopBarInsets,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            Text(
                text = "${state.totalCount} contacts",
                color = Color(0xFF6D7785),
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color(0xFFF2F4F7), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF97A1AF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF243447),
                        fontSize = 16.sp
                    ),
                    decorationBox = { inner ->
                        if (query.isBlank()) {
                            Text(
                                text = "Search contacts",
                                color = Color(0xFF97A1AF),
                                fontSize = 16.sp
                            )
                        }
                        inner()
                    }
                )
            }

            if (quickAccessContacts.isNotEmpty()) {
                Text(
                    text = "Quick access",
                    color = Color(0xFF6D7785),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 6.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    quickAccessContacts.forEach { contact ->
                        Column(
                            modifier = Modifier
                                .width(104.dp)
                                .padding(end = 12.dp)
                                .background(Color(0xFFF5F7FA), RoundedCornerShape(18.dp))
                                .clickable { onContactClick(contact.userId) }
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFF4D76D0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contact.initials,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = contact.name,
                                color = Color(0xFF243447),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredContacts, key = { it.userId }) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onContactClick(contact.userId) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFF4D76D0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.initials,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.name,
                                color = Color(0xFF243447),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            if (contact.email.isNotBlank()) {
                                Text(
                                    text = contact.email,
                                    color = Color(0xFF8D97A5),
                                    fontSize = 13.sp
                                )
                            } else if (contact.phone.isNotBlank()) {
                                Text(
                                    text = contact.phone,
                                    color = Color(0xFF8D97A5),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
                }
                if (filteredContacts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No contacts found",
                                color = Color(0xFF8D97A5),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
