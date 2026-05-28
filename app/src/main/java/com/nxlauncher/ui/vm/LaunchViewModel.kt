package com.nxlauncher.ui.vm

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxlauncher.data.DeviceInfo
import com.nxlauncher.data.JavaRuntimes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class StepStatus { PENDING, RUNNING, DONE, WARN, BLOCKED }

data class LaunchStep(
    val title: String,
    val detail: String = "",
    val status: StepStatus = StepStatus.PENDING
)

private const val REQUIRED_STORAGE_MB = 500L

class LaunchViewModel : ViewModel() {

    var version by mutableStateOf("")
        private set
    var steps by mutableStateOf(
        listOf(
            LaunchStep("Checking storage"),
            LaunchStep("Resolving Java runtime"),
            LaunchStep("Preparing game directory"),
            LaunchStep("Starting rendering engine")
        )
    )
        private set
    var finished by mutableStateOf(false)
        private set

    private var started = false

    fun start(context: Context, targetVersion: String) {
        if (started) return
        started = true
        version = targetVersion
        viewModelScope.launch {
            val stats = DeviceInfo.read(context)

            set(0, StepStatus.RUNNING)
            delay(500)
            if (stats.freeStorageMb < REQUIRED_STORAGE_MB) {
                set(0, StepStatus.WARN, "Only " + stats.freeStorageReadable() + " free, about 500 MB needed")
            } else {
                set(0, StepStatus.DONE, stats.freeStorageReadable() + " free")
            }

            set(1, StepStatus.RUNNING)
            delay(450)
            val java = JavaRuntimes.recommendedFor(targetVersion)
            set(1, StepStatus.DONE, "Selected " + java + " for " + targetVersion)

            set(2, StepStatus.RUNNING)
            delay(450)
            val prepared = withContext(Dispatchers.IO) {
                runCatching {
                    val dir = File(context.filesDir, ".minecraft/versions/" + targetVersion)
                    dir.mkdirs()
                }.isSuccess
            }
            if (prepared) {
                set(2, StepStatus.DONE, ".minecraft/versions/" + targetVersion)
            } else {
                set(2, StepStatus.WARN, "Could not create the game directory")
            }

            set(3, StepStatus.RUNNING)
            delay(450)
            set(3, StepStatus.BLOCKED, "Native runtime engine is not integrated yet")
            finished = true
        }
    }

    private fun set(index: Int, status: StepStatus, detail: String? = null) {
        steps = steps.mapIndexed { i, step ->
            if (i == index) step.copy(status = status, detail = detail ?: step.detail) else step
        }
    }
}
