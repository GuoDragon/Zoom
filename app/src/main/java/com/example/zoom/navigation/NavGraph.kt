package com.example.zoom.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zoom.presentation.calendar.CalendarScreen
import com.example.zoom.presentation.detailedinfo.DetailedInfoScreen
import com.example.zoom.presentation.documents.DocumentsScreen
import com.example.zoom.presentation.home.HomeScreen
import com.example.zoom.presentation.hostmeeting.HostMeetingScreen
import com.example.zoom.presentation.joinmeeting.JoinMeetingScreen
import com.example.zoom.presentation.mail.MailScreen
import com.example.zoom.presentation.profile.ProfileScreen
import com.example.zoom.presentation.schedulemeeting.ScheduleMeetingScreen
import com.example.zoom.presentation.settings.SettingsScreen
import com.example.zoom.presentation.teamchat.TeamChatScreen

@Composable
fun ZoomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAvatarClick = onAvatarClick,
                onHostMeetingClick = { navController.navigate(Screen.HostMeeting.route) },
                onJoinMeetingClick = { navController.navigate(Screen.JoinMeeting.route) },
                onScheduleMeetingClick = { navController.navigate(Screen.ScheduleMeeting.route) }
            )
        }
        composable(Screen.TeamChat.route) { TeamChatScreen(onAvatarClick = onAvatarClick) }
        composable(Screen.Documents.route) { DocumentsScreen(onAvatarClick = onAvatarClick) }
        composable(Screen.Calendar.route) { CalendarScreen(onAvatarClick = onAvatarClick) }
        composable(Screen.Mail.route) { MailScreen(onAvatarClick = onAvatarClick) }
        composable(Screen.HostMeeting.route) {
            HostMeetingScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.JoinMeeting.route) {
            JoinMeetingScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.ScheduleMeeting.route) {
            ScheduleMeetingScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onDetailedInfoClick = { navController.navigate(Screen.DetailedInfo.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.DetailedInfo.route) {
            DetailedInfoScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
