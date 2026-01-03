package id.antasari.suara_230104040212_tugasakhir.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(private val appwriteService: AppwriteService) : ViewModel() {

    // --- Input Field State ---
    private val _nik = MutableStateFlow("")
    val nik: StateFlow<String> = _nik.asStateFlow()

    private val _nama = MutableStateFlow("")
    val nama: StateFlow<String> = _nama.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    // --- Validation Error State ---
    private val _nikError = MutableStateFlow<String?>(null)
    val nikError: StateFlow<String?> = _nikError.asStateFlow()

    private val _namaError = MutableStateFlow<String?>(null)
    val namaError: StateFlow<String?> = _namaError.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError.asStateFlow()

    // --- UI Operation State ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()


    // --- Event Handlers for UI input changes ---

    fun onNikChange(input: String) {
        _nik.value = input
        if (input.isNotBlank()) {
            _nikError.value = if (input.length == 16 && input.all { it.isDigit() }) {
                null
            } else {
                "NIK harus terdiri dari 16 digit angka."
            }
        } else {
            _nikError.value = "NIK tidak boleh kosong."
        }
    }

    fun onNamaChange(input: String) {
        _nama.value = input
        _namaError.value = if (input.isNotBlank()) null else "Nama tidak boleh kosong."
    }

    fun onEmailChange(input: String) {
        _email.value = input
        if (input.isNotBlank()) {
            _emailError.value = if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                null
            } else {
                "Format email tidak valid."
            }
        } else {
            _emailError.value = "Email tidak boleh kosong."
        }
    }

    fun onPasswordChange(input: String) {
        _password.value = input
        if (input.isNotBlank()) {
            _passwordError.value = if (input.length >= 8) null else "Password minimal 8 karakter."
            // Re-validate confirm password whenever password changes
            if (_confirmPassword.value.isNotEmpty()) {
                onConfirmPasswordChange(_confirmPassword.value)
            }
        } else {
            _passwordError.value = "Password tidak boleh kosong."
        }
    }

    fun onConfirmPasswordChange(input: String) {
        _confirmPassword.value = input
        if (input.isNotBlank()) {
            _confirmPasswordError.value = if (input == _password.value) {
                null
            } else {
                "Password dan konfirmasi password tidak cocok."
            }
        } else {
            _confirmPasswordError.value = "Konfirmasi password tidak boleh kosong."
        }
    }

    private fun validateAllFields(): Boolean {
        // Trigger validation for all fields to show errors if they are empty and user clicks register.
        onNikChange(_nik.value)
        onNamaChange(_nama.value)
        onEmailChange(_email.value)
        onPasswordChange(_password.value)
        onConfirmPasswordChange(_confirmPassword.value)

        // Check if there are any errors
        return _nikError.value == null && _namaError.value == null && _emailError.value == null &&
                _passwordError.value == null && _confirmPasswordError.value == null &&
                _nik.value.isNotBlank() && _nama.value.isNotBlank() && _email.value.isNotBlank() &&
                _password.value.isNotBlank() && _confirmPassword.value.isNotBlank()
    }

    fun onMessagesShown() {
        _errorMessage.value = null
        _successMessage.value = null
    }


    fun register() {
        if (!validateAllFields()) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _isSuccess.value = false

            try {
                // 1. Create user account.
                appwriteService.account.create(
                    userId = _nik.value,
                    email = _email.value,
                    password = _password.value,
                    name = _nama.value
                )

                // 2. Login to create a user session.
                appwriteService.account.createEmailPasswordSession(
                    email = _email.value,
                    password = _password.value
                )

                // 3. Store user details in 'users' collection
                appwriteService.databases.createDocument(
                    databaseId = "6958cd64000647aadc01",
                    collectionId = "users",
                    documentId = _nik.value,
                    data = mapOf(
                        "nama" to _nama.value,
                        "email" to _email.value
                    ),
                    permissions = listOf(
                        Permission.read(Role.user(_nik.value)),
                        Permission.update(Role.user(_nik.value)),
                        Permission.delete(Role.user(_nik.value))
                    )
                )

                _successMessage.value = "Akun berhasil dibuat"
                _isSuccess.value = true
            } catch (e: AppwriteException) {
                _errorMessage.value = "Akun gagal dibuat: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}