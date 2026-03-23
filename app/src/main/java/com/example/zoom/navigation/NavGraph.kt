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
import com.example.zoom.presentation.leavemeeting.LeaveMeetingScreen
import com.example.zoom.presentation.leavemeetingdetailed.LeaveMeetingDetailedScreen
import com.example.zoom.presentation.mail.MailScreen
import com.example.zoom.presentation.meetingdetailed.MeetingDetailedScreen
import com.example.zoom.presentation.meetingpreview.MeetingPreviewScreen
import com.example.zoom.presentation.profile.ProfileScreen
import com.example.zoom.presentation.schedulemeeting.ScheduleMeetingScreen
import com.example.zoom.presentation.search.SearchScreen
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
            SearchScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.HostMeeting.route) {
            HostMeetingScreen(
                onBackClick = { navController.popBackStack() },
                onStartMeetingClick = { navController.navigate(Screen.MeetingPreview.route) }
            )
        }
        composable(Screen.MeetingPreview.route) {
            MeetingPreviewScreen(
                onLeaveClick = { navController.navigate(Screen.LeaveMeeting.route) },
                onStartClick = { navController.navigate(Screen.MeetingDetailed.route) }
            )
        }
        composable(Screen.LeaveMeeting.route) {
            LeaveMeetingScreen(
                onLeaveClick = {
                    navController.popBackStack(Screen.HostMeeting.route, false)
                },
                onCancelClick = { navController.popBackStack() }
            )
        }
        composable(Screen.MeetingDetailed.route) {
            MeetingDetailedScreen(
                onBackClick = { navController.popBackStack() },
                onEndClick = { navController.navigate(Screen.LeaveMeetingDetailed.route) }
            )
        }
        composable(Screen.LeaveMeetingDetailed.route) {
            LeaveMeetingDetailedScreen(
                onEndForAllClick = {
                    navController.popBackStack(Screen.HostMeeting.route, false)
                },
                onLeaveClick = {
                    navController.popBackStack(Screen.HostMeeting.route, false)
                },
                onCancelClick = { navController.popBackStack() }
            )
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
