package com.nxlauncher.data.repository

import com.nxlauncher.data.Constants
import com.nxlauncher.data.model.MinecraftVersion
import com.nxlauncher.data.model.VersionType
import com.nxlauncher.data.net.Http
import org.json.JSONObject

object MojangRepository {

    private var cached: List<MinecraftVersion>? = null
    private var cachedLatestRelease: String? = null

    suspend fun versions(): List<MinecraftVersion> {
        cached?.let { return it }
        val raw = Http.get(Constants.MOJANG_MANIFEST)
        val root = JSONObject(raw)
        cachedLatestRelease = root.optJSONObject("latest")?.optString("release")
        val array = root.getJSONArray("versions")
        val result = ArrayList<MinecraftVersion>(array.length())
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val id = obj.getString("id")
            val type = when (obj.getString("type")) {
                "release" -> VersionType.RELEASE
                "snapshot" -> VersionType.SNAPSHOT
                else -> VersionType.OLD
            }
            val releaseTime = obj.optString("releaseTime")
            result.add(MinecraftVersion(id, type, releaseTime))
        }
        cached = result
        return result
    }

    suspend fun latestRelease(): String? {
        if (cachedLatestRelease == null) {
            versions()
        }
        return cachedLatestRelease
    }

    suspend fun releaseVersionIds(): List<String> {
        return versions().filter { it.type == VersionType.RELEASE }.map { it.id }
    }
}
