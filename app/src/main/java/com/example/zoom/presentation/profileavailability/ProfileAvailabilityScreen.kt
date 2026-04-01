package com.example.zoom.presentation.profileavailability

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ZoomTopBarInsets
import com.example.zoom.ui.theme.ZoomBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAvailabilityScreen(onBackClick: () -> Unit) {
    var uiState by remember { mutableStateOf<ProfileAvailabilityUiState?>(null) }

    val view = remember {
        object : ProfileAvailabilityContract.View {
            override fun showContent(content: ProfileAvailabilityUiState) {
                uiState = content
            }
        }
    }
    val presenter = remember(view) { ProfileAvailabilityPresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    val state = uiState ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Availability", fontWeight = FontWeight.SemiBold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            items(state.options) { option ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            presenter.updateAvailability(option)
                            uiState = uiState?.copy(currentAvailability = option)
                        }
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = option,
                        color = Color(0xFF243447),
                        fontSize = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (option == state.currentAvailability) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = ZoomBlue
                        )
                    }
                }
                HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
            }
        }
    }
}
