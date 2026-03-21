package com.example.zoom.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.model.User
import com.example.zoom.ui.components.ProfileCard
import com.example.zoom.ui.components.ProfileCardDivider
import com.example.zoom.ui.components.ProfileIdentityHeader
import com.example.zoom.ui.components.ProfileListRow
import com.example.zoom.ui.components.ProfilePageBackground
import com.example.zoom.ui.components.ZoomTopBarInsets
import com.example.zoom.ui.theme.ZoomGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onDetailedInfoClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val currentUser = remember { mutableStateOf<User?>(null) }

    val view = remember {
        object : ProfileContract.View {
            override fun showUser(user: User) {
                currentUser.value = user
            }
        }
    }

    LaunchedEffect(Unit) {
        ProfilePresenter(view).loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
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
        currentUser.value?.let { user ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    ProfilePageBackground(modifier = Modifier.fillParentMaxWidth()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        ProfileIdentityHeader(
                            name = user.username,
                            email = user.email,
                            showBasicBadge = true
                        )
                        Spacer(modifier = Modifier.height(22.dp))
                        OfferCard()
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                item {
                    ProfilePageBackground(modifier = Modifier.fillParentMaxWidth()) {
                        val statusRows = listOf(
                            ProfileMenuRow(
                                icon = Icons.Default.CheckCircle,
                                title = "Availability",
                                trailing = "Available",
                                tint = ZoomGreen
                            ),
                            ProfileMenuRow(
                                icon = Icons.Default.EmojiEmotions,
                                title = "Status",
                                trailing = "What is your status?"
                            ),
                            ProfileMenuRow(
                                icon = Icons.Default.Circle,
                                title = "Work location",
                                trailing = "Not set"
                            )
                        )

                        ProfileCard {
                            statusRows.forEachIndexed { index, item ->
                                ProfileListRow(
                                    title = item.title,
                                    leadingIcon = item.icon,
                                    iconTint = item.tint,
                                    trailingText = item.trailing
                                )
                                if (index != statusRows.lastIndex) {
                                    ProfileCardDivider()
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                item {
                    ProfilePageBackground(modifier = Modifier.fillParentMaxWidth()) {
                        val menuRows = listOf(
                            ProfileMenuRow(
                                icon = Icons.Default.PersonOutline,
                                title = "My profile",
                                onClick = onDetailedInfoClick
                            ),
                            ProfileMenuRow(
                                icon = Icons.Default.Settings,
                                title = "Settings",
                                onClick = onSettingsClick
                            )
                        )

                        ProfileCard {
                            menuRows.forEachIndexed { index, item ->
                                ProfileListRow(
                                    title = item.title,
                                    leadingIcon = item.icon,
                                    iconTint = item.tint,
                                    onClick = item.onClick
                                )
                                if (index != menuRows.lastIndex) {
                                    ProfileCardDivider()
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OfferCard() {
    ProfileCard {
        Text(
            text = "FREE TRIAL OFFER",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 16.dp, top = 14.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF7C3AED), Color(0xFF39B980))
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
        Text(
            text = "Unlock longer meetings and AI Companion with a Workplace Pro trial for up to 14 days. Get started",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            color = Color(0xFF465466),
            fontSize = 15.sp,
            lineHeight = 20.sp
        )
    }
}

private data class ProfileMenuRow(
    val icon: ImageVector,
    val title: String,
    val trailing: String? = null,
    val tint: Color = Color(0xFF6F7886),
    val onClick: (() -> Unit)? = null
)
