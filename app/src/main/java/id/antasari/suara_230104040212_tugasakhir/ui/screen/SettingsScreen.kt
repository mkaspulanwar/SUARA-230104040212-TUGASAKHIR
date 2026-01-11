package id.antasari.suara_230104040212_tugasakhir.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.navigation.Screen
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.SettingViewModel

// --- VARIABEL WARNA LOKAL ---
private val SettingBlue = Color(0xFF1C74E9)
private val SettingBackground = Color(0xFFF8F9FA)
private val SettingTextBlack = Color(0xFF1F2937)
private val SettingGrayText = Color(0xFF6B7280)
private val SettingRed = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current

    // 1. Inisialisasi ViewModel
    val viewModel: SettingViewModel = viewModel(factory = ViewModelFactory(context))

    // 2. Pantau State Data User
    val userName by viewModel.userName.collectAsState()
    val userNik by viewModel.userNik.collectAsState()
    val isLoggedOut by viewModel.isLoggedOut.collectAsState()

    // 3. Ambil data saat layar dibuka
    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    // 4. Efek Navigasi saat Logout
    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            viewModel.resetState()
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Dialog Konfirmasi Logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color.White,
            title = { Text("Konfirmasi Log Out", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin keluar dari akun ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SettingRed)
                ) {
                    Text("Ya, Log Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = SettingTextBlack)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            // --- TOP BAR (Center Aligned) ---
            CenterAlignedTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = {
                    Text(
                        "Pengaturan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = SettingTextBlack
                    )
                },
                actions = {
                    // Icon Headset (Support) di Kanan
                    IconButton(onClick = {
                        Toast.makeText(context, "Layanan Support", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = "Support",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SettingBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // --- HEADER PROFIL ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(2.dp, SettingBlue.copy(alpha = 0.2f), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Nama User
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SettingTextBlack,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- BADGE NIK ---
                    Surface(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(20.dp),
                        border = null
                    ) {
                        Text(
                            text = "NIK: $userNik",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SettingGrayText,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- MENU GROUP 1: AKUN ---
            SectionTitle(title = "Pengaturan Akun")

            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    SettingsMenuTile(
                        icon = Icons.Outlined.Person,
                        title = "Edit Profil",
                        onClick = {}
                    )
                    SettingsDivider()
                    SettingsMenuTile(
                        icon = Icons.Outlined.Lock,
                        title = "Keamanan & Password",
                        onClick = {}
                    )
                    SettingsDivider()
                    SettingsMenuTile(
                        icon = Icons.Outlined.Palette,
                        title = "Tampilan",
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- MENU GROUP 2: LAINNYA ---
            SectionTitle(title = "Lainnya")

            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    // "Pusat Bantuan" DIHAPUS
                    SettingsMenuTile(
                        icon = Icons.Filled.Info,
                        title = "Tentang Aplikasi",
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL LOG OUT ---
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SettingRed),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = SettingRed
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = SettingGrayText,
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        color = Color(0xFFF1F5F9),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun SettingsMenuTile(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SettingBlue.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SettingBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 15.sp,
            color = SettingTextBlack,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFFD1D5DB),
            modifier = Modifier.size(14.dp)
        )
    }
}