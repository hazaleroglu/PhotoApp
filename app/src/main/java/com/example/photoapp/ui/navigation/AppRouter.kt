package com.example.photoapp.ui.navigation

private object Route {
    const val LOGIN = "login"
    const val FEED = "feed"
}

sealed class Screen(val route: String) {
    object Login : Screen(Route.LOGIN)
    object Feed : Screen(Route.FEED)
}