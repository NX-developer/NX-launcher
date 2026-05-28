package com.nxlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.nxlauncher.data.model.ModDependency
import com.nxlauncher.data.model.ModDetail
import com.nxlauncher.data.model.ModSource
import com.nxlauncher.ui.components.TagPill
import com.nxlauncher.ui.components.formatDownloads
import com.nxlauncher.ui.theme.NXAmber
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXOutline
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXSurfaceVariant
import com.nxlauncher.ui.theme.NXTextMuted
import com.nxlauncher.ui.theme.NXTextSecondary
import com.nxlauncher.ui.vm.ModDetailViewModel

@Composable
fun ModDetailScreen(source: ModSource, id: String, onBack: () -> Unit) {
    val vm: ModDetailViewModel = viewModel()
    LaunchedEffect(source, id) { vm.start(source, id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NXSurfaceVariant)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(Modifier.height(20.dp))

        when {
            vm.loading -> Box(
                modifier = Modifier.fillMaxWidth().height(320.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = NXGreen) }

            vm.error != null -> Box(
                modifier = Modifier.fillMaxWidth().height(320.dp),
                contentAlignment = Alignment.Center
            ) { Text(vm.error.orEmpty(), color = NXTextMuted) }

            vm.detail != null -> DetailContent(vm.detail!!)
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun DetailContent(mod: ModDetail) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(NXSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (mod.iconUrl != null) {
                AsyncImage(
                    model = mod.iconUrl,
                    contentDescription = mod.name,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = mod.name.take(1).uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    color = NXGreen,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mod.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (mod.author.isNotBlank()) {
                Text(
                    text = "by " + mod.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NXTextSecondary
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(mod.source.accent)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = mod.source.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = mod.source.accent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    Spacer(Modifier.height(18.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox(modifier = Modifier.weight(1f), value = formatDownloads(mod.downloads), label = "Downloads")
        StatBox(modifier = Modifier.weight(1f), value = mod.gameVersions.size.toString(), label = "Versions")
        StatBox(modifier = Modifier.weight(1f), value = mod.loaders.size.toString(), label = "Loaders")
    }

    Spacer(Modifier.height(20.dp))

    Text(
        text = "Description",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = mod.description,
        style = MaterialTheme.typography.bodyLarge,
        color = NXTextSecondary
    )

    Spacer(Modifier.height(22.dp))

    RequirementSection(mod = mod)

    Spacer(Modifier.height(20.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NXAmber.copy(alpha = 0.10f))
            .border(1.dp, NXAmber.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = NXAmber, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Downloading will be available in the next phase.",
                style = MaterialTheme.typography.bodyMedium,
                color = NXAmber
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RequirementSection(mod: ModDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "Requirements",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(16.dp))
        Text("Supported versions", style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
        Spacer(Modifier.height(8.dp))
        if (mod.gameVersions.isEmpty()) {
            Text("Not listed", style = MaterialTheme.typography.bodyMedium, color = NXTextSecondary)
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mod.gameVersions.take(40).forEach { TagPill(it, NXGreen) }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Supported loaders", style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
        Spacer(Modifier.height(8.dp))
        if (mod.loaders.isEmpty()) {
            Text("Not listed", style = MaterialTheme.typography.bodyMedium, color = NXTextSecondary)
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mod.loaders.forEach { TagPill(it.replaceFirstChar { c -> c.uppercase() }, mod.source.accent) }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Dependencies", style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
        Spacer(Modifier.height(8.dp))
        if (mod.dependencies.isEmpty()) {
            Text("No additional dependencies required", style = MaterialTheme.typography.bodyMedium, color = NXTextSecondary)
        } else {
            mod.dependencies.forEach { dependency ->
                DependencyRow(dependency)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DependencyRow(dependency: ModDependency) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NXSurfaceVariant)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (dependency.required) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (dependency.required) NXGreen else NXTextMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = dependency.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (dependency.required) "Required" else "Optional",
            style = MaterialTheme.typography.labelSmall,
            color = if (dependency.required) NXGreen else NXTextMuted,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatBox(modifier: Modifier = Modifier, value: String, label: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(14.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = NXGreen, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
    }
}
