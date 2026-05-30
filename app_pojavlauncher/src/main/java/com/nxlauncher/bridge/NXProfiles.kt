package com.nxlauncher.bridge

import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile

object NXProfiles {
    private const val NX_PROFILE_NAME = "NX"

    @JvmStatic
    fun selectVersion(versionId: String) {
        LauncherProfiles.load()
        val root = LauncherProfiles.mainProfileJson ?: return
        val profiles = root.profiles ?: return

        var key = profiles.entries.firstOrNull { it.value?.name == NX_PROFILE_NAME }?.key
        val profile = if (key != null) profiles[key]!! else MinecraftProfile.createTemplate().also {
            it.name = NX_PROFILE_NAME
        }
        profile.lastVersionId = versionId

        if (key == null) {
            key = LauncherProfiles.getFreeProfileKey()
        }
        profiles[key] = profile

        LauncherPreferences.DEFAULT_PREF.edit()
            .putString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, key)
            .apply()
        LauncherProfiles.write()
    }

    @JvmStatic
    fun currentVersion(): String? {
        LauncherProfiles.load()
        val key = LauncherPreferences.DEFAULT_PREF.getString(
            LauncherPreferences.PREF_KEY_CURRENT_PROFILE, null
        ) ?: return null
        val v = LauncherProfiles.mainProfileJson?.profiles?.get(key)?.lastVersionId
        if (v == null || v == "Unknown") return null
        return v
    }
}
