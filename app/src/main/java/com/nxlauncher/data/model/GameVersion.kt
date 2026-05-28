package com.nxlauncher.data.model

data class GameVersion(
    val id: String,
    val type: VersionType,
    val releaseDate: String,
    val installed: Boolean,
    val availableLoaders: List<ModLoader>
)

data class Account(
    val username: String,
    val online: Boolean
)
