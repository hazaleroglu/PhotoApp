package com.example.photoapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoapp.ui.navigation.Screen
import com.example.photoapp.ui.screens.feed.FeedScreen
import com.example.photoapp.ui.theme.PhotoAppTheme

@Composable
fun PhotoApp() {
    PhotoAppTheme {
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = Screen.Feed.route
            ) {
                baseAppNavGraph(navController)
            }

        }

    }
}

private fun NavGraphBuilder.baseAppNavGraph(
    navController: NavHostController
) {
    composable(Screen.Feed.route) {
        FeedScreen()
    }
}