package com.example.photoapp.ui.navigation

private object Route {
    const val FEED = "feed"
}

sealed class Screen(val route: String) {
    object Feed : Screen(Route.FEED)
}