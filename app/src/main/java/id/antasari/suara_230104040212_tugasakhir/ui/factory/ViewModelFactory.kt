package id.antasari.suara_230104040212_tugasakhir.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.LoginViewModel
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.RegistrationViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appwriteService = AppwriteService(context.applicationContext)
        return when {
            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> {
                RegistrationViewModel(appwriteService) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(appwriteService) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
