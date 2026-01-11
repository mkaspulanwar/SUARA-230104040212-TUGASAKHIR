package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.navigation.Screen
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.SettingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) { // Nama fungsi disesuaikan dengan kodingan Anda
    val context = LocalContext.current

    // 1. Inisialisasi ViewModel
    val viewModel: SettingViewModel = viewModel(
        factory = ViewModelFactory(context)
    )

    // 2. Pantau State Logout
    val isLoggedOut by viewModel.isLoggedOut.collectAsState()

    // 3. Efek Navigasi saat Logout Berhasil
    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            // Arahkan ke Login
            navController.navigate(Screen.Login.route) {
                // Hapus semua history backstack agar tidak bisa kembali ke profil
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            // Reset state di ViewModel (opsional)
            viewModel.resetState()
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?") },
            confirmButton = {
                Button(
                    onClick = {
                        // 4. Panggil Fungsi Logout di ViewModel
                        viewModel.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Ya, Keluar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text("Profil & Pengaturan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar Profile
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFFE3F2FD), CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("M. Kaspul Anwar", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("NIK: 3201**********", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Group 1
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsItem(icon = Icons.Default.Edit, text = "Edit Profil", onClick = { /* navController.navigate(Screen.EditProfile.route) */ })
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
                    SettingsItem(icon = Icons.Default.Notifications, text = "Notifikasi")
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
                    SettingsItem(icon = Icons.Default.Security, text = "Keamanan")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Group 2
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsItem(icon = Icons.Default.Help, text = "Pusat Bantuan")
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
                    SettingsItem(icon = Icons.Default.Info, text = "Tentang Aplikasi")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Logout
            Button(
                onClick = { showLogoutDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, text: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}