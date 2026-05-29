package com.nxlauncher.data

import android.app.ActivityManager
import android.content.Context

data class DeviceStats(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val freeStorageMb: Long,
    val totalStorageMb: Long
) {
    fun freeStorageReadable(): String {
        return if (freeStorageMb >= 1024) {
            String.format(java.util.Locale.US, "%.1f GB", freeStorageMb / 1024.0)
        } else {
            freeStorageMb.toString() + " MB"
        }
    }
}

object DeviceInfo {
    fun read(context: Context): DeviceStats {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem / (1024 * 1024)
        val availRam = memoryInfo.availMem / (1024 * 1024)

        val filesDir = context.filesDir
        val freeStorage = filesDir.usableSpace / (1024 * 1024)
        val totalStorage = filesDir.totalSpace / (1024 * 1024)

        return DeviceStats(totalRam, availRam, freeStorage, totalStorage)
    }
}
