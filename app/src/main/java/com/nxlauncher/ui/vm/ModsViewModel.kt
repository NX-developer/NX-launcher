package com.nxlauncher.ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxlauncher.data.model.ModLoader
import com.nxlauncher.data.model.ModSource
import com.nxlauncher.data.model.ModSummary
import com.nxlauncher.data.repository.CurseForgeKeyMissing
import com.nxlauncher.data.repository.ModRepository
import com.nxlauncher.data.repository.MojangRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 8

class ModsViewModel : ViewModel() {

    var source by mutableStateOf(ModSource.MODRINTH)
        private set
    var query by mutableStateOf("")
        private set
    var version by mutableStateOf<String?>(null)
        private set
    var loader by mutableStateOf<ModLoader?>(null)
        private set
    var page by mutableStateOf(0)
        private set

    var items by mutableStateOf<List<ModSummary>>(emptyList())
        private set
    var totalHits by mutableStateOf(0)
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var keyMissing by mutableStateOf(false)
        private set

    var versionOptions by mutableStateOf<List<String>>(emptyList())
        private set

    private var searchJob: Job? = null

    val pageCount: Int
        get() = if (totalHits <= 0) 1 else minOf((totalHits + PAGE_SIZE - 1) / PAGE_SIZE, 625)

    init {
        loadVersionOptions()
        search()
    }

    private fun loadVersionOptions() {
        viewModelScope.launch {
            runCatching { MojangRepository.releaseVersionIds() }
                .onSuccess { versionOptions = it }
        }
    }

    fun setSource(value: ModSource) {
        if (value == source) return
        source = value
        page = 0
        search()
    }

    fun setQuery(value: String) {
        query = value
        page = 0
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(450)
            search()
        }
    }

    fun setVersion(value: String?) {
        version = value
        page = 0
        search()
    }

    fun setLoader(value: ModLoader?) {
        loader = value
        page = 0
        search()
    }

    fun firstPage() { goToPage(0) }
    fun prevPage() { goToPage(page - 1) }
    fun nextPage() { goToPage(page + 1) }
    fun lastPage() { goToPage(pageCount - 1) }

    private fun goToPage(target: Int) {
        val safe = target.coerceIn(0, pageCount - 1)
        if (safe == page) return
        page = safe
        search()
    }

    fun search() {
        loading = true
        error = null
        keyMissing = false
        viewModelScope.launch {
            runCatching {
                ModRepository.search(source, query, version, loader, page * PAGE_SIZE, PAGE_SIZE)
            }.onSuccess {
                items = it.items
                totalHits = it.totalHits
                loading = false
            }.onFailure {
                items = emptyList()
                totalHits = 0
                loading = false
                if (it is CurseForgeKeyMissing) {
                    keyMissing = true
                    error = null
                } else {
                    error = it.message ?: "Could not load mods"
                }
            }
        }
    }
}
