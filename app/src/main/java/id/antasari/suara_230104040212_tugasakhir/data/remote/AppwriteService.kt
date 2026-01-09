package id.antasari.suara_230104040212_tugasakhir.data.remote

import android.content.Context
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyModel
import io.appwrite.Client
import io.appwrite.ID // PENTING: Import ini dibutuhkan untuk generate ID unik
import io.appwrite.Query
import io.appwrite.extensions.tryJsonCast
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

class AppwriteService(context: Context) {
    private val client = Client(context)
        .setEndpoint(AppwriteConfig.ENDPOINT)
        .setProject(AppwriteConfig.PROJECT_ID)

    val account = Account(client)
    val databases = Databases(client)
    val storage = Storage(client)

    /**
     * Mengambil semua daftar kebijakan dari koleksi 'policies'
     */
    suspend fun getAllPolicies(): List<PolicyModel> {
        return try {
            val response = databases.listDocuments(
                databaseId = AppwriteConfig.DATABASE_ID,
                collectionId = "policies"
            )

            response.documents.map { doc ->
                PolicyModel(
                    id = doc.id,
                    institution = doc.data["institution"].toString(),
                    category = doc.data["category"].toString(),
                    title = doc.data["title"].toString(),
                    content = doc.data["content"].toString(),
                    imageUrl = doc.data["image_url"].toString(),
                    createdAt = doc.createdAt
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Mengambil satu kebijakan secara spesifik berdasarkan ID
     */
    suspend fun getPolicyById(documentId: String): PolicyModel? {
        return try {
            val doc = databases.getDocument(
                databaseId = AppwriteConfig.DATABASE_ID,
                collectionId = "policies",
                documentId = documentId
            )
            PolicyModel(
                id = doc.id,
                institution = doc.data["institution"].toString(),
                category = doc.data["category"].toString(),
                title = doc.data["title"].toString(),
                content = doc.data["content"].toString(),
                imageUrl = doc.data["image_url"].toString(),
                createdAt = doc.createdAt
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Mengambil data vote berdasarkan policy_id
     * Digunakan untuk menghitung total dukungan dan persentase
     */
    suspend fun getVotesByPolicy(policyId: String): List<Map<String, Any>> {
        return try {
            val response = databases.listDocuments(
                databaseId = AppwriteConfig.DATABASE_ID,
                collectionId = "votes",
                queries = listOf(
                    Query.equal("policy_id", policyId)
                )
            )
            response.documents.mapNotNull { it.data.tryJsonCast<Map<String, Any>>() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // --- FITUR INTERAKTIF BARU ---

    /**
     * Mengambil ID User yang sedang login (Session saat ini)
     */
    suspend fun getCurrentUserId(): String? {
        return try {
            val user = account.get()
            user.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Mengecek apakah user sudah pernah vote di kebijakan tertentu.
     * Mengembalikan Document ID jika sudah ada, null jika belum.
     */
    suspend fun getUserVoteDocumentId(policyId: String, userId: String): String? {
        return try {
            val response = databases.listDocuments(
                databaseId = AppwriteConfig.DATABASE_ID,
                collectionId = "votes",
                queries = listOf(
                    Query.equal("policy_id", policyId),
                    Query.equal("user_id", userId)
                )
            )
            if (response.documents.isNotEmpty()) response.documents[0].id else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Mengirimkan Vote (Insert Baru atau Update Lama)
     */
    suspend fun submitVote(policyId: String, userId: String, status: String): Boolean {
        return try {
            // 1. Cek apakah user ini sudah pernah vote sebelumnya?
            val existingDocId = getUserVoteDocumentId(policyId, userId)

            if (existingDocId != null) {
                // UPDATE: Jika sudah ada, update statusnya (misal dari setuju -> tidak)
                databases.updateDocument(
                    databaseId = AppwriteConfig.DATABASE_ID,
                    collectionId = "votes",
                    documentId = existingDocId,
                    data = mapOf("status" to status)
                )
            } else {
                // CREATE: Jika belum ada, buat data vote baru
                databases.createDocument(
                    databaseId = AppwriteConfig.DATABASE_ID,
                    collectionId = "votes",
                    documentId = ID.unique(), // Generate ID unik
                    data = mapOf(
                        "policy_id" to policyId,
                        "user_id" to userId,
                        "status" to status
                    )
                )
            }
            true // Berhasil
        } catch (e: Exception) {
            e.printStackTrace()
            false // Gagal
        }
    }
}