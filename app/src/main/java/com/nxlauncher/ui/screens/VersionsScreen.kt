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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nxlauncher.data.model.GameVersion
import com.nxlauncher.data.model.VersionType
import com.nxlauncher.data.sample.SampleData
import com.nxlauncher.ui.components.FilterChip
import com.nxlauncher.ui.components.TagPill
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXOutline
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXSurfaceVariant
import com.nxlauncher.ui.theme.NXTextMuted
import com.nxlauncher.ui.theme.NXTextSecondary

@Composable
fun VersionsScreen() {
    var typeFilter by remember { mutableStateOf<VersionType?>(null) }

    val filtered = remember(typeFilter) {
        SampleData.versions.filter { typeFilter == null || it.type == typeFilter }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Game Versions",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Choose a Minecraft version to install",
            style = MaterialTheme.typography.bodyMedium,
            color = NXTextSecondary
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip("All", typeFilter == null) { typeFilter = null }
            FilterChip("Release", typeFilter == VersionType.RELEASE) { typeFilter = VersionType.RELEASE }
            FilterChip("Snapshot", typeFilter == VersionType.SNAPSHOT) { typeFilter = VersionType.SNAPSHOT }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filtered) { version ->
                VersionRow(version)
            }
        }
    }
}

@Composable
private fun VersionRow(version: GameVersion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NXSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Widgets, contentDescription = null, tint = NXGreen)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = version.id,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.width(8.dp))
                TagPill(version.type.label, if (version.type == VersionType.RELEASE) NXGreen else NXTextMuted)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = version.releaseDate + "  •  " + version.availableLoaders.joinToString(", ") { it.label },
                style = MaterialTheme.typography.labelSmall,
                color = NXTextSecondary
            )
        }
        Spacer(Modifier.width(10.dp))
        if (version.installed) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NXGreen.copy(alpha = 0.16f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = NXGreen, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Installed", style = MaterialTheme.typography.labelSmall, color = NXGreen, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NXSurfaceVariant)
                    .clickable { }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, tint = NXTextSecondary, modifier = Modifier.size(18.dp))
            }
        }
    }
}
