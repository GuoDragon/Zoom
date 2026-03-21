package com.example.zoom.presentation.hostmeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import com.example.zoom.ui.components.ZoomActionPageTopBar
import com.example.zoom.ui.components.ZoomInsetDivider
import com.example.zoom.ui.components.ZoomPageSurface
import com.example.zoom.ui.components.ZoomPrimaryActionButton
import com.example.zoom.ui.components.ZoomSettingSwitchRow

@Composable
fun HostMeetingScreen(onBackClick: () -> Unit) {
    var uiState by remember { mutableStateOf<HostMeetingUiState?>(null) }

    val view = remember {
        object : HostMeetingContract.View {
            override fun showContent(content: HostMeetingUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        HostMeetingPresenter(view).loadData()
    }

    uiState?.let { screenState ->
        var videoOn by remember(screenState) { mutableStateOf(screenState.videoOn) }
        var usePersonalMeetingId by remember(screenState) { mutableStateOf(screenState.usePersonalMeetingId) }

        Scaffold(
            topBar = {
                ZoomActionPageTopBar(
                    title = "Start a meeting",
                    onCancelClick = onBackClick
                )
            }
        ) { padding ->
            ZoomPageSurface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
            ) {
                ZoomSettingSwitchRow(
                    title = "Video on",
                    checked = videoOn,
                    onCheckedChange = { videoOn = it }
                )
                ZoomInsetDivider()
                ZoomSettingSwitchRow(
                    title = "Use personal meeting ID (PMI)",
                    subtitle = screenState.personalMeetingId,
                    checked = usePersonalMeetingId,
                    onCheckedChange = { usePersonalMeetingId = it }
                )
                Spacer(modifier = Modifier.height(28.dp))
                ZoomPrimaryActionButton(
                    text = "Start a meeting",
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}
