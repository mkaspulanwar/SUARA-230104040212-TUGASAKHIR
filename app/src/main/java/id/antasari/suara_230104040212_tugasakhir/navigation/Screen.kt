package id.antasari.suara_230104040212_tugasakhir.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Welcome : Screen("welcome_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Home : Screen("home_screen")
    object Stream : Screen("stream_screen")
    object Aspiration : Screen("aspiration_screen")
    object Settings : Screen("settings_screen")
    object Policy : Screen("policy_screen")
}