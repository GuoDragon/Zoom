package com.example.zoom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zoom.data.DataRepository
import com.example.zoom.navigation.Screen
import com.example.zoom.navigation.ZoomNavGraph
import com.example.zoom.presentation.more.MorePageOverlay
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomTheme

private data class NavItem(val label: String, val icon: ImageVector, val screen: Screen?)

private val navItems = listOf(
    NavItem("Home", Icons.Default.Videocam, Screen.Home),
    NavItem("Team Chat", Icons.Default.Forum, Screen.TeamChat),
    NavItem("Docs", Icons.Default.Description, Screen.Documents),
    NavItem("Calendar", Icons.Default.CalendarMonth, Screen.Calendar),
    NavItem("Mail", Icons.Default.Email, Screen.Mail),
    NavItem("More", Icons.Default.MoreHoriz, null)
)

private val bottomBarRoutes = setOf(
    Screen.Home.route,
    Screen.TeamChat.route,
    Screen.Documents.route,
    Screen.Calendar.route,
    Screen.Mail.route
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataRepository.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            ZoomTheme {
                ZoomApp()
            }
        }
    }
}

@Composable
fun ZoomApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes
    var showMoreOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        if (currentRoute !in bottomBarRoutes) {
            showMoreOverlay = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    navItems.forEach { item ->
                        val isMoreItem = item.screen == null
                        NavigationBarItem(
                            selected = if (isMoreItem) {
                                showMoreOverlay
                            } else {
                                !showMoreOverlay && item.screen?.route == currentRoute
                            },
                            onClick = {
                                if (isMoreItem) {
                                    showMoreOverlay = !showMoreOverlay
                                } else {
                                    showMoreOverlay = false
                                    item.screen?.let { target ->
                                        navController.navigate(target.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ZoomBlue,
                                selectedTextColor = ZoomBlue,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ZoomNavGraph(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                onAvatarClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )

            if (showMoreOverlay && currentRoute in bottomBarRoutes) {
                MorePageOverlay(onDismiss = { showMoreOverlay = false })
            }
        }
    }
}
