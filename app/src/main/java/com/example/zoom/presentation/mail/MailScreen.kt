package com.example.zoom.presentation.mail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ZoomTopBar
import com.example.zoom.ui.theme.ZoomBlue

@Composable
fun MailScreen(
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    var avatarInitial by remember { mutableStateOf("?") }
    var showWelcome by remember { mutableStateOf(false) }

    val view = remember {
        object : MailContract.View {
            override fun showUiState(state: MailUiState) {
                avatarInitial = state.currentUserInitial
                showWelcome = state.showWelcome
            }
        }
    }
    val presenter = remember(view) { MailPresenter(view) }

    LaunchedEffect(Unit) {
        presenter.loadData()
    }

    Scaffold(
        topBar = {
            ZoomTopBar(
                title = "Mail",
                avatarInitial = avatarInitial,
                onAvatarClick = onAvatarClick,
                onSearchClick = onSearchClick
            )
        }
    ) { padding ->
        if (showWelcome) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = ZoomBlue
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Zoom Mail",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Send and receive emails directly in Zoom. Connect your Google or Microsoft account to get started.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ZoomBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sign in with Google", modifier = Modifier.padding(vertical = 4.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sign in with Microsoft", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
