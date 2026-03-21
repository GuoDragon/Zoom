package com.example.zoom.presentation.joinmeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ZoomActionPageTopBar
import com.example.zoom.ui.components.ZoomInsetDivider
import com.example.zoom.ui.components.ZoomPageSurface
import com.example.zoom.ui.components.ZoomPrimaryActionButton
import com.example.zoom.ui.components.ZoomSettingSwitchRow
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomGray
import com.example.zoom.ui.theme.ZoomTextSecondary

@Composable
fun JoinMeetingScreen(onBackClick: () -> Unit) {
    var uiState by remember { mutableStateOf<JoinMeetingUiState?>(null) }

    val view = remember {
        object : JoinMeetingContract.View {
            override fun showContent(content: JoinMeetingUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        JoinMeetingPresenter(view).loadData()
    }

    uiState?.let { screenState ->
        var meetingId by remember { mutableStateOf("") }
        var screenName by remember { mutableStateOf("") }
        var audioOff by remember(screenState) { mutableStateOf(screenState.audioOff) }
        var videoOff by remember(screenState) { mutableStateOf(screenState.videoOff) }

        Scaffold(
            topBar = {
                ZoomActionPageTopBar(
                    title = "Join meeting",
                    onCancelClick = onBackClick
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ZoomGray)
                    .padding(padding)
            ) {
                ZoomPageSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    MeetingIdField(
                        value = meetingId,
                        onValueChange = { meetingId = it }
                    )
                    Text(
                        text = "Join with a personal link name",
                        color = ZoomBlue,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                SimpleInputField(
                    value = screenName,
                    onValueChange = { screenName = it },
                    placeholder = "Screen name"
                )

                Spacer(modifier = Modifier.height(20.dp))

                ZoomPrimaryActionButton(
                    text = "Join",
                    onClick = {},
                    enabled = meetingId.isNotBlank(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Text(
                    text = "If you received an invitation link, tap on the link again to join the meeting",
                    color = ZoomTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Join options",
                    fontSize = 14.sp,
                    color = ZoomTextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Column(modifier = Modifier.background(Color.White)) {
                    ZoomSettingSwitchRow(
                        title = "Don't connect to audio",
                        checked = audioOff,
                        onCheckedChange = { audioOff = it }
                    )
                    ZoomInsetDivider()
                    ZoomSettingSwitchRow(
                        title = "Turn off my video",
                        checked = videoOff,
                        onCheckedChange = { videoOff = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun MeetingIdField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, color = Color(0xFF3A4A5F)),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text("Meeting ID", color = Color(0xFFA7B0BC), fontSize = 18.sp)
                }
                innerTextField()
            }
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color(0xFFB6BFCA)
        )
    }
}

@Composable
private fun SimpleInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, color = Color(0xFF3A4A5F)),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(placeholder, color = Color(0xFFA7B0BC), fontSize = 18.sp)
                }
                innerTextField()
            }
        )
    }
}
