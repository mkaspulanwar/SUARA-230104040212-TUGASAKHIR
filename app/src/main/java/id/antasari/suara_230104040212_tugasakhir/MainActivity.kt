package id.antasari.suara_230104040212_tugasakhir

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- IMPORT SCREEN & UTILS ---
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import id.antasari.suara_230104040212_tugasakhir.navigation.Screen
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.MainViewModel

// Daftar Screen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.HomeScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.LoginScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.RegistrationScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.PolicyScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.SettingsScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.StreamScreen
import id.antasari.suara_230104040212_tugasakhir.ui.screen.AspirationScreen
import id.antasari.suara_230104040212_tugasakhir.ui.theme.Suara_230104040212_tugasakhirTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisialisasi Service (Context Global)
        val appwriteService = AppwriteService(applicationContext)

        setContent {
            Suara_230104040212_tugasakhirTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SuaraApp()
                }
            }
        }
    }
}

@Composable
fun SuaraApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // 1. Cek Sesi User (Logic Splash Screen)
    val mainViewModel: MainViewModel = viewModel(
        factory = ViewModelFactory(context)
    )
    val isSessionActive by mainViewModel.isSessionActive.collectAsState()

    // 2. Tentukan Tampilan Berdasarkan Status Sesi
    if (isSessionActive == null) {
        // A. Loading / Splash Screen
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // B. Navigasi Utama
        val startDestination = if (isSessionActive == true) {
            Screen.Home.route // Jika sesi ada -> Home
        } else {
            Screen.Login.route // Jika sesi habis -> Login
        }

        // C. Setup NavHost (Animasi dimatikan agar instan)
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {

            // --- 1. RUTE LOGIN ---
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        // Login Sukses -> Masuk ke Home & Hapus Login dari backstack
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onBackClicked = { (context as? Activity)?.finish() },
                    onRegisterClicked = {
                        // Pindah ke halaman Register
                        navController.navigate(Screen.Register.route)
                    },
                    onForgotPasswordClicked = { Toast.makeText(context, "Fitur Lupa Password belum tersedia", Toast.LENGTH_SHORT).show() },
                    onSupportClicked = { Toast.makeText(context, "Hubungi Admin", Toast.LENGTH_SHORT).show() }
                )
            }

            // --- 2. RUTE REGISTER (DIUPDATE) ---
            composable(Screen.Register.route) {
                RegistrationScreen(
                    onRegisterSuccess = {
                        // LOGIC BARU:
                        // 1. Tampilkan pesan sukses
                        Toast.makeText(context, "Akun berhasil dibuat. Silakan Login.", Toast.LENGTH_LONG).show()

                        // 2. Kembali ke halaman Login (bukan ke Home)
                        navController.popBackStack()
                    },
                    onLoginClicked = {
                        // Kembali ke halaman Login
                        navController.popBackStack()
                    },
                    onPrivacyPolicyClicked = { Toast.makeText(context, "Kebijakan Privasi belum tersedia", Toast.LENGTH_SHORT).show() },
                    onTermsClicked = { Toast.makeText(context, "Syarat & Ketentuan belum tersedia", Toast.LENGTH_SHORT).show() }
                )
            }

            // --- 3. RUTE MENU UTAMA ---

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

            // --- 4. RUTE DETAIL KEBIJAKAN ---
            composable("policy_detail/{policyId}") { backStackEntry ->
                val policyId = backStackEntry.arguments?.getString("policyId")
                PolicyScreen(navController = navController, policyId = policyId)
            }

            // --- 5. RUTE NOTIFIKASI ---
            composable(Screen.Notification.route) {
                // Uncomment jika NotificationScreen sudah siap
                // NotificationScreen(navController)
            }
        }
    }
}