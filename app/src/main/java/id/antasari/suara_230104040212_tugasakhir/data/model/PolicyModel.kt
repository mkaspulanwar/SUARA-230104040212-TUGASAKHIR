package id.antasari.suara_230104040212_tugasakhir.data.model

data class PolicyModel(
    val id: String,
    val institution: String,
    val category: String,
    val title: String,
    val content: String,
    val imageUrl: String,
    val createdAt: String,

    // --- Field Tambahan untuk Home Screen ---
    var agreePercentage: Int = 0, // Persentase setuju (default 0)
    var totalVotes: Int = 0,      // Total vote (untuk label "1.1rb")
    var totalComments: Int = 0    // Total komentar (untuk label "24")
)