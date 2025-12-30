package id.antasari.suara_230104040212_tugasakhir.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Welcome : Screen("welcome_screen")
    object Home : Screen("home_screen")
}