package com.example.zoom.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.zoom.presentation.calendar.CalendarScreen
import com.example.zoom.presentation.detailedinfo.DetailedInfoScreen
import com.example.zoom.presentation.documents.DocumentsScreen
import com.example.zoom.presentation.home.HomeScreen
import com.example.zoom.presentation.hostmeeting.HostMeetingScreen
import com.example.zoom.presentation.joinmeeting.JoinMeetingScreen
import com.example.zoom.presentation.mail.MailScreen
import com.example.zoom.presentation.profile.ProfileScreen
import com.example.zoom.presentation.schedulemeeting.ScheduleMeetingScreen
import com.example.zoom.presentation.search.SearchScreen
import com.example.zoom.presentation.search.detail.chatdetail.SearchChatDetailScreen
import com.example.zoom.presentation.search.detail.meetingdetail.SearchMeetingDetailScreen
import com.example.zoom.presentation.search.detail.messagedetail.SearchMessageDetailScreen
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
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onHostMeetingClick = { navController.navigate(Screen.HostMeeting.route) },
                onJoinMeetingClick = { navController.navigate(Screen.JoinMeeting.route) },
                onScheduleMeetingClick = { navController.navigate(Screen.ScheduleMeeting.route) }
            )
        }
        composable(Screen.TeamChat.route) {
            TeamChatScreen(
                onAvatarClick = onAvatarClick,
                onSearchClick = { navController.navigate(Screen.Search.route) }
            )
        }
        composable(Screen.Documents.route) {
            DocumentsScreen(
                onAvatarClick = onAvatarClick,
                onSearchClick = { navController.navigate(Screen.Search.route) }
            )
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onAvatarClick = onAvatarClick,
                onSearchClick = { navController.navigate(Screen.Search.route) }
            )
        }
        composable(Screen.Mail.route) {
            MailScreen(
                onAvatarClick = onAvatarClick,
                onSearchClick = { navController.navigate(Screen.Search.route) }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onBackClick = { navController.popBackStack() },
                onMessageDetailClick = { meetingId ->
                    navController.navigate(Screen.SearchMessageDetail.createRoute(meetingId))
                },
                onChatDetailClick = { meetingId ->
                    navController.navigate(Screen.SearchChatDetail.createRoute(meetingId))
                },
                onMeetingDetailClick = { meetingId ->
                    navController.navigate(Screen.SearchMeetingDetail.createRoute(meetingId))
                }
            )
        }
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
        composable(
            route = Screen.SearchMessageDetail.route,
            arguments = listOf(navArgument("meetingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getString("meetingId") ?: ""
            SearchMessageDetailScreen(
                meetingId = meetingId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.SearchChatDetail.route,
            arguments = listOf(navArgument("meetingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getString("meetingId") ?: ""
            SearchChatDetailScreen(
                meetingId = meetingId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.SearchMeetingDetail.route,
            arguments = listOf(navArgument("meetingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getString("meetingId") ?: ""
            SearchMeetingDetailScreen(
                meetingId = meetingId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
