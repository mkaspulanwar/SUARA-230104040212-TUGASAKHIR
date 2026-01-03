package id.antasari.suara_230104040212_tugasakhir.data.remote

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

class AppwriteService(context: Context) {
    val client = Client(context)
        .setEndpoint(AppwriteConfig.ENDPOINT)
        .setProject(AppwriteConfig.PROJECT_ID)

    val account = Account(client)
    val databases = Databases(client)
    val storage = Storage(client) // Berguna untuk upload foto aspirasi nanti
}