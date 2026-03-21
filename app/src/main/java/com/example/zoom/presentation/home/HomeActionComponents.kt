package com.example.zoom.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.KeyboardKey

@Composable
fun HomeActionCard(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = color),
            modifier = Modifier
                .size(64.dp)
                .clickable(onClick = onClick)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun ShareScreenOverlay(onDismiss: () -> Unit) {
    var roomCode by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.22f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
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
                        Text("Share screen", fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Enter room code or meeting ID to share to a Zoom Room",
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
                                value = roomCode,
                                onValueChange = { roomCode = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 16.sp,
                                    color = Color(0xFF465466)
                                ),
                                decorationBox = { innerTextField ->
                                    if (roomCode.isEmpty()) {
                                        Text(
                                            "Room code or meeting ID",
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
                            ShareDialogAction(
                                text = "Cancel",
                                color = Color(0xFF2687FF),
                                modifier = Modifier.weight(1f),
                                onClick = onDismiss
                            )
                            Box(
                                modifier = Modifier
                                    .width(0.6.dp)
                                    .height(54.dp)
                                    .background(Color(0xFFE7EBF0))
                            )
                            ShareDialogAction(
                                text = "OK",
                                color = Color(0xFFB0B7C3),
                                modifier = Modifier.weight(1f),
                                onClick = {}
                            )
                        }
                    }
                }
            }

            FakeAlphabetKeyboard()
        }
    }
}

@Composable
private fun ShareDialogAction(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = color, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun FakeAlphabetKeyboard() {
    val firstRow = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val secondRow = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val thirdRow = listOf("Shift", "z", "x", "c", "v", "b", "n", "m", "Del")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD7DCE4))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        KeyboardRow(firstRow)
        KeyboardRow(secondRow, horizontalPadding = 18.dp)
        KeyboardRow(thirdRow)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            KeyboardKey(label = "123", modifier = Modifier.weight(1.2f))
            KeyboardKey(label = "space", modifier = Modifier.weight(2.2f))
            KeyboardKey(label = "return", modifier = Modifier.weight(1.4f))
        }
    }
}

@Composable
private fun KeyboardRow(
    keys: List<String>,
    horizontalPadding: androidx.compose.ui.unit.Dp = 0.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        keys.forEach { key ->
            KeyboardKey(label = key, modifier = Modifier.weight(1f))
        }
    }
}
