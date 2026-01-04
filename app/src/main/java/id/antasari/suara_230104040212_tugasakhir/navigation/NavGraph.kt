package id.antasari.suara_230104040212_tugasakhir.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType // Tambahkan import ini
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument // Tambahkan import ini
import id.antasari.suara_230104040212_tugasakhir.ui.screen.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClicked = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClicked = {}
            )
        }

        composable(Screen.Register.route) {
            RegistrationScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onLoginClicked = { navController.popBackStack() },
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

        composable(Screen.AspirationForm.route) {
            AspirationFormScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }

        // --- UPDATE BAGIAN INI ---
        // Menambahkan rute dinamis agar bisa menerima ID kebijakan dari HomeScreen
        composable(
            route = "policy_detail/{policyId}",
            arguments = listOf(navArgument("policyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val policyId = backStackEntry.arguments?.getString("policyId")
            PolicyScreen(navController = navController, policyId = policyId)
        }

        composable(Screen.Notification.route) {
            NotificationScreen(navController = navController)
        }
    }
}