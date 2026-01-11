package id.antasari.suara_230104040212_tugasakhir.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingViewModel(private val appwriteService: AppwriteService) : ViewModel() {

    private val account = appwriteService.account
    // Tambahkan akses ke database untuk mengambil data 'users'
    private val databases = appwriteService.databases

    // --- State Data User ---
    private val _userName = MutableStateFlow("Memuat...")
    val userName = _userName.asStateFlow()

    private val _userNik = MutableStateFlow("...")
    val userNik = _userNik.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail = _userEmail.asStateFlow()

    // --- State Logout ---
    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()

    // --- FUNGSI BARU: Ambil Data Profil ---
    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                // 1. Ambil Akun yang sedang login (Session Aktif)
                val userAccount = account.get()

                // Di sistem Anda, User ID adalah NIK
                _userNik.value = userAccount.id
                _userEmail.value = userAccount.email

                // 2. Ambil Detail Nama dari Database (Collection 'users')
                // Menggunakan Database ID yang sama dengan saat Registrasi ("6958cd64000647aadc01")
                val userDoc = databases.getDocument(
                    databaseId = "6958cd64000647aadc01",
                    collectionId = "users",
                    documentId = userAccount.id
                )

                // Ambil field 'nama' dari dokumen database
                val namaDb = userDoc.data["nama"]?.toString()

                if (!namaDb.isNullOrBlank()) {
                    _userName.value = namaDb
                } else {
                    // Fallback: Jika di database kosong, ambil dari nama akun standar
                    _userName.value = userAccount.name
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Jika error (misal offline), set default atau biarkan data terakhir
                if (_userName.value == "Memuat...") {
                    _userName.value = "Pengguna"
                }
            }
        }
    }

    // --- Fungsi Logout (Tetap Ada) ---
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