package com.nxlauncher.data.model

data class ModDependency(
    val name: String,
    val required: Boolean
)

data class ModItem(
    val id: String,
    val name: String,
    val author: String,
    val description: String,
    val source: ModSource,
    val downloads: Long,
    val iconColor: Long,
    val categories: List<String>,
    val supportedVersions: List<String>,
    val supportedLoaders: List<ModLoader>,
    val dependencies: List<ModDependency>
)
