package id.antasari.suara_230104040212_tugasakhir.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyModel
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PolicyViewModel(private val service: AppwriteService) : ViewModel() {

    // --- BAGIAN 1: LIST KEBIJAKAN (Tetap sama) ---
    val policies = mutableStateListOf<PolicyModel>()

    fun fetchPolicies() {
        viewModelScope.launch {
            try {
                val list = service.getAllPolicies()
                policies.clear()
                policies.addAll(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- BAGIAN 2: DETAIL & INTERACTIVE VOTING ---

    // State untuk menampung Detail Kebijakan
    private val _selectedPolicy = MutableStateFlow<PolicyModel?>(null)
    val selectedPolicy: StateFlow<PolicyModel?> = _selectedPolicy

    // State untuk Loading Indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // UPDATE: Data Class sekarang menampung detail jumlah suara (Count)
    data class VoteStats(
        val totalVotes: Int = 0,
        val agreeCount: Int = 0,      // Tambahan: Jumlah spesifik yang setuju
        val disagreeCount: Int = 0,   // Tambahan: Jumlah spesifik yang tidak setuju
        val agreePercentage: Int = 0,
        val disagreePercentage: Int = 0
    )

    // State statistik vote global
    private val _voteStats = MutableStateFlow(VoteStats())
    val voteStats: StateFlow<VoteStats> = _voteStats

    // Menyimpan pilihan user saat ini (null, "setuju", atau "tidak")
    private val _currentUserVote = MutableStateFlow<String?>(null)
    val currentUserVote: StateFlow<String?> = _currentUserVote

    // Variable internal untuk menyimpan ID User yang login
    private var currentUserId: String? = null

    /**
     * Fungsi Utama: Mengambil detail kebijakan, ID user, dan status vote
     */
    fun loadPolicyDetail(policyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Ambil User ID dulu
                currentUserId = service.getCurrentUserId()

                // 2. Ambil Detail Kebijakan
                val policy = service.getPolicyById(policyId)
                _selectedPolicy.value = policy

                // 3. Jika kebijakan ada, hitung statistik
                if (policy != null) {
                    refreshVoteData(policyId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fungsi untuk menghitung ulang statistik dan mengecek pilihan user.
     */
    private suspend fun refreshVoteData(policyId: String) {
        // Ambil semua data vote untuk kebijakan ini
        val votesList = service.getVotesByPolicy(policyId)
        val total = votesList.size

        // A. Hitung Statistik Global
        if (total > 0) {
            // Hitung yang setuju
            val agreeCount = votesList.count {
                val status = it["status"].toString().lowercase()
                status == "setuju" || status == "agree"
            }

            // Sisanya adalah tidak setuju
            val disagreeCount = total - agreeCount

            // Hitung Persentase
            val agreePercent = ((agreeCount.toDouble() / total) * 100).toInt()
            val disagreePercent = ((disagreeCount.toDouble() / total) * 100).toInt()

            // Update State dengan data lengkap (Total, Count, & Percentage)
            _voteStats.value = VoteStats(
                totalVotes = total,
                agreeCount = agreeCount,
                disagreeCount = disagreeCount,
                agreePercentage = agreePercent,
                disagreePercentage = disagreePercent
            )
        } else {
            // Jika belum ada vote, semua nol
            _voteStats.value = VoteStats()
        }

        // B. Cek Pilihan User Saat Ini (Agar tombol bisa menyala)
        if (currentUserId != null) {
            val myVote = votesList.find { it["user_id"] == currentUserId }
            if (myVote != null) {
                _currentUserVote.value = myVote["status"].toString().lowercase()
            } else {
                _currentUserVote.value = null
            }
        }
    }

    /**
     * Fungsi yang dipanggil saat user mengklik tombol Vote
     */
    fun castVote(policyId: String, status: String) {
        viewModelScope.launch {
            if (currentUserId == null) return@launch

            // 1. Optimistic Update (Ubah UI instan)
            _currentUserVote.value = status

            // 2. Kirim ke Server
            val success = service.submitVote(policyId, currentUserId!!, status)

            // 3. Refresh data statistik jika sukses
            if (success) {
                refreshVoteData(policyId)
            }
        }
    }
}