package com.nxlauncher.bridge

import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile

object NXProfiles {
    private const val NX_PROFILE_KEY = "nx"

    @JvmStatic
    fun selectVersion(versionId: String) {
        LauncherProfiles.load()
        val root = LauncherProfiles.mainProfileJson ?: return
        val profiles = root.profiles ?: return
        var profile = profiles[NX_PROFILE_KEY]
        if (profile == null) {
            profile = MinecraftProfile.createTemplate()
            profile.name = "NX"
        }
        profile.lastVersionId = versionId
        profiles[NX_PROFILE_KEY] = profile
        LauncherPreferences.DEFAULT_PREF.edit()
            .putString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, NX_PROFILE_KEY)
            .apply()
        LauncherProfiles.write()
    }

    @JvmStatic
    fun currentVersion(): String? {
        LauncherProfiles.load()
        val key = LauncherPreferences.DEFAULT_PREF.getString(
            LauncherPreferences.PREF_KEY_CURRENT_PROFILE, null
        ) ?: return null
        return LauncherProfiles.mainProfileJson?.profiles?.get(key)?.lastVersionId
    }
}
