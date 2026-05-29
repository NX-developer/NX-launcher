package com.nxlauncher.ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxlauncher.data.model.MinecraftVersion
import com.nxlauncher.data.repository.MojangRepository
import kotlinx.coroutines.launch

class VersionsViewModel : ViewModel() {

    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var versions by mutableStateOf<List<MinecraftVersion>>(emptyList())
        private set

    init {
        load()
    }

    fun load() {
        loading = true
        error = null
        viewModelScope.launch {
            runCatching { MojangRepository.versions() }
                .onSuccess {
                    versions = it
                    loading = false
                }
                .onFailure {
                    error = it.message ?: "Could not load versions"
                    loading = false
                }
        }
    }
}
