package id.antasari.suara_230104040212_tugasakhir.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingViewModel(private val appwriteService: AppwriteService) : ViewModel() {

    private val account = appwriteService.account

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            try {
                // Hapus sesi 'current' (sesi di HP ini)
                account.deleteSession(sessionId = "current")
                _isLoggedOut.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                // Jika gagal koneksi, paksa logout di UI saja
                _isLoggedOut.value = true
            }
        }
    }

    fun resetState() {
        _isLoggedOut.value = false
    }
}