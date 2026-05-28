package com.nxlauncher.data.sample

import com.nxlauncher.data.model.Account
import com.nxlauncher.data.model.GameVersion
import com.nxlauncher.data.model.ModDependency
import com.nxlauncher.data.model.ModItem
import com.nxlauncher.data.model.ModLoader
import com.nxlauncher.data.model.ModSource
import com.nxlauncher.data.model.VersionType

object SampleData {

    val account = Account(username = "Steve", online = false)

    val versions: List<GameVersion> = listOf(
        GameVersion("1.21.4", VersionType.RELEASE, "2024-12-03", true, listOf(ModLoader.FABRIC, ModLoader.NEOFORGE, ModLoader.QUILT)),
        GameVersion("1.21.1", VersionType.RELEASE, "2024-08-08", true, listOf(ModLoader.FABRIC, ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.QUILT)),
        GameVersion("1.21", VersionType.RELEASE, "2024-06-13", false, listOf(ModLoader.FABRIC, ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.QUILT)),
        GameVersion("1.20.6", VersionType.RELEASE, "2024-04-29", false, listOf(ModLoader.FABRIC, ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.QUILT)),
        GameVersion("1.20.4", VersionType.RELEASE, "2023-12-07", false, listOf(ModLoader.FABRIC, ModLoader.FORGE, ModLoader.QUILT)),
        GameVersion("1.20.1", VersionType.RELEASE, "2023-06-12", true, listOf(ModLoader.FABRIC, ModLoader.FORGE, ModLoader.QUILT)),
        GameVersion("1.19.4", VersionType.RELEASE, "2023-03-14", false, listOf(ModLoader.FABRIC, ModLoader.FORGE, ModLoader.QUILT)),
        GameVersion("1.19.2", VersionType.RELEASE, "2022-08-05", false, listOf(ModLoader.FABRIC, ModLoader.FORGE, ModLoader.QUILT)),
        GameVersion("1.18.2", VersionType.RELEASE, "2022-02-28", false, listOf(ModLoader.FABRIC, ModLoader.FORGE)),
        GameVersion("1.16.5", VersionType.RELEASE, "2021-01-15", false, listOf(ModLoader.FABRIC, ModLoader.FORGE)),
        GameVersion("25w03a", VersionType.SNAPSHOT, "2025-01-15", false, listOf(ModLoader.FABRIC)),
        GameVersion("24w46a", VersionType.SNAPSHOT, "2024-11-13", false, listOf(ModLoader.FABRIC))
    )

    val allVersionStrings: List<String> = versions.map { it.id }

    private val allLoaders = listOf(ModLoader.FABRIC, ModLoader.FORGE, ModLoader.QUILT, ModLoader.NEOFORGE)
    private val fabricQuilt = listOf(ModLoader.FABRIC, ModLoader.QUILT)
    private val fabricOnly = listOf(ModLoader.FABRIC)

    val mods: List<ModItem> = listOf(
        ModItem("sodium", "Sodium", "CaffeineMC", "A modern rendering engine that dramatically improves frame rates and reduces stutter.", ModSource.MODRINTH, 38_400_000, 0xFF2D7DD2, listOf("Optimization"), listOf("1.21.4", "1.21.1", "1.21", "1.20.6", "1.20.4", "1.20.1"), fabricQuilt, emptyList()),
        ModItem("fabric-api", "Fabric API", "modmuss50", "Core hooks and interoperability layer required by most Fabric mods.", ModSource.MODRINTH, 52_100_000, 0xFFB8B8B8, listOf("Library"), listOf("1.21.4", "1.21.1", "1.21", "1.20.6", "1.20.4", "1.20.1", "1.19.4", "1.19.2", "1.18.2", "1.16.5"), fabricQuilt, emptyList()),
        ModItem("lithium", "Lithium", "CaffeineMC", "General purpose optimization mod for game physics, mob AI and chunk ticking.", ModSource.MODRINTH, 19_800_000, 0xFF6FCF97, listOf("Optimization"), listOf("1.21.4", "1.21.1", "1.21", "1.20.6", "1.20.1", "1.19.2"), fabricQuilt, emptyList()),
        ModItem("iris", "Iris Shaders", "coderbot", "Adds support for OptiFine shader packs while staying compatible with Sodium.", ModSource.MODRINTH, 14_200_000, 0xFF9B59B6, listOf("Graphics"), listOf("1.21.4", "1.21.1", "1.21", "1.20.4", "1.20.1"), fabricQuilt, listOf(ModDependency("Sodium", true))),
        ModItem("modmenu", "Mod Menu", "Prospector", "In-game menu to view installed mods and edit their configuration.", ModSource.MODRINTH, 28_600_000, 0xFFE67E22, listOf("Utility"), listOf("1.21.4", "1.21.1", "1.21", "1.20.6", "1.20.1", "1.19.2"), fabricQuilt, listOf(ModDependency("Fabric API", true))),
        ModItem("ferritecore", "FerriteCore", "malte0811", "Reduces the memory usage of Minecraft without sacrificing performance.", ModSource.MODRINTH, 11_300_000, 0xFFC0392B, listOf("Optimization"), listOf("1.21.4", "1.21.1", "1.21", "1.20.1", "1.19.2", "1.18.2"), allLoaders, emptyList()),
        ModItem("entityculling", "Entity Culling", "tr7zw", "Skips rendering of entities and block entities that are not visible.", ModSource.MODRINTH, 13_700_000, 0xFF16A085, listOf("Optimization"), listOf("1.21.4", "1.21.1", "1.21", "1.20.1", "1.19.2"), allLoaders, emptyList()),
        ModItem("appleskin", "AppleSkin", "squeek502", "Adds food and hunger related tooltips and HUD information.", ModSource.MODRINTH, 24_900_000, 0xFFE74C3C, listOf("Utility"), listOf("1.21.4", "1.21.1", "1.21", "1.20.6", "1.20.1", "1.19.2", "1.16.5"), allLoaders, emptyList()),
        ModItem("jei", "Just Enough Items", "mezz", "Item and recipe viewing mod that lets you look up crafting recipes in game.", ModSource.CURSEFORGE, 350_000_000, 0xFFF39C12, listOf("Utility"), listOf("1.21.1", "1.21", "1.20.6", "1.20.1", "1.19.2", "1.18.2", "1.16.5"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.FABRIC), emptyList()),
        ModItem("create", "Create", "simibubi", "Adds rotational contraptions, mechanical machinery and elaborate automation.", ModSource.CURSEFORGE, 96_000_000, 0xFF7F8C8D, listOf("Technology"), listOf("1.21.1", "1.20.1", "1.19.2", "1.18.2"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.FABRIC), emptyList()),
        ModItem("jade", "Jade", "Snownee", "Shows information about the block or entity you are looking at.", ModSource.CURSEFORGE, 88_000_000, 0xFF27AE60, listOf("Utility"), listOf("1.21.4", "1.21.1", "1.20.1", "1.19.2", "1.18.2"), allLoaders, emptyList()),
        ModItem("rei", "Roughly Enough Items", "shedaniel", "A recipe and item viewer mod built for Fabric and Forge.", ModSource.CURSEFORGE, 120_000_000, 0xFF2980B9, listOf("Utility"), listOf("1.21.1", "1.20.1", "1.19.2", "1.18.2", "1.16.5"), allLoaders, listOf(ModDependency("Cloth Config", true))),
        ModItem("cloth-config", "Cloth Config", "shedaniel", "Configuration library used by many popular Fabric and Forge mods.", ModSource.MODRINTH, 41_000_000, 0xFF95A5A6, listOf("Library"), listOf("1.21.4", "1.21.1", "1.21", "1.20.1", "1.19.2", "1.18.2", "1.16.5"), allLoaders, emptyList()),
        ModItem("xaeros-minimap", "Xaero's Minimap", "xaero96", "A high quality minimap that shows the surrounding terrain, mobs and players.", ModSource.CURSEFORGE, 230_000_000, 0xFF34495E, listOf("Map"), listOf("1.21.4", "1.21.1", "1.20.1", "1.19.2", "1.18.2", "1.16.5"), allLoaders, emptyList()),
        ModItem("journeymap", "JourneyMap", "techbrew", "Real-time mapping in game or in a web browser as you explore.", ModSource.CURSEFORGE, 180_000_000, 0xFF8E44AD, listOf("Map"), listOf("1.21.1", "1.20.1", "1.19.2", "1.18.2", "1.16.5"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.FABRIC), emptyList()),
        ModItem("indium", "Indium", "comp500", "Sodium addon providing the rendering interface needed by some mods.", ModSource.MODRINTH, 9_400_000, 0xFF1ABC9C, listOf("Library"), listOf("1.21.4", "1.21.1", "1.21", "1.20.1"), fabricQuilt, listOf(ModDependency("Sodium", true), ModDependency("Fabric API", true))),
        ModItem("dynamic-fps", "Dynamic FPS", "juliand665", "Reduces resource usage while the game is in the background.", ModSource.MODRINTH, 12_100_000, 0xFFD35400, listOf("Optimization"), listOf("1.21.4", "1.21.1", "1.21", "1.20.1", "1.19.2"), fabricQuilt, emptyList()),
        ModItem("starlight", "Starlight", "spottedleaf", "Rewrites the light engine to fix lighting performance and bugs.", ModSource.MODRINTH, 8_900_000, 0xFFF1C40F, listOf("Optimization"), listOf("1.20.1", "1.19.2", "1.18.2", "1.16.5"), fabricQuilt, emptyList()),
        ModItem("continuity", "Continuity", "PepperCode1", "Provides support for connected textures using resource packs.", ModSource.MODRINTH, 7_600_000, 0xFF3498DB, listOf("Graphics"), listOf("1.21.1", "1.20.1", "1.19.2"), fabricQuilt, listOf(ModDependency("Fabric API", true))),
        ModItem("yacl", "YetAnotherConfigLib", "isXander", "A builder based configuration library for Minecraft mods.", ModSource.MODRINTH, 22_300_000, 0xFFBDC3C7, listOf("Library"), listOf("1.21.4", "1.21.1", "1.21", "1.20.1", "1.19.2"), allLoaders, emptyList()),
        ModItem("waystones", "Waystones", "BlayTheNinth", "Place waystones in the world to teleport between them later.", ModSource.CURSEFORGE, 140_000_000, 0xFF16A085, listOf("Adventure"), listOf("1.21.1", "1.20.1", "1.19.2", "1.18.2", "1.16.5"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.FABRIC), emptyList()),
        ModItem("farmers-delight", "Farmer's Delight", "vectorwing", "Expands farming and cooking with new crops, foods and mechanics.", ModSource.CURSEFORGE, 75_000_000, 0xFFE67E22, listOf("Food"), listOf("1.20.1", "1.19.2", "1.18.2", "1.16.5"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.FABRIC), emptyList()),
        ModItem("supplementaries", "Supplementaries", "MehVahdJukaar", "Adds a wide variety of decorative and functional blocks.", ModSource.CURSEFORGE, 60_000_000, 0xFFC0392B, listOf("Decoration"), listOf("1.21.1", "1.20.1", "1.19.2", "1.18.2"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.FABRIC), emptyList()),
        ModItem("immersive-engineering", "Immersive Engineering", "BluSunrize", "Retro futuristic tech mod with multiblock machines and wiring.", ModSource.CURSEFORGE, 90_000_000, 0xFF7F8C8D, listOf("Technology"), listOf("1.20.1", "1.19.2", "1.18.2", "1.16.5"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE), emptyList()),
        ModItem("biomes-o-plenty", "Biomes O' Plenty", "Forstride", "Adds over eighty new biomes filled with new flora and terrain.", ModSource.CURSEFORGE, 160_000_000, 0xFF27AE60, listOf("World Gen"), listOf("1.21.1", "1.20.1", "1.19.2", "1.18.2", "1.16.5"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE), emptyList()),
        ModItem("controlify", "Controlify", "isXander", "Full controller support with on screen prompts and remapping.", ModSource.MODRINTH, 4_300_000, 0xFF9B59B6, listOf("Utility"), listOf("1.21.4", "1.21.1", "1.21", "1.20.1"), fabricQuilt, listOf(ModDependency("YetAnotherConfigLib", true), ModDependency("Fabric API", true))),
        ModItem("zoomify", "Zoomify", "isXander", "A highly configurable zoom mod with smooth transitions.", ModSource.MODRINTH, 6_800_000, 0xFF2ECC71, listOf("Utility"), listOf("1.21.1", "1.20.1", "1.19.2"), fabricQuilt, listOf(ModDependency("YetAnotherConfigLib", true))),
        ModItem("simple-voice-chat", "Simple Voice Chat", "henkelmax", "Proximity based voice chat that works on servers and singleplayer.", ModSource.MODRINTH, 35_500_000, 0xFF1ABC9C, listOf("Social"), listOf("1.21.4", "1.21.1", "1.20.1", "1.19.2", "1.18.2", "1.16.5"), allLoaders, emptyList()),
        ModItem("tweakeroo", "Tweakeroo", "masady", "A collection of client side tweaks and small features.", ModSource.MODRINTH, 5_100_000, 0xFFE74C3C, listOf("Utility"), listOf("1.21.1", "1.20.1", "1.19.2"), fabricOnly, listOf(ModDependency("MaLiLib", true))),
        ModItem("litematica", "Litematica", "masady", "Schematic mod for placing, saving and loading structures.", ModSource.MODRINTH, 9_900_000, 0xFFF39C12, listOf("Utility"), listOf("1.21.1", "1.20.1", "1.19.2"), fabricOnly, listOf(ModDependency("MaLiLib", true))),
        ModItem("malilib", "MaLiLib", "masady", "Library mod required by Tweakeroo, Litematica and MiniHUD.", ModSource.MODRINTH, 10_400_000, 0xFF95A5A6, listOf("Library"), listOf("1.21.1", "1.20.1", "1.19.2", "1.18.2"), fabricOnly, emptyList()),
        ModItem("chunky", "Chunky", "pop4959", "Pre-generates chunks to reduce lag during exploration.", ModSource.MODRINTH, 8_200_000, 0xFF34495E, listOf("Utility"), listOf("1.21.4", "1.21.1", "1.20.1", "1.19.2"), allLoaders, emptyList()),
        ModItem("clumps", "Clumps", "Jaredlll08", "Groups experience orbs together to reduce lag.", ModSource.CURSEFORGE, 145_000_000, 0xFF2ECC71, listOf("Optimization"), listOf("1.21.1", "1.20.1", "1.19.2", "1.18.2", "1.16.5"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE), emptyList()),
        ModItem("oculus", "Oculus", "Asek3", "Shader support for the Forge mod loader based on Iris.", ModSource.CURSEFORGE, 70_000_000, 0xFF8E44AD, listOf("Graphics"), listOf("1.20.1", "1.19.2", "1.18.2"), listOf(ModLoader.FORGE), listOf(ModDependency("Embeddium", true))),
        ModItem("embeddium", "Embeddium", "embeddedt", "A Forge port of Sodium for improved rendering performance.", ModSource.CURSEFORGE, 65_000_000, 0xFF2D7DD2, listOf("Optimization"), listOf("1.21.1", "1.20.1", "1.19.2", "1.18.2"), listOf(ModLoader.FORGE, ModLoader.NEOFORGE), emptyList())
    )
}
