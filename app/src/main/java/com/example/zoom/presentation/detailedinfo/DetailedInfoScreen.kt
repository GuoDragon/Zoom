package com.example.zoom.presentation.detailedinfo

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.zoom.ui.components.ProfileCard
import com.example.zoom.ui.components.ProfileCardDivider
import com.example.zoom.ui.components.ProfileIdentityHeader
import com.example.zoom.ui.components.ProfileListRow
import com.example.zoom.ui.components.ProfilePageBackground
import com.example.zoom.ui.components.ProfileSectionLabel
import com.example.zoom.ui.components.ZoomTopBarInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedInfoScreen(onBackClick: () -> Unit) {
    var uiState by remember { mutableStateOf<DetailedInfoUiState?>(null) }

    val view = remember {
        object : DetailedInfoContract.View {
            override fun showContent(content: DetailedInfoUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        DetailedInfoPresenter(view).loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My profile", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = ZoomTopBarInsets,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        uiState?.let { screenState ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    ProfilePageBackground(modifier = Modifier.fillParentMaxWidth()) {
                        Spacer(modifier = Modifier.height(18.dp))
                        ProfileIdentityHeader(
                            name = screenState.user.username,
                            email = null,
                            showCameraBadge = true
                        )
                        Spacer(modifier = Modifier.height(26.dp))
                    }
                }

                itemsIndexed(screenState.sections) { _, section ->
                    ProfilePageBackground(modifier = Modifier.fillParentMaxWidth()) {
                        ProfileSectionLabel(section.title)
                        ProfileCard {
                            section.rows.forEachIndexed { index, row ->
                                val trailingIcon =
                                    if (row.kind == DetailedInfoRowKind.Link) Icons.Default.Link else null
                                ProfileListRow(
                                    title = row.title,
                                    trailingText = row.value,
                                    trailingIcon = trailingIcon,
                                    showChevron = row.showChevron
                                )
                                if (index != section.rows.lastIndex) {
                                    ProfileCardDivider()
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
            }
        }
    }
}
