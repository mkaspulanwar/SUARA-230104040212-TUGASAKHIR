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

    // List yang ditampilkan di UI (bisa berubah karena filter)
    val policies = mutableStateListOf<PolicyModel>()

    // List kategori untuk Filter Section (Dinamis)
    val categoryList = mutableStateListOf("Semua", "Trending")

    // Cache internal: Menyimpan SEMUA data asli dari server
    private var allPoliciesCache = listOf<PolicyModel>()

    /**
     * Mengambil daftar kebijakan, melengkapinya dengan statistik,
     * dan menyiapkan kategori filter.
     */
    fun fetchPolicies() {
        viewModelScope.launch {
            try {
                // 1. Ambil List Kebijakan Dasar dari Appwrite
                val rawList = service.getAllPolicies()

                // 2. Loop setiap kebijakan untuk mengambil data statistik (Enrichment)
                val enrichedList = rawList.map { policy ->
                    // A. Ambil Data Vote
                    val votes = service.getVotesByPolicy(policy.id)
                    val totalV = votes.size
                    val agreeCount = votes.count {
                        val status = it["status"].toString().lowercase()
                        status == "setuju" || status == "agree"
                    }
                    val percent = if (totalV > 0) {
                        ((agreeCount.toDouble() / totalV) * 100).toInt()
                    } else { 0 }

                    // B. Ambil Data Komentar
                    val comments = service.getComments(policy.id)
                    val totalC = comments.size

                    // C. Update object PolicyModel
                    policy.apply {
                        this.totalVotes = totalV
                        this.agreePercentage = percent
                        this.totalComments = totalC
                    }
                }

                // 3. Simpan ke Cache Master & Update UI
                allPoliciesCache = enrichedList
                policies.clear()
                policies.addAll(allPoliciesCache)

                // 4. Update Kategori Filter secara Dinamis
                updateCategories(enrichedList)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Membuat daftar kategori unik dari data yang ada di database
     */
    private fun updateCategories(list: List<PolicyModel>) {
        val dbCategories = list.map { it.category }
            .distinct() // Hapus duplikat
            .sorted()   // Urutkan Abjad

        categoryList.clear()
        categoryList.add("Semua")
        categoryList.add("Trending")
        categoryList.addAll(dbCategories)
    }

    /**
     * LOGIKA FILTERING & SORTING
     * Dipanggil saat user mengklik chip kategori di HomeScreen
     */
    fun filterPolicies(category: String) {
        val filteredList = when (category) {
            "Semua" -> {
                // Tampilkan semua data asli (urutan default/terbaru)
                allPoliciesCache
            }
            "Trending" -> {
                // Urutkan berdasarkan popularitas (Vote + Komen terbanyak)
                allPoliciesCache.sortedByDescending { it.totalVotes + it.totalComments }
            }
            else -> {
                // Filter berdasarkan kesamaan nama kategori (case insensitive)
                allPoliciesCache.filter {
                    it.category.equals(category, ignoreCase = true)
                }
            }
        }

        // Update list yang ditampilkan di UI
        policies.clear()
        policies.addAll(filteredList)
    }

    // ==========================================
    // BAGIAN 2: DETAIL & VOTING (Detail Screen)
    // ==========================================

    private val _selectedPolicy = MutableStateFlow<PolicyModel?>(null)
    val selectedPolicy: StateFlow<PolicyModel?> = _selectedPolicy

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    data class VoteStats(
        val totalVotes: Int = 0,
        val agreeCount: Int = 0,
        val disagreeCount: Int = 0,
        val agreePercentage: Int = 0,
        val disagreePercentage: Int = 0
    )

    private val _voteStats = MutableStateFlow(VoteStats())
    val voteStats: StateFlow<VoteStats> = _voteStats

    private val _currentUserVote = MutableStateFlow<String?>(null)
    val currentUserVote: StateFlow<String?> = _currentUserVote

    private var currentUserId: String? = null
    private var currentUserName: String = "Warga Antasari"

    // ==========================================
    // BAGIAN 3: KOMENTAR
    // ==========================================

    private val _comments = MutableStateFlow<List<PolicyComment>>(emptyList())
    val comments: StateFlow<List<PolicyComment>> = _comments.asStateFlow()

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText.asStateFlow()

    private val _isPostingComment = MutableStateFlow(false)
    val isPostingComment: StateFlow<Boolean> = _isPostingComment.asStateFlow()

    // ==========================================
    // LOGIC UTAMA (Detail Screen)
    // ==========================================

    fun loadPolicyDetail(policyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (currentUserId == null) {
                    currentUserId = service.getCurrentUserId()
                }
                currentUserName = service.getCurrentUserName()

                val policy = service.getPolicyById(policyId)
                _selectedPolicy.value = policy

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
            _currentUserVote.value = status
            val success = service.submitVote(policyId, currentUserId!!, status)
            if (success) refreshVoteData(policyId)
        }
    }

    fun loadComments(policyId: String) {
        viewModelScope.launch {
            val result = service.getComments(policyId)
            _comments.value = result
        }
    }

    fun onCommentTextChanged(text: String) {
        _commentText.value = text
    }

    fun sendComment(policyId: String) {
        val message = _commentText.value
        if (message.isBlank()) return

        viewModelScope.launch {
            _isPostingComment.value = true
            val userIdToSend = currentUserId ?: "guest_user"
            val userNameToSend = currentUserName

            val success = service.addComment(policyId, message, userIdToSend, userNameToSend)

            if (success) {
                _commentText.value = ""
                loadComments(policyId)
            }
            _isPostingComment.value = false
        }
    }
}