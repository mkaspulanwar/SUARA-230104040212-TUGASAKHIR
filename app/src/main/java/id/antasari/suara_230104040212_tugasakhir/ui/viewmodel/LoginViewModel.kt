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
            val isNik = currentIdentifier.all { it.isDigit() } && currentIdentifier.length == 16

            if (isNik) {
                // TODO: Implement NIK to Email lookup from the database in the future.
                _errorMessage.value = "Login dengan NIK belum didukung. Silakan gunakan email Anda."
                _isLoading.value = false
                return@launch
            }

            try {
                // Assume the identifier is an email
                account.createEmailPasswordSession(
                    email = currentIdentifier,
                    password = _password.value
                )
                _isLoggedIn.value = true
            } catch (e: AppwriteException) {
                _errorMessage.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
