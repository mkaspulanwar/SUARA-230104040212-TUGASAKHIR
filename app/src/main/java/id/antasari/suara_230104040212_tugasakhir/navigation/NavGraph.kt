package id.antasari.suara_230104040212_tugasakhir.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.antasari.suara_230104040212_tugasakhir.ui.screen.AspirationScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.HomeScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.LoginScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.RegistrationScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.SplashScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.StreamScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.WelcomeScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.SettingsScreen

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
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClicked = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                onRegisterClicked = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClicked = {}
            )
        }
        composable(Screen.Register.route) {
            RegistrationScreen(
                onRegisterClicked = {},
                onLoginClicked = { navController.navigate(Screen.Login.route) },
                onTermsClicked = {},
                onPrivacyPolicyClicked = {}
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Stream.route) {
            StreamScreen(navController = navController)
        }
        composable(Screen.Aspiration.route) {
            AspirationScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}