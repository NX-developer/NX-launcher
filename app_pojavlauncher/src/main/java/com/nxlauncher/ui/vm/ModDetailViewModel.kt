package com.nxlauncher.ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxlauncher.data.model.ModDetail
import com.nxlauncher.data.model.ModSource
import com.nxlauncher.data.repository.ModRepository
import kotlinx.coroutines.launch

class ModDetailViewModel : ViewModel() {

    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var detail by mutableStateOf<ModDetail?>(null)
        private set

    private var started = false

    fun start(source: ModSource, id: String) {
        if (started) return
        started = true
        loading = true
        error = null
        viewModelScope.launch {
            runCatching { ModRepository.detail(source, id) }
                .onSuccess {
                    detail = it
                    loading = false
                }
                .onFailure {
                    error = it.message ?: "Could not load mod"
                    loading = false
                }
        }
    }
}
