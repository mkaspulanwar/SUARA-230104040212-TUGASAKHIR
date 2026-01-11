package id.antasari.suara_230104040212_tugasakhir.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.LoginViewModel
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.MainViewModel
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.PolicyViewModel
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.RegistrationViewModel
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.SettingViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Menginisialisasi AppwriteService satu kali
        val appwriteService = AppwriteService(context.applicationContext)

        return when {
            // 1. PENTING: Untuk Cek Sesi di MainActivity
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(appwriteService) as T
            }

            // 2. Untuk Login
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(appwriteService) as T
            }

            // 3. Untuk Register (Jika ada)
            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> {
                RegistrationViewModel(appwriteService) as T
            }

            // 4. Untuk Home & Detail Kebijakan
            modelClass.isAssignableFrom(PolicyViewModel::class.java) -> {
                PolicyViewModel(appwriteService) as T
            }

            // 5. PENTING: Untuk Logout di SettingScreen
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(appwriteService) as T
            }

            // Jika ViewModel tidak ditemukan
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}