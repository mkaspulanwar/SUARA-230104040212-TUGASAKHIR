package id.antasari.suara_230104040212_tugasakhir.data.model

data class PolicyComment(
    val id: String,
    val policyId: String,
    val userId: String,
    val userName: String,
    val message: String,
    val createdAt: String,
    val isAdmin: Boolean = false // Default false, nanti bisa dilogika kalau user tertentu admin
)