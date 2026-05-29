package com.nxlauncher.data.repository

import com.nxlauncher.data.Constants
import com.nxlauncher.data.model.ModDependency
import com.nxlauncher.data.model.ModDetail
import com.nxlauncher.data.model.ModLoader
import com.nxlauncher.data.model.ModSearchResult
import com.nxlauncher.data.model.ModSource
import com.nxlauncher.data.model.ModSummary
import com.nxlauncher.data.net.Http
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class CurseForgeKeyMissing : Exception("CurseForge API key is missing")

object ModRepository {

    suspend fun search(
        source: ModSource,
        query: String,
        version: String?,
        loader: ModLoader?,
        offset: Int,
        limit: Int
    ): ModSearchResult = when (source) {
        ModSource.MODRINTH -> searchModrinth(query, version, loader, offset, limit)
        ModSource.CURSEFORGE -> searchCurseForge(query, version, loader, offset, limit)
    }

    suspend fun detail(source: ModSource, id: String): ModDetail = when (source) {
        ModSource.MODRINTH -> detailModrinth(id)
        ModSource.CURSEFORGE -> detailCurseForge(id)
    }

    private fun enc(value: String): String = URLEncoder.encode(value, "UTF-8")

    private suspend fun searchModrinth(
        query: String,
        version: String?,
        loader: ModLoader?,
        offset: Int,
        limit: Int
    ): ModSearchResult {
        val facets = StringBuilder("[[\"project_type:mod\"]")
        if (!version.isNullOrBlank()) facets.append(",[\"versions:").append(version).append("\"]")
        if (loader != null) facets.append(",[\"categories:").append(loader.apiName).append("\"]")
        facets.append("]")

        val url = StringBuilder(Constants.MODRINTH_API)
            .append("/search?limit=").append(limit)
            .append("&offset=").append(offset)
            .append("&index=relevance")
            .append("&facets=").append(enc(facets.toString()))
        if (query.isNotBlank()) url.append("&query=").append(enc(query))

        val root = JSONObject(Http.get(url.toString()))
        val hits = root.getJSONArray("hits")
        val items = ArrayList<ModSummary>(hits.length())
        for (i in 0 until hits.length()) {
            val obj = hits.getJSONObject(i)
            items.add(
                ModSummary(
                    id = obj.optString("project_id"),
                    slug = obj.optString("slug"),
                    name = obj.optString("title"),
                    author = obj.optString("author"),
                    description = obj.optString("description"),
                    iconUrl = obj.optString("icon_url").ifBlank { null },
                    downloads = obj.optLong("downloads"),
                    source = ModSource.MODRINTH,
                    categories = jsonToList(obj.optJSONArray("display_categories") ?: obj.optJSONArray("categories"))
                )
            )
        }
        return ModSearchResult(items, root.optInt("total_hits"))
    }

    private suspend fun detailModrinth(id: String): ModDetail {
        val project = JSONObject(Http.get(Constants.MODRINTH_API + "/project/" + id))
        val gameVersions = jsonToList(project.optJSONArray("game_versions")).asReversed()
        val loaders = jsonToList(project.optJSONArray("loaders"))

        var author = ""
        runCatching {
            val members = JSONArray(Http.get(Constants.MODRINTH_API + "/project/" + id + "/members"))
            for (i in 0 until members.length()) {
                val member = members.getJSONObject(i)
                if (member.optString("role").equals("Owner", true)) {
                    author = member.optJSONObject("user")?.optString("username").orEmpty()
                    break
                }
            }
            if (author.isBlank() && members.length() > 0) {
                author = members.getJSONObject(0).optJSONObject("user")?.optString("username").orEmpty()
            }
        }

        val dependencies = ArrayList<ModDependency>()
        runCatching {
            val versions = JSONArray(Http.get(Constants.MODRINTH_API + "/project/" + id + "/version"))
            if (versions.length() > 0) {
                val deps = versions.getJSONObject(0).optJSONArray("dependencies")
                if (deps != null && deps.length() > 0) {
                    val idToRequired = LinkedHashMap<String, Boolean>()
                    for (i in 0 until deps.length()) {
                        val dep = deps.getJSONObject(i)
                        val depId = dep.optString("project_id")
                        if (depId.isNotBlank()) {
                            idToRequired[depId] = dep.optString("dependency_type") == "required"
                        }
                    }
                    if (idToRequired.isNotEmpty()) {
                        val idsParam = idToRequired.keys.joinToString(",", "[", "]") { "\"" + it + "\"" }
                        val projects = JSONArray(Http.get(Constants.MODRINTH_API + "/projects?ids=" + enc(idsParam)))
                        for (i in 0 until projects.length()) {
                            val p = projects.getJSONObject(i)
                            val pid = p.optString("id")
                            dependencies.add(ModDependency(p.optString("title"), idToRequired[pid] ?: true))
                        }
                    }
                }
            }
        }

        return ModDetail(
            id = id,
            name = project.optString("title"),
            author = author,
            description = project.optString("description"),
            iconUrl = project.optString("icon_url").ifBlank { null },
            downloads = project.optLong("downloads"),
            source = ModSource.MODRINTH,
            gameVersions = gameVersions,
            loaders = loaders,
            dependencies = dependencies
        )
    }

    private fun curseForgeHeaders(): Map<String, String> {
        if (Constants.CURSEFORGE_API_KEY.isBlank()) throw CurseForgeKeyMissing()
        return mapOf(
            "x-api-key" to Constants.CURSEFORGE_API_KEY,
            "Accept" to "application/json"
        )
    }

    private fun curseForgeLoaderType(loader: ModLoader?): Int? = when (loader) {
        ModLoader.FORGE -> 1
        ModLoader.FABRIC -> 4
        ModLoader.QUILT -> 5
        ModLoader.NEOFORGE -> 6
        null -> null
    }

    private suspend fun searchCurseForge(
        query: String,
        version: String?,
        loader: ModLoader?,
        offset: Int,
        limit: Int
    ): ModSearchResult {
        val headers = curseForgeHeaders()
        val url = StringBuilder(Constants.CURSEFORGE_API)
            .append("/mods/search?gameId=").append(Constants.CURSEFORGE_GAME_ID)
            .append("&classId=").append(Constants.CURSEFORGE_MODS_CLASS_ID)
            .append("&pageSize=").append(limit)
            .append("&index=").append(offset)
            .append("&sortField=2&sortOrder=desc")
        if (query.isNotBlank()) url.append("&searchFilter=").append(enc(query))
        if (!version.isNullOrBlank()) url.append("&gameVersion=").append(enc(version))
        curseForgeLoaderType(loader)?.let { url.append("&modLoaderType=").append(it) }

        val root = JSONObject(Http.get(url.toString(), headers))
        val data = root.getJSONArray("data")
        val items = ArrayList<ModSummary>(data.length())
        for (i in 0 until data.length()) {
            val obj = data.getJSONObject(i)
            items.add(
                ModSummary(
                    id = obj.optInt("id").toString(),
                    slug = obj.optString("slug"),
                    name = obj.optString("name"),
                    author = firstAuthor(obj.optJSONArray("authors")),
                    description = obj.optString("summary"),
                    iconUrl = obj.optJSONObject("logo")?.optString("url")?.ifBlank { null },
                    downloads = obj.optLong("downloadCount"),
                    source = ModSource.CURSEFORGE,
                    categories = categoryNames(obj.optJSONArray("categories"))
                )
            )
        }
        val total = root.optJSONObject("pagination")?.optInt("totalCount") ?: items.size
        return ModSearchResult(items, total)
    }

    private suspend fun detailCurseForge(id: String): ModDetail {
        val headers = curseForgeHeaders()
        val obj = JSONObject(Http.get(Constants.CURSEFORGE_API + "/mods/" + id, headers)).getJSONObject("data")

        val versions = LinkedHashSet<String>()
        val loaderTypes = LinkedHashSet<Int>()
        val indexes = obj.optJSONArray("latestFilesIndexes")
        if (indexes != null) {
            for (i in 0 until indexes.length()) {
                val idx = indexes.getJSONObject(i)
                idx.optString("gameVersion").takeIf { it.isNotBlank() }?.let { versions.add(it) }
                if (idx.has("modLoader")) loaderTypes.add(idx.optInt("modLoader"))
            }
        }

        return ModDetail(
            id = id,
            name = obj.optString("name"),
            author = firstAuthor(obj.optJSONArray("authors")),
            description = obj.optString("summary"),
            iconUrl = obj.optJSONObject("logo")?.optString("url")?.ifBlank { null },
            downloads = obj.optLong("downloadCount"),
            source = ModSource.CURSEFORGE,
            gameVersions = versions.toList(),
            loaders = loaderTypes.mapNotNull { loaderName(it) },
            dependencies = emptyList()
        )
    }

    private fun loaderName(type: Int): String? = when (type) {
        1 -> "Forge"
        4 -> "Fabric"
        5 -> "Quilt"
        6 -> "NeoForge"
        else -> null
    }

    private fun firstAuthor(array: JSONArray?): String {
        if (array == null || array.length() == 0) return ""
        return array.getJSONObject(0).optString("name")
    }

    private fun categoryNames(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val list = ArrayList<String>(array.length())
        for (i in 0 until array.length()) {
            list.add(array.getJSONObject(i).optString("name"))
        }
        return list
    }

    private fun jsonToList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val list = ArrayList<String>(array.length())
        for (i in 0 until array.length()) list.add(array.optString(i))
        return list
    }
}
