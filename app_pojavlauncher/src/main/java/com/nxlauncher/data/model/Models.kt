package com.nxlauncher.data.model

data class ModDependency(
    val name: String,
    val required: Boolean
)

data class ModSummary(
    val id: String,
    val slug: String,
    val name: String,
    val author: String,
    val description: String,
    val iconUrl: String?,
    val downloads: Long,
    val source: ModSource,
    val categories: List<String>
)

data class ModDetail(
    val id: String,
    val name: String,
    val author: String,
    val description: String,
    val iconUrl: String?,
    val downloads: Long,
    val source: ModSource,
    val gameVersions: List<String>,
    val loaders: List<String>,
    val dependencies: List<ModDependency>
)

data class ModSearchResult(
    val items: List<ModSummary>,
    val totalHits: Int
)

data class MinecraftVersion(
    val id: String,
    val type: VersionType,
    val releaseTimeIso: String
)

data class Account(
    val username: String,
    val online: Boolean
)
