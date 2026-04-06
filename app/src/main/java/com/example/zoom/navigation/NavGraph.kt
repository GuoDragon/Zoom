package com.example.zoom.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.zoom.data.DataRepository
import com.example.zoom.presentation.calendar.CalendarScreen
import com.example.zoom.presentation.contacts.ContactsScreen
import com.example.zoom.presentation.detailedinfo.DetailedInfoScreen
import com.example.zoom.presentation.directchat.DirectChatScreen
import com.example.zoom.presentation.documents.DocumentsScreen
import com.example.zoom.presentation.home.HomeScreen
import com.example.zoom.presentation.hostmeeting.HostMeetingScreen
import com.example.zoom.presentation.joinmeeting.JoinMeetingScreen
import com.example.zoom.presentation.leavemeeting.LeaveMeetingScreen
import com.example.zoom.presentation.leavemeetingdetailed.LeaveMeetingDetailedScreen
import com.example.zoom.presentation.mail.MailScreen
import com.example.zoom.presentation.meetingdetailed.MeetingDetailedScreen
import com.example.zoom.presentation.meetinginfodetailed.MeetingInfoDetailedScreen
import com.example.zoom.presentation.meetingpreview.MeetingPreviewScreen
import com.example.zoom.presentation.profile.ProfileScreen
import com.example.zoom.presentation.profileavailability.ProfileAvailabilityScreen
import com.example.zoom.presentation.profiledisplayname.ProfileDisplayNameScreen
import com.example.zoom.presentation.schedulemeetingchat.ScheduleMeetingChatScreen
import com.example.zoom.presentation.schedulemeeting.ScheduleMeetingScreen
import com.example.zoom.presentation.schedulemeetingdetailed.ScheduleMeetingDetailedScreen
import com.example.zoom.presentation.schedulemeetingdetailedcalendar.ScheduleMeetingDetailedInCalendarScreen
import com.example.zoom.presentation.search.SearchScreen
import com.example.zoom.presentation.settings.SettingsScreen
import com.example.zoom.presentation.teamchat.TeamChatScreen
import com.example.zoom.ui.components.MeetingAudioOption
import com.example.zoom.ui.components.MeetingSessionConfig

@Composable
fun ZoomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit,
    onMeetingMinimize: (MeetingSessionConfig, String) -> Unit = { _, _ -> },
    onMeetingDetailedExit: (MeetingExitAction) -> Unit = {}
) {
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAvatarClick = onAvatarClick,
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onHostMeetingClick = { navController.navigate(Screen.HostMeeting.route) },
                onJoinMeetingClick = { navController.navigate(Screen.JoinMeeting.route) },
                onScheduleMeetingClick = { navController.navigate(Screen.ScheduleMeeting.createRoute()) },
                onShareMeetingClick = { config ->
                    navController.navigate(Screen.MeetingDetailed.createRoute(config))
                },
                onScheduledMeetingClick = { meetingId ->
                    navController.navigate(Screen.ScheduleMeetingDetailed.createRoute(meetingId))
                }
            )
        }
        composable(Screen.TeamChat.route) {
            TeamChatScreen(
                onAvatarClick = onAvatarClick,
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onContactsClick = { navController.navigate(Screen.Contacts.route) },
                onDirectChatClick = { userId ->
                    navController.navigate(Screen.DirectChat.createRoute(userId))
                }
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
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onMeetingClick = { meetingId ->
                    navController.navigate(Screen.ScheduleMeetingDetailedInCalendar.createRoute(meetingId))
                }
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
                onContactClick = { userId ->
                    navController.navigate(Screen.DirectChat.createRoute(userId))
                }
            )
        }
        composable(Screen.HostMeeting.route) {
            HostMeetingScreen(
                onBackClick = { navController.popBackStack() },
                onStartMeetingClick = { meetingId, config ->
                    navController.navigate(Screen.MeetingPreview.createRoute(meetingId, config))
                }
            )
        }
        composable(
            route = Screen.MeetingPreview.routePattern,
            arguments = listOf(
                navArgument(Screen.MeetingPreview.meetingIdArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument(Screen.MeetingPreview.microphoneArg) {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument(Screen.MeetingPreview.cameraArg) {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument(Screen.MeetingPreview.audioArg) {
                    type = NavType.StringType
                    defaultValue = MeetingAudioOption.WifiOrCellular.routeValue
                }
            )
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments
                ?.getString(Screen.MeetingPreview.meetingIdArg)
                ?.takeIf { it.isNotBlank() }
            val previewConfig = MeetingSessionConfig(
                microphoneOn = backStackEntry.arguments?.getBoolean(Screen.MeetingPreview.microphoneArg) ?: false,
                cameraOn = backStackEntry.arguments?.getBoolean(Screen.MeetingPreview.cameraArg) ?: false,
                audioOption = MeetingAudioOption.fromRouteValue(
                    backStackEntry.arguments?.getString(Screen.MeetingPreview.audioArg)
                )
            )
            MeetingPreviewScreen(
                meetingId = meetingId,
                initialConfig = previewConfig,
                onLeaveClick = { navController.navigate(Screen.LeaveMeeting.route) },
                onStartClick = { config ->
                    navController.navigate(Screen.MeetingDetailed.createRoute(config))
                }
            )
        }
        composable(Screen.LeaveMeeting.route) {
            LeaveMeetingScreen(
                onLeaveClick = {
                    val leaveDismissed = navController.popBackStack()
                    val previewDismissed = navController.popBackStack()
                    if (!leaveDismissed || !previewDismissed) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                onCancelClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.MeetingDetailed.routePattern,
            arguments = listOf(
                navArgument(Screen.MeetingDetailed.microphoneArg) {
                    type = NavType.BoolType
                },
                navArgument(Screen.MeetingDetailed.cameraArg) {
                    type = NavType.BoolType
                },
                navArgument(Screen.MeetingDetailed.audioArg) {
                    type = NavType.StringType
                },
                navArgument(Screen.MeetingDetailed.screenSharingArg) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            val config = MeetingSessionConfig(
                microphoneOn = args?.getBoolean(Screen.MeetingDetailed.microphoneArg) ?: false,
                cameraOn = args?.getBoolean(Screen.MeetingDetailed.cameraArg) ?: false,
                audioOption = MeetingAudioOption.fromRouteValue(
                    args?.getString(Screen.MeetingDetailed.audioArg)
                ),
                screenSharingEnabled = args?.getBoolean(Screen.MeetingDetailed.screenSharingArg) ?: false
            )
            MeetingDetailedScreen(
                initialConfig = config,
                onMinimizeClick = {
                    onMeetingMinimize(config, "JW")
                    navController.popBackStack(Screen.Home.route, false)
                },
                onEndClick = { navController.navigate(Screen.LeaveMeetingDetailed.route) },
                onInfoClick = { navController.navigate(Screen.MeetingInfoDetailed.route) }
            )
        }
        composable(Screen.LeaveMeetingDetailed.route) {
            LeaveMeetingDetailedScreen(
                onEndForAllClick = {
                    DataRepository.stopCurrentScreenShareSessionIfActive()
                    DataRepository.recordCurrentMeetingExited(MeetingExitAction.END_FOR_ALL.name)
                    onMeetingDetailedExit(MeetingExitAction.END_FOR_ALL)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLeaveClick = {
                    DataRepository.stopCurrentScreenShareSessionIfActive()
                    DataRepository.recordCurrentMeetingExited(MeetingExitAction.LEAVE_SELF.name)
                    onMeetingDetailedExit(MeetingExitAction.LEAVE_SELF)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onCancelClick = { navController.popBackStack() }
            )
        }
        composable(Screen.JoinMeeting.route) {
            JoinMeetingScreen(
                onBackClick = { navController.popBackStack() },
                onJoinMeetingClick = { meetingId, config ->
                    navController.navigate(Screen.MeetingPreview.createRoute(meetingId, config))
                }
            )
        }
        composable(
            route = Screen.ScheduleMeeting.routePattern,
            arguments = listOf(
                navArgument(Screen.ScheduleMeeting.meetingIdArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val editingMeetingId = backStackEntry.arguments
                ?.getString(Screen.ScheduleMeeting.meetingIdArg)
                ?.takeIf { it.isNotBlank() }
            ScheduleMeetingScreen(
                onBackClick = { navController.popBackStack() },
                editingMeetingId = editingMeetingId,
                onSaveSuccess = { meetingId ->
                    if (editingMeetingId.isNullOrBlank()) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Screen.ScheduleMeetingDetailed.createRoute(meetingId)) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.ScheduleMeetingDetailed.routePattern,
            arguments = listOf(
                navArgument(Screen.ScheduleMeetingDetailed.meetingIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments
                ?.getString(Screen.ScheduleMeetingDetailed.meetingIdArg)
                ?: return@composable
            ScheduleMeetingDetailedScreen(
                meetingId = meetingId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { selectedMeetingId ->
                    navController.navigate(Screen.ScheduleMeeting.createRoute(selectedMeetingId))
                },
                onStartClick = { selectedMeetingId ->
                    navController.navigate(
                        Screen.MeetingPreview.createRoute(
                            selectedMeetingId,
                            MeetingSessionConfig(
                                microphoneOn = false,
                                cameraOn = false,
                                audioOption = MeetingAudioOption.WifiOrCellular
                            )
                        )
                    )
                },
                onChatClick = { selectedMeetingId ->
                    navController.navigate(Screen.ScheduleMeetingChat.createRoute(selectedMeetingId))
                },
                onMeetingCancelled = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.ScheduleMeetingDetailedInCalendar.routePattern,
            arguments = listOf(
                navArgument(Screen.ScheduleMeetingDetailedInCalendar.meetingIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments
                ?.getString(Screen.ScheduleMeetingDetailedInCalendar.meetingIdArg)
                ?: return@composable
            ScheduleMeetingDetailedInCalendarScreen(
                meetingId = meetingId,
                onBackClick = { navController.popBackStack() },
                onStartClick = { selectedMeetingId ->
                    navController.navigate(
                        Screen.MeetingPreview.createRoute(
                            selectedMeetingId,
                            MeetingSessionConfig(
                                microphoneOn = false,
                                cameraOn = false,
                                audioOption = MeetingAudioOption.WifiOrCellular
                            )
                        )
                    )
                }
            )
        }
        composable(
            route = Screen.ScheduleMeetingChat.routePattern,
            arguments = listOf(
                navArgument(Screen.ScheduleMeetingChat.meetingIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments
                ?.getString(Screen.ScheduleMeetingChat.meetingIdArg)
                ?: return@composable
            ScheduleMeetingChatScreen(
                meetingId = meetingId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onDetailedInfoClick = { navController.navigate(Screen.DetailedInfo.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onAvailabilityClick = { navController.navigate(Screen.ProfileAvailability.route) }
            )
        }
        composable(Screen.DetailedInfo.route) {
            DetailedInfoScreen(
                onBackClick = { navController.popBackStack() },
                onDisplayNameClick = { navController.navigate(Screen.ProfileDisplayName.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.MeetingInfoDetailed.route) {
            MeetingInfoDetailedScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.Contacts.route) {
            ContactsScreen(
                onBackClick = { navController.popBackStack() },
                onContactClick = { userId ->
                    navController.navigate(Screen.DirectChat.createRoute(userId))
                }
            )
        }
        composable(
            route = Screen.DirectChat.routePattern,
            arguments = listOf(
                navArgument(Screen.DirectChat.userIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(Screen.DirectChat.userIdArg) ?: return@composable
            DirectChatScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.ProfileAvailability.route) {
            ProfileAvailabilityScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.ProfileDisplayName.route) {
            ProfileDisplayNameScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
