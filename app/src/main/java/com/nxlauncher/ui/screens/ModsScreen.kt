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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LastPage
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.nxlauncher.data.model.ModLoader
import com.nxlauncher.data.model.ModSource
import com.nxlauncher.data.model.ModSummary
import com.nxlauncher.ui.components.TagPill
import com.nxlauncher.ui.components.formatDownloads
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXOutline
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXSurfaceVariant
import com.nxlauncher.ui.theme.NXTextMuted
import com.nxlauncher.ui.theme.NXTextSecondary
import com.nxlauncher.ui.vm.ModsViewModel

@Composable
fun ModsScreen(onModClick: (ModSource, String) -> Unit) {
    val vm: ModsViewModel = viewModel()
    val listState = rememberLazyListState()

    LaunchedEffect(vm.page, vm.source) {
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

        SourceToggle(source = vm.source, onSelect = { vm.selectSource(it) })
        Spacer(Modifier.height(12.dp))

        SearchField(query = vm.query, onChange = { vm.updateQuery(it) })
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VersionDropdown(
                modifier = Modifier.weight(1f),
                options = vm.versionOptions,
                selected = vm.version,
                onSelect = { vm.selectVersion(it) }
            )
            LoaderDropdown(
                modifier = Modifier.weight(1f),
                selected = vm.loader,
                onSelect = { vm.selectLoader(it) }
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (vm.loading) "Searching..." else vm.totalHits.toString() + " results",
                style = MaterialTheme.typography.labelSmall,
                color = NXTextMuted
            )
            Text(
                text = "Page " + (vm.page + 1) + " / " + vm.pageCount,
                style = MaterialTheme.typography.labelSmall,
                color = NXTextMuted
            )
        }

        Spacer(Modifier.height(10.dp))

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when {
                vm.loading -> CircularProgressIndicator(color = NXGreen)
                vm.keyMissing -> KeyMissingState()
                vm.error != null -> ErrorState(message = vm.error.orEmpty(), onRetry = { vm.search() })
                vm.items.isEmpty() -> Text(
                    text = "No mods match your filters",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NXTextMuted
                )
                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 2.dp, bottom = 12.dp)
                ) {
                    items(vm.items, key = { it.source.name + it.id }) { mod ->
                        ModCard(mod = mod, onClick = { onModClick(mod.source, mod.id) })
                    }
                }
            }
        }

        PaginationBar(
            page = vm.page,
            pageCount = vm.pageCount,
            enabled = !vm.loading,
            onFirst = { vm.firstPage() },
            onPrev = { vm.prevPage() },
            onNext = { vm.nextPage() },
            onLast = { vm.lastPage() }
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
    options: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        DropdownTrigger(label = "Version", value = selected ?: "All", onClick = { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(NXSurfaceVariant)
                .height(360.dp)
        ) {
            DropdownMenuItem(
                text = { Text("All versions", color = NXTextSecondary) },
                onClick = { onSelect(null); expanded = false }
            )
            options.forEach { v ->
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
        DropdownTrigger(label = "Loader", value = selected?.label ?: "All", onClick = { expanded = true })
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
private fun ModCard(mod: ModSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        ModIcon(iconUrl = mod.iconUrl, name = mod.name, size = 52)
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
            if (mod.author.isNotBlank()) {
                Text(
                    text = "by " + mod.author,
                    style = MaterialTheme.typography.labelSmall,
                    color = NXTextMuted
                )
            }
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
private fun ModIcon(iconUrl: String?, name: String, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NXSurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (iconUrl != null) {
            AsyncImage(
                model = iconUrl,
                contentDescription = name,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = NXGreen,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun KeyMissingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Icon(Icons.Filled.Key, contentDescription = null, tint = NXTextMuted, modifier = Modifier.size(34.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            text = "CurseForge API key required",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Add your key to Constants.kt to enable CurseForge. Modrinth works without a key.",
            style = MaterialTheme.typography.bodyMedium,
            color = NXTextSecondary
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, color = NXTextMuted, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(NXGreen.copy(alpha = 0.16f))
                .clickable { onRetry() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = null, tint = NXGreen, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Retry", color = NXGreen, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PaginationBar(
    page: Int,
    pageCount: Int,
    enabled: Boolean,
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
        PageButton(Icons.Filled.FirstPage, enabled = enabled && page > 0, onClick = onFirst)
        PageButton(Icons.Filled.KeyboardArrowLeft, enabled = enabled && page > 0, onClick = onPrev)
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
        PageButton(Icons.Filled.KeyboardArrowRight, enabled = enabled && page < pageCount - 1, onClick = onNext)
        PageButton(Icons.Filled.LastPage, enabled = enabled && page < pageCount - 1, onClick = onLast)
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
