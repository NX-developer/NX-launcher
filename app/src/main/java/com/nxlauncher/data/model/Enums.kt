package com.nxlauncher.data.model

import androidx.compose.ui.graphics.Color
import com.nxlauncher.ui.theme.NXCurseForge
import com.nxlauncher.ui.theme.NXModrinth

enum class ModSource(val label: String, val accent: Color) {
    MODRINTH("Modrinth", NXModrinth),
    CURSEFORGE("CurseForge", NXCurseForge)
}

enum class ModLoader(val label: String) {
    FABRIC("Fabric"),
    FORGE("Forge"),
    QUILT("Quilt"),
    NEOFORGE("NeoForge")
}

enum class VersionType(val label: String) {
    RELEASE("Release"),
    SNAPSHOT("Snapshot")
}
