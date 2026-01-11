package id.antasari.suara_230104040212_tugasakhir.data.remote

import android.content.Context
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyComment
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyModel
import io.appwrite.Client
import io.appwrite.ID
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

    // ==========================================
    // BAGIAN 1: KEBIJAKAN (POLICIES)
    // ==========================================

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

    // ==========================================
    // BAGIAN 2: VOTING SYSTEM
    // ==========================================

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

    suspend fun getCurrentUserId(): String? {
        return try {
            val user = account.get()
            user.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- PERBAIKAN UTAMA DI SINI ---

    /**
     * Mengambil Nama User Asli.
     * Logika: Ambil Email akun login -> Cari di tabel 'users' berdasarkan Email -> Ambil kolom 'nama'
     */
    suspend fun getCurrentUserName(): String {
        return try {
            // 1. Ambil info akun yang sedang login
            val accountObj = account.get()
            val email = accountObj.email

            // 2. Cari di collection 'users' yang email-nya sama
            // PENTING: Pastikan Anda sudah membuat Index untuk 'email' di Appwrite Console
            val response = databases.listDocuments(
                databaseId = AppwriteConfig.DATABASE_ID,
                collectionId = "users",
                queries = listOf(
                    Query.equal("email", email)
                )
            )

            if (response.documents.isNotEmpty()) {
                // 3. Ambil data dari kolom 'nama' (sesuai screenshot database Anda)
                // Jika null, fallback ke nama akun Auth
                response.documents[0].data["nama"]?.toString() ?: accountObj.name
            } else {
                // Jika data tidak ditemukan di tabel users, pakai nama dari Auth
                accountObj.name
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Warga Antasari" // Default jika terjadi error koneksi
        }
    }
    // -------------------------------

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

    suspend fun submitVote(policyId: String, userId: String, status: String): Boolean {
        return try {
            val existingDocId = getUserVoteDocumentId(policyId, userId)

            if (existingDocId != null) {
                databases.updateDocument(
                    databaseId = AppwriteConfig.DATABASE_ID,
                    collectionId = "votes",
                    documentId = existingDocId,
                    data = mapOf("status" to status)
                )
            } else {
                databases.createDocument(
                    databaseId = AppwriteConfig.DATABASE_ID,
                    collectionId = "votes",
                    documentId = ID.unique(),
                    data = mapOf(
                        "policy_id" to policyId,
                        "user_id" to userId,
                        "status" to status
                    )
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ==========================================
    // BAGIAN 3: KOMENTAR
    // ==========================================

    suspend fun getComments(policyId: String): List<PolicyComment> {
        return try {
            val response = databases.listDocuments(
                databaseId = AppwriteConfig.DATABASE_ID,
                collectionId = "policy_comments",
                queries = listOf(
                    Query.equal("policy_id", policyId),
                    Query.orderDesc("\$createdAt")
                )
            )

            response.documents.map { doc ->
                PolicyComment(
                    id = doc.id,
                    policyId = doc.data["policy_id"].toString(),
                    userId = doc.data["user_id"].toString(),
                    userName = doc.data["user_name"].toString(),
                    message = doc.data["message"].toString(),
                    createdAt = doc.createdAt
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addComment(policyId: String, message: String, userId: String, userName: String): Boolean {
        return try {
            databases.createDocument(
                databaseId = AppwriteConfig.DATABASE_ID,
                collectionId = "policy_comments",
                documentId = ID.unique(),
                data = mapOf(
                    "policy_id" to policyId,
                    "message" to message,
                    "user_id" to userId,
                    "user_name" to userName
                )
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}