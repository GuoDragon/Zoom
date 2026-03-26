package com.example.zoom.presentation.meetinginfodetailed

import androidx.compose.runtime.Composable

@Composable
fun MeetingInfoDetailedScreen(onBackClick: () -> Unit) {
    MeetingInfoPage(
        onDismiss = onBackClick,
        showScrim = false
    )
}
