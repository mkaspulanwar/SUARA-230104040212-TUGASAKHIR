package id.antasari.suara_230104040212_tugasakhir.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyComment
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyModel
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PolicyViewModel(private val service: AppwriteService) : ViewModel() {

    // ==========================================
    // BAGIAN 1: LIST KEBIJAKAN (Home Screen)
    // ==========================================
    val policies = mutableStateListOf<PolicyModel>()

    /**
     * Mengambil daftar kebijakan DAN melengkapinya dengan data statistik
     * (Total Vote, Persentase Setuju, Jumlah Komentar) untuk Home Screen
     */
    fun fetchPolicies() {
        viewModelScope.launch {
            try {
                // 1. Ambil List Kebijakan Dasar dari Appwrite
                val rawList = service.getAllPolicies()

                // 2. Loop setiap kebijakan untuk mengambil data statistik (Enrichment)
                val enrichedList = rawList.map { policy ->

                    // A. Ambil Data Vote untuk kebijakan ini
                    val votes = service.getVotesByPolicy(policy.id)
                    val totalV = votes.size

                    // Hitung yang setuju
                    val agreeCount = votes.count {
                        val status = it["status"].toString().lowercase()
                        status == "setuju" || status == "agree"
                    }

                    // Hitung Persentase
                    val percent = if (totalV > 0) {
                        ((agreeCount.toDouble() / totalV) * 100).toInt()
                    } else {
                        0
                    }

                    // B. Ambil Data Komentar untuk menghitung jumlahnya
                    val comments = service.getComments(policy.id)
                    val totalC = comments.size

                    // C. Masukkan data statistik ke dalam object PolicyModel
                    policy.apply {
                        this.totalVotes = totalV
                        this.agreePercentage = percent
                        this.totalComments = totalC
                    }
                }

                // 3. Update State List agar UI berubah
                policies.clear()
                policies.addAll(enrichedList)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==========================================
    // BAGIAN 2: DETAIL & VOTING (Detail Screen)
    // ==========================================

    // State untuk menampung Detail Kebijakan
    private val _selectedPolicy = MutableStateFlow<PolicyModel?>(null)
    val selectedPolicy: StateFlow<PolicyModel?> = _selectedPolicy

    // State untuk Loading Indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Data Class Vote Stats
    data class VoteStats(
        val totalVotes: Int = 0,
        val agreeCount: Int = 0,
        val disagreeCount: Int = 0,
        val agreePercentage: Int = 0,
        val disagreePercentage: Int = 0
    )

    // State statistik vote global
    private val _voteStats = MutableStateFlow(VoteStats())
    val voteStats: StateFlow<VoteStats> = _voteStats

    // Menyimpan pilihan user saat ini (null, "setuju", atau "tidak")
    private val _currentUserVote = MutableStateFlow<String?>(null)
    val currentUserVote: StateFlow<String?> = _currentUserVote

    // Variable internal untuk menyimpan ID User yang login (untuk Voting)
    private var currentUserId: String? = null

    // Variable internal untuk menyimpan Nama User (untuk Komentar)
    private var currentUserName: String = "Warga Antasari"

    // ==========================================
    // BAGIAN 3: KOMENTAR
    // ==========================================

    // State List Komentar
    private val _comments = MutableStateFlow<List<PolicyComment>>(emptyList())
    val comments: StateFlow<List<PolicyComment>> = _comments.asStateFlow()

    // State Input Text Komentar
    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText.asStateFlow()

    // State Loading saat mengirim komentar
    private val _isPostingComment = MutableStateFlow(false)
    val isPostingComment: StateFlow<Boolean> = _isPostingComment.asStateFlow()

    // ==========================================
    // LOGIC UTAMA (Detail Screen)
    // ==========================================

    /**
     * Fungsi Utama: Mengambil detail kebijakan, ID user, Nama user, status vote, DAN komentar.
     */
    fun loadPolicyDetail(policyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Ambil User ID (Untuk keperluan pengecekan Vote)
                if (currentUserId == null) {
                    currentUserId = service.getCurrentUserId()
                }

                // 2. Ambil Nama User Asli (Menggunakan Email dari Service)
                currentUserName = service.getCurrentUserName()

                // 3. Ambil Detail Kebijakan
                val policy = service.getPolicyById(policyId)
                _selectedPolicy.value = policy

                // 4. Jika kebijakan ada, load data pendukung (Vote & Komentar)
                if (policy != null) {
                    refreshVoteData(policyId)
                    loadComments(policyId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- LOGIC VOTING ---

    private suspend fun refreshVoteData(policyId: String) {
        val votesList = service.getVotesByPolicy(policyId)
        val total = votesList.size

        if (total > 0) {
            val agreeCount = votesList.count {
                val status = it["status"].toString().lowercase()
                status == "setuju" || status == "agree"
            }
            val disagreeCount = total - agreeCount
            val agreePercent = ((agreeCount.toDouble() / total) * 100).toInt()
            val disagreePercent = ((disagreeCount.toDouble() / total) * 100).toInt()

            _voteStats.value = VoteStats(
                totalVotes = total,
                agreeCount = agreeCount,
                disagreeCount = disagreeCount,
                agreePercentage = agreePercent,
                disagreePercentage = disagreePercent
            )
        } else {
            _voteStats.value = VoteStats()
        }

        if (currentUserId != null) {
            val myVote = votesList.find { it["user_id"] == currentUserId }
            _currentUserVote.value = myVote?.get("status")?.toString()?.lowercase()
        }
    }

    fun castVote(policyId: String, status: String) {
        viewModelScope.launch {
            if (currentUserId == null) return@launch
            _currentUserVote.value = status // Optimistic update
            val success = service.submitVote(policyId, currentUserId!!, status)
            if (success) refreshVoteData(policyId)
        }
    }

    // --- LOGIC KOMENTAR ---

    /**
     * Mengambil daftar komentar dari database
     */
    fun loadComments(policyId: String) {
        viewModelScope.launch {
            val result = service.getComments(policyId)
            _comments.value = result
        }
    }

    /**
     * Mengupdate state text field saat user mengetik
     */
    fun onCommentTextChanged(text: String) {
        _commentText.value = text
    }

    /**
     * Mengirim komentar ke database
     */
    fun sendComment(policyId: String) {
        val message = _commentText.value
        if (message.isBlank()) return

        // Mencegah user mengirim jika belum login atau sedang loading
        viewModelScope.launch {
            _isPostingComment.value = true

            // Gunakan ID dan Nama yang sudah di-fetch di awal (saat loadPolicyDetail)
            val userIdToSend = currentUserId ?: "guest_user"

            // Nama diambil dari variable yang sudah diset oleh getCurrentUserName()
            val userNameToSend = currentUserName

            val success = service.addComment(policyId, message, userIdToSend, userNameToSend)

            if (success) {
                _commentText.value = "" // Reset input
                loadComments(policyId)  // Refresh list komentar agar muncul yang baru
            }
            _isPostingComment.value = false
        }
    }
}