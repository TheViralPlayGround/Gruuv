package com.example.gruuv.ui

import LoginScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gruuv.MainActivity
import com.example.gruuv.ui.achievement.AddAchievementScreen
import com.example.gruuv.ui.achievement.EditAchievementScreen
import com.example.gruuv.ui.components.BottomNavigationBar
import com.example.gruuv.ui.dashboard.AchievementViewModel
import com.example.gruuv.ui.dashboard.DashboardScreen
import com.example.gruuv.ui.graph.GraphScreen
import com.example.gruuv.ui.insights.InsightsScreen
import com.example.gruuv.ui.signup.SignUpScreen
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.getViewModel

@Composable
fun GruuvApp(mainActivity: MainActivity) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) "dashboard" else "login"

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("signup") {
                SignUpScreen(navController = navController)
            }
            composable("dashboard") {
                val achievementViewModel: AchievementViewModel = getViewModel()
                DashboardScreen(
                    viewModel = achievementViewModel,
                    navController = navController,
                    mainActivity = mainActivity
                )
            }
            composable("addAchievement") {
                AddAchievementScreen(navController = navController)
            }
            composable("graph") {
                GraphScreen()
            }
            composable(
                "insights/{achievementId}",
                arguments = listOf(navArgument("achievementId") { type = NavType.StringType })
            ) { backStackEntry ->
                val achievementId = backStackEntry.arguments?.getString("achievementId") ?: ""
                InsightsScreen(achievementId)
            }
            composable(
                "editAchievement/{achievementId}",
                arguments = listOf(navArgument("achievementId") { type = NavType.StringType })
            ) { backStackEntry ->
                val achievementId = backStackEntry.arguments?.getString("achievementId") ?: ""
                EditAchievementScreen(
                    navController = navController,
                    achievementId = achievementId
                )
            }
        }
    }
}
