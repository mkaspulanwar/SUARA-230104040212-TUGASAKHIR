package id.antasari.suara_230104040212_tugasakhir.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val appwriteService: AppwriteService) : ViewModel() {

    private val account = appwriteService.account

    // --- STATE ---
    private val _identifier = MutableStateFlow("")
    val identifier = _identifier.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // State ini akan dipantau oleh LoginScreen.
    // Jika true -> Navigasi pindah ke Home.
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // --- INPUT HANDLERS ---
    fun onIdentifierChange(identifier: String) {
        _identifier.value = identifier
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    // --- LOGIN LOGIC ---
    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val currentIdentifier = _identifier.value
            val currentPassword = _password.value

            // 1. Validasi Input
            if (currentIdentifier.isBlank() || currentPassword.isBlank()) {
                _errorMessage.value = "Email dan Password tidak boleh kosong."
                _isLoading.value = false
                return@launch
            }

            // Validasi NIK (Sesuai logic Anda)
            val isNik = currentIdentifier.all { it.isDigit() } && currentIdentifier.length == 16
            if (isNik) {
                _errorMessage.value = "Login dengan NIK belum didukung. Silakan gunakan email Anda."
                _isLoading.value = false
                return@launch
            }

            try {
                // 2. SAFETY NET: Hapus sesi lama jika ada (Defensive Programming)
                // Ini mencegah error "Session already active" jika MainViewModel meleset
                try {
                    account.deleteSession(sessionId = "current")
                } catch (e: Exception) {
                    // Abaikan error (artinya memang tidak ada sesi, itu bagus)
                }

                // 3. Buat Sesi Baru
                account.createEmailPasswordSession(
                    email = currentIdentifier,
                    password = currentPassword
                )

                // 4. Sukses -> Trigger Navigasi
                _isLoggedIn.value = true

            } catch (e: AppwriteException) {
                // Handle Error Appwrite
                _errorMessage.value = when (e.code) {
                    401 -> "Email atau Password salah."
                    0 -> "Tidak ada koneksi internet."
                    else -> e.message ?: "Terjadi kesalahan: ${e.code}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Reset error message
    fun clearError() {
        _errorMessage.value = null
    }

    // Opsional: Reset status login setelah navigasi selesai
    // (berguna jika user menekan Back dari Home ke Login lagi)
    fun resetLoginState() {
        _isLoggedIn.value = false
        _identifier.value = ""
        _password.value = ""
    }
}