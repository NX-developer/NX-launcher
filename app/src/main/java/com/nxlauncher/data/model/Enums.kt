package com.nxlauncher.data.model

import androidx.compose.ui.graphics.Color
import com.nxlauncher.ui.theme.NXCurseForge
import com.nxlauncher.ui.theme.NXModrinth

enum class ModSource(val label: String, val accent: Color) {
    MODRINTH("Modrinth", NXModrinth),
    CURSEFORGE("CurseForge", NXCurseForge)
}

enum class ModLoader(val label: String, val apiName: String) {
    FABRIC("Fabric", "fabric"),
    FORGE("Forge", "forge"),
    QUILT("Quilt", "quilt"),
    NEOFORGE("NeoForge", "neoforge")
}

enum class VersionType(val label: String) {
    RELEASE("Release"),
    SNAPSHOT("Snapshot"),
    OLD("Old")
}
