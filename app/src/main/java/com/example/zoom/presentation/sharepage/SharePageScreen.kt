package com.example.zoom.presentation.sharepage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.MeetingSessionConfig

@Composable
fun SharePageScreen(
    onDismiss: () -> Unit,
    onShareSuccess: (MeetingSessionConfig) -> Unit
) {
    var uiState by remember { mutableStateOf<SharePageUiState?>(null) }
    var shareCode by remember { mutableStateOf("") }

    val view = remember {
        object : SharePageContract.View {
            override fun showContent(content: SharePageUiState) {
                uiState = content
            }

            override fun dismiss() {
                onDismiss()
            }

            override fun navigateToMeeting(config: MeetingSessionConfig) {
                onShareSuccess(config)
            }
        }
    }
    val presenter = remember(view) { SharePagePresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    val state = uiState ?: return
    val canConfirm = shareCode.length == 8

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.22f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(horizontal = 56.dp)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = state.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.hint,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF576375),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 14.dp)
                            .fillMaxWidth()
                            .background(Color(0xFFF7F8FA), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        BasicTextField(
                            value = shareCode,
                            onValueChange = { input ->
                                shareCode = input.filter { it.isDigit() }.take(8)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = Color(0xFF465466)
                            ),
                            decorationBox = { innerTextField ->
                                if (shareCode.isEmpty()) {
                                    Text(
                                        text = state.placeholder,
                                        fontSize = 16.sp,
                                        color = Color(0xFFC0C6D0)
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SharePageAction(
                            text = state.cancelLabel,
                            color = Color(0xFF2687FF),
                            modifier = Modifier.weight(1f),
                            onClick = presenter::onCancel
                        )
                        Box(
                            modifier = Modifier
                                .width(0.6.dp)
                                .height(54.dp)
                                .background(Color(0xFFE7EBF0))
                        )
                        SharePageAction(
                            text = state.confirmLabel,
                            color = if (canConfirm) Color(0xFF2687FF) else Color(0xFFB0B7C3),
                            modifier = Modifier.weight(1f),
                            enabled = canConfirm,
                            onClick = {
                                presenter.onConfirmShare(shareCode)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SharePageAction(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(enabled = enabled, onClick = onClick)
            .background(Color.Transparent)
            .height(54.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
