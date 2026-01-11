package id.antasari.suara_230104040212_tugasakhir.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val appwriteService: AppwriteService) : ViewModel() {

    private val account = appwriteService.account

    // State Status Sesi:
    // null  = Sedang mengecek (Loading)
    // true  = Ada sesi aktif (Ke Home)
    // false = Tidak ada sesi (Ke Login)
    private val _isSessionActive = MutableStateFlow<Boolean?>(null)
    val isSessionActive = _isSessionActive.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                // Cek apakah user sudah login
                account.get()
                // Jika sukses, berarti sesi masih tersimpan
                _isSessionActive.value = true
            } catch (e: Exception) {
                // Jika error (401), berarti harus login ulang
                _isSessionActive.value = false
            }
        }
    }
}