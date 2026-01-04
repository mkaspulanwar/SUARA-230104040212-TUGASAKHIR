package id.antasari.suara_230104040212_tugasakhir.data.remote

import android.content.Context
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyModel
import io.appwrite.Client
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
                    // Mengambil waktu pembuatan dokumen dari sistem Appwrite
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
}