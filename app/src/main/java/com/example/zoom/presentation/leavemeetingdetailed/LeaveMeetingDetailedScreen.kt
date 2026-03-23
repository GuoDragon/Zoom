package com.example.zoom.presentation.leavemeetingdetailed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LeaveMeetingDetailedScreen(
    onEndForAllClick: () -> Unit,
    onLeaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var uiState by remember { mutableStateOf<LeaveMeetingDetailedUiState?>(null) }

    val view = remember {
        object : LeaveMeetingDetailedContract.View {
            override fun showContent(content: LeaveMeetingDetailedUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        LeaveMeetingDetailedPresenter(view).loadData()
    }

    uiState?.let { screenState ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF111111))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(132.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF507B27),
                                Color(0xFF315613).copy(alpha = 0.92f),
                                Color.Transparent
                            )
                        )
                    )
                    .blur(26.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                DetailedLeaveAction(
                    text = screenState.endForAllLabel,
                    backgroundColor = Color(0xFFE7184B),
                    textColor = Color.White,
                    onClick = onEndForAllClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                DetailedLeaveAction(
                    text = screenState.leaveLabel,
                    backgroundColor = Color(0xFF323338),
                    textColor = Color(0xFFE25A74),
                    onClick = onLeaveClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                DetailedLeaveAction(
                    text = screenState.cancelLabel,
                    backgroundColor = Color(0xFF323338),
                    textColor = Color(0xFF6C8BFF),
                    onClick = onCancelClick
                )
            }
        }
    }
}

@Composable
private fun DetailedLeaveAction(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
