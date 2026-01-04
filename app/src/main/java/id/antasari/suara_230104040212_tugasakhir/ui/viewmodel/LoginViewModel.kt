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

    private val _identifier = MutableStateFlow("")
    val identifier = _identifier.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun onIdentifierChange(identifier: String) {
        _identifier.value = identifier
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val currentIdentifier = _identifier.value

            // Validasi Input Sederhana
            if (currentIdentifier.isBlank() || _password.value.isBlank()) {
                _errorMessage.value = "Email dan Password tidak boleh kosong."
                _isLoading.value = false
                return@launch
            }

            val isNik = currentIdentifier.all { it.isDigit() } && currentIdentifier.length == 16
            if (isNik) {
                _errorMessage.value = "Login dengan NIK belum didukung. Silakan gunakan email Anda."
                _isLoading.value = false
                return@launch
            }

            try {
                // --- PERBAIKAN UTAMA ---
                // Mencoba menghapus sesi lama jika ada.
                // Jika tidak ada sesi aktif, Appwrite akan melempar exception,
                // kita bungkus dalam try-catch agar proses login tetap berlanjut.
                try {
                    account.deleteSession(sessionId = "current")
                } catch (e: Exception) {
                    // Abaikan error jika memang tidak ada sesi aktif
                }

                // Setelah dipastikan bersih, buat sesi baru
                account.createEmailPasswordSession(
                    email = currentIdentifier,
                    password = _password.value
                )
                _isLoggedIn.value = true

            } catch (e: AppwriteException) {
                // Menangani pesan error agar lebih mudah dipahami user
                _errorMessage.value = when (e.code) {
                    401 -> "Email atau Password salah."
                    else -> e.message ?: "Terjadi kesalahan yang tidak diketahui."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Tambahkan fungsi untuk mereset pesan error saat UI sudah menampilkannya
    fun clearError() {
        _errorMessage.value = null
    }
}