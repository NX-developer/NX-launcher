package com.nxlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LastPage
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nxlauncher.data.model.ModItem
import com.nxlauncher.data.model.ModLoader
import com.nxlauncher.data.model.ModSource
import com.nxlauncher.data.sample.SampleData
import com.nxlauncher.ui.components.TagPill
import com.nxlauncher.ui.components.formatDownloads
import com.nxlauncher.ui.theme.NXBackground
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXOutline
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXSurfaceVariant
import com.nxlauncher.ui.theme.NXTextMuted
import com.nxlauncher.ui.theme.NXTextSecondary

private const val PAGE_SIZE = 6

@Composable
fun ModsScreen(onModClick: (String) -> Unit) {
    var source by remember { mutableStateOf(ModSource.MODRINTH) }
    var query by remember { mutableStateOf("") }
    var versionFilter by remember { mutableStateOf<String?>(null) }
    var loaderFilter by remember { mutableStateOf<ModLoader?>(null) }
    var page by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    val filtered = remember(source, query, versionFilter, loaderFilter) {
        SampleData.mods.filter { mod ->
            mod.source == source &&
                (query.isBlank() || mod.name.contains(query, true) || mod.author.contains(query, true)) &&
                (versionFilter == null || mod.supportedVersions.contains(versionFilter)) &&
                (loaderFilter == null || mod.supportedLoaders.contains(loaderFilter))
        }
    }

    val pageCount = if (filtered.isEmpty()) 1 else (filtered.size + PAGE_SIZE - 1) / PAGE_SIZE
    val safePage = page.coerceIn(0, pageCount - 1)
    val pageItems = filtered.drop(safePage * PAGE_SIZE).take(PAGE_SIZE)

    LaunchedEffect(source, query, versionFilter, loaderFilter) {
        page = 0
    }
    LaunchedEffect(safePage) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Browse Mods",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))

        SourceToggle(source = source, onSelect = { source = it })
        Spacer(Modifier.height(12.dp))

        SearchField(query = query, onChange = { query = it })
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VersionDropdown(
                modifier = Modifier.weight(1f),
                selected = versionFilter,
                onSelect = { versionFilter = it }
            )
            LoaderDropdown(
                modifier = Modifier.weight(1f),
                selected = loaderFilter,
                onSelect = { loaderFilter = it }
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = filtered.size.toString() + " results",
                style = MaterialTheme.typography.labelSmall,
                color = NXTextMuted
            )
            Text(
                text = "Page " + (safePage + 1) + " / " + pageCount,
                style = MaterialTheme.typography.labelSmall,
                color = NXTextMuted
            )
        }

        Spacer(Modifier.height(10.dp))

        if (pageItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No mods match your filters",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NXTextMuted
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(pageItems, key = { it.id }) { mod ->
                    ModCard(mod = mod, onClick = { onModClick(mod.id) })
                }
            }
        }

        PaginationBar(
            page = safePage,
            pageCount = pageCount,
            onFirst = { page = 0 },
            onPrev = { page = (safePage - 1).coerceAtLeast(0) },
            onNext = { page = (safePage + 1).coerceAtMost(pageCount - 1) },
            onLast = { page = pageCount - 1 }
        )
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun SourceToggle(source: ModSource, onSelect: (ModSource) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ModSource.entries.forEach { entry ->
            val selected = entry == source
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) entry.accent.copy(alpha = 0.18f) else Color.Transparent)
                    .clickable { onSelect(entry) }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(entry.accent)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = entry.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) entry.accent else NXTextSecondary,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        placeholder = { Text("Search mods", color = NXTextMuted) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = NXTextSecondary) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = NXSurface,
            unfocusedContainerColor = NXSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = NXGreen,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun VersionDropdown(
    modifier: Modifier = Modifier,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        DropdownTrigger(
            label = "Version",
            value = selected ?: "All",
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(NXSurfaceVariant)
        ) {
            DropdownMenuItem(
                text = { Text("All versions", color = NXTextSecondary) },
                onClick = { onSelect(null); expanded = false }
            )
            SampleData.allVersionStrings.forEach { v ->
                DropdownMenuItem(
                    text = { Text(v, color = MaterialTheme.colorScheme.onBackground) },
                    onClick = { onSelect(v); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun LoaderDropdown(
    modifier: Modifier = Modifier,
    selected: ModLoader?,
    onSelect: (ModLoader?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        DropdownTrigger(
            label = "Loader",
            value = selected?.label ?: "All",
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(NXSurfaceVariant)
        ) {
            DropdownMenuItem(
                text = { Text("All loaders", color = NXTextSecondary) },
                onClick = { onSelect(null); expanded = false }
            )
            ModLoader.entries.forEach { loader ->
                DropdownMenuItem(
                    text = { Text(loader.label, color = MaterialTheme.colorScheme.onBackground) },
                    onClick = { onSelect(loader); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun DropdownTrigger(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
            Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        }
        Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = NXTextSecondary)
    }
}

@Composable
private fun ModCard(mod: ModItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(mod.iconColor)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = mod.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = mod.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(mod.source.accent)
                )
            }
            Text(
                text = "by " + mod.author,
                style = MaterialTheme.typography.labelSmall,
                color = NXTextMuted
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = mod.description,
                style = MaterialTheme.typography.bodyMedium,
                color = NXTextSecondary,
                maxLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, tint = NXGreen, modifier = Modifier.size(14.dp))
                Text(
                    text = formatDownloads(mod.downloads),
                    style = MaterialTheme.typography.labelSmall,
                    color = NXTextSecondary
                )
                Spacer(Modifier.width(2.dp))
                mod.categories.take(1).forEach { TagPill(it, NXTextSecondary) }
            }
        }
    }
}

@Composable
private fun PaginationBar(
    page: Int,
    pageCount: Int,
    onFirst: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLast: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(14.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PageButton(Icons.Filled.FirstPage, enabled = page > 0, onClick = onFirst)
        PageButton(Icons.Filled.KeyboardArrowLeft, enabled = page > 0, onClick = onPrev)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(NXGreen.copy(alpha = 0.16f))
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                text = (page + 1).toString() + " / " + pageCount,
                style = MaterialTheme.typography.labelLarge,
                color = NXGreen,
                fontWeight = FontWeight.Bold
            )
        }
        PageButton(Icons.Filled.KeyboardArrowRight, enabled = page < pageCount - 1, onClick = onNext)
        PageButton(Icons.Filled.LastPage, enabled = page < pageCount - 1, onClick = onLast)
    }
}

@Composable
private fun PageButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val tint = if (enabled) MaterialTheme.colorScheme.onBackground else NXTextMuted.copy(alpha = 0.4f)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(NXSurfaceVariant)
            .clickable(enabled = enabled) { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}
