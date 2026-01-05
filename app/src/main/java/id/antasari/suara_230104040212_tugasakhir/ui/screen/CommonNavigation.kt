package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Feed
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Stream
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.ViewStream
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import id.antasari.suara_230104040212_tugasakhir.navigation.Screen
import id.antasari.suara_230104040212_tugasakhir.ui.theme.BlueMain

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Stream,
        NavigationItem.Aspiration,
        NavigationItem.Settings
    )

    // Mengubah background menjadi Putih dan tonalElevation ke 0 agar tidak terlihat abu-abu
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp // Ukuran teks label sedikit diperkecil agar rapi
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BlueMain,
                    selectedTextColor = BlueMain,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent // Menghilangkan background lonjong saat dipilih
                )
            )
        }
    }
}

// Update Icon Outlined yang lebih modern
sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem(
        route = Screen.Home.route,
        icon = Icons.Outlined.Home,
        title = "Home"
    )
    object Stream : NavigationItem(
        route = Screen.Stream.route,
        icon = Icons.Outlined.Article, // Mengganti Article ke Feed agar lebih modern
        title = "Stream"
    )
    object Aspiration : NavigationItem(
        route = Screen.Aspiration.route,
        icon = Icons.Outlined.ChatBubbleOutline, // Mengganti QuestionAnswer ke Forum (lebih cocok untuk aspirasi rakyat)
        title = "Aspirasi"
    )
    object Settings : NavigationItem(
        route = Screen.Settings.route,
        icon = Icons.Outlined.Settings, // Mengganti Settings ke Tune (ikon pengaturan yang lebih clean)
        title = "Settings"
    )
}