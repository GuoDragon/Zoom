package com.example.zoom.presentation.directchat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ZoomTopBarInsets
import com.example.zoom.ui.theme.ZoomBlue
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectChatScreen(
    userId: String,
    onBackClick: () -> Unit
) {
    var uiState by remember { mutableStateOf<DirectChatUiState?>(null) }
    var localMessages by remember { mutableStateOf<List<DirectChatMessageUi>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val inputFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val view = remember {
        object : DirectChatContract.View {
            override fun showContent(content: DirectChatUiState) {
                uiState = content
                localMessages = content.messages
            }
        }
    }
    val presenter = remember(view) { DirectChatPresenter(view) }

    LaunchedEffect(presenter, userId) {
        presenter.loadData(userId)
        delay(250)
        inputFocusRequester.requestFocus()
        keyboardController?.show()
    }

    LaunchedEffect(localMessages.size) {
        if (localMessages.isNotEmpty()) {
            listState.animateScrollToItem(localMessages.lastIndex)
        }
    }

    val title = uiState?.partnerName ?: "Direct chat"
    val quickMessages = remember(title) {
        if (title == "Natalie Cox") {
            listOf("下周一的会议我需要请个假", "收到", "谢谢")
        } else {
            listOf("I'll join soon", "Thanks", "Talk later")
        }
    }

    fun sendMessageContent(content: String) {
        if (content.isBlank()) return

        presenter.sendMessage(userId, content)?.let { newMessage ->
            localMessages = localMessages + newMessage
            inputText = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold, maxLines = 1) },
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
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(localMessages, key = { it.messageId }) { message ->
                    DirectChatBubble(message)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                QuickMessageRow(
                    messages = quickMessages,
                    onMessageClick = ::sendMessageContent
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(inputFocusRequester),
                        placeholder = { Text("Message") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF2F4F7),
                            unfocusedContainerColor = Color(0xFFF2F4F7),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .width(76.dp)
                            .height(48.dp)
                            .background(
                                color = if (inputText.isBlank()) Color(0xFFE5E7EB) else ZoomBlue,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable(enabled = inputText.isNotBlank()) {
                                sendMessageContent(inputText)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Send",
                            color = if (inputText.isBlank()) Color(0xFF94A3B8) else Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickMessageRow(
    messages: List<String>,
    onMessageClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        messages.forEach { message ->
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .clickable { onMessageClick(message) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    text = message,
                    color = Color(0xFF2A3441),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun DirectChatBubble(message: DirectChatMessageUi) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isSelf) Alignment.End else Alignment.Start
    ) {
        Text(
            text = if (message.isSelf) {
                "You · ${message.timestampLabel}"
            } else {
                "${message.senderName} · ${message.timestampLabel}"
            },
            color = Color(0xFF8D97A5),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(
                    color = if (message.isSelf) Color(0xFFE6F1FF) else Color.White,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = Color(0xFF2A3441),
                fontSize = 15.sp
            )
        }
    }
}
