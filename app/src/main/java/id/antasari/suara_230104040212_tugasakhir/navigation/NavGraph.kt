package id.antasari.suara_230104040212_tugasakhir.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.antasari.suara_230104040212_tugasakhir.ui.screen.HomeScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.SplashScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.WelcomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}