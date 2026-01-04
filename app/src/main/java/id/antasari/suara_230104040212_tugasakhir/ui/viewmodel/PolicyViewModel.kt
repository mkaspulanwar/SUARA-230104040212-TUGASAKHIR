package id.antasari.suara_230104040212_tugasakhir.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyModel
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import kotlinx.coroutines.launch

class PolicyViewModel(private val service: AppwriteService) : ViewModel() {
    // State untuk menyimpan daftar kebijakan dari Appwrite
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
}