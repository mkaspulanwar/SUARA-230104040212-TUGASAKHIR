package id.antasari.suara_230104040212_tugasakhir.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.Query // Import Query untuk sorting
import io.appwrite.models.InputFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Model Sederhana untuk List (Bisa dipisah file, tapi disini biar praktis)
data class AspirationModel(
    val id: String,
    val title: String,
    val status: String,
    val createdAt: String
)

class AspirationViewModel(private val appwriteService: AppwriteService) : ViewModel() {

    private val account = appwriteService.account
    private val databases = appwriteService.databases
    private val storage = appwriteService.storage

    private val DATABASE_ID = "6958cd64000647aadc01"
    private val COLLECTION_ID = "aspirations"
    private val BUCKET_ID = "695a1a690039dd5795b3"

    // --- State Form (Tetap) ---
    private val _institution = MutableStateFlow("")
    val institution = _institution.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    // --- State UI ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    // --- STATE BARU: LIST ASPIRASI ---
    private val _aspirationList = MutableStateFlow<List<AspirationModel>>(emptyList())
    val aspirationList = _aspirationList.asStateFlow()

    // --- Input Handlers (Tetap) ---
    fun onInstitutionChange(text: String) { _institution.value = text }
    fun onTitleChange(text: String) { _title.value = text }
    fun onDescriptionChange(text: String) { _description.value = text }
    fun onImageSelected(uri: Uri?) { _selectedImageUri.value = uri }

    // --- FUNGSI BARU: AMBIL DATA DARI DATABASE ---
    fun fetchAspirations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ambil dokumen, urutkan dari yang terbaru ($createdAt Descending)
                val response = databases.listDocuments(
                    databaseId = DATABASE_ID,
                    collectionId = COLLECTION_ID,
                    queries = listOf(Query.orderDesc("\$createdAt"))
                )

                // Mapping data dari Document Appwrite ke Model Kita
                val list = response.documents.map { doc ->
                    val data = doc.data
                    AspirationModel(
                        id = doc.id,
                        title = data["title"]?.toString() ?: "Tanpa Judul",
                        status = data["status"]?.toString() ?: "pending",
                        createdAt = doc.createdAt // Mengambil waktu dibuat otomatis
                    )
                }
                _aspirationList.value = list

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- Submit Aspirasi (Update Mapping Key) ---
    fun submitAspiration(context: Context) {
        if (_institution.value.isBlank() || _title.value.isBlank() || _description.value.isBlank()) {
            Toast.makeText(context, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = account.get()

                var imageIdResult: String? = null
                if (_selectedImageUri.value != null) {
                    val file = uriToFile(context, _selectedImageUri.value!!)
                    if (file != null) {
                        val upload = storage.createFile(
                            bucketId = BUCKET_ID,
                            fileId = ID.unique(),
                            file = InputFile.fromFile(file),
                            permissions = listOf(Permission.read(Role.any()))
                        )
                        imageIdResult = upload.id
                    }
                }

                databases.createDocument(
                    databaseId = DATABASE_ID,
                    collectionId = COLLECTION_ID,
                    documentId = ID.unique(),
                    data = mapOf(
                        "user_id" to user.id,
                        "user_name" to user.name,
                        "institution" to _institution.value,
                        "title" to _title.value,
                        "description" to _description.value,
                        "status" to "pending", // Default status
                        "image_id" to (imageIdResult ?: "")
                    ),
                    permissions = listOf(
                        Permission.read(Role.any()),
                        Permission.update(Role.user(user.id)),
                        Permission.delete(Role.user(user.id))
                    )
                )

                _isSuccess.value = true
                Toast.makeText(context, "Aspirasi berhasil dikirim!", Toast.LENGTH_LONG).show()
                resetForm()

                // Refresh list setelah kirim sukses
                fetchAspirations()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Gagal mengirim: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun resetForm() {
        _institution.value = ""
        _title.value = ""
        _description.value = ""
        _selectedImageUri.value = null
        _isSuccess.value = false
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}