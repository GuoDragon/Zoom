package com.example.zoom.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TeamChat : Screen("team_chat")
    object Documents : Screen("documents")
    object Calendar : Screen("calendar")
    object Mail : Screen("mail")
    object Profile : Screen("profile")
}
