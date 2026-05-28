package com.nxlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nxlauncher.ui.theme.NXBackgroundElevated
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXGreenDark
import com.nxlauncher.ui.theme.NXOutline
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXSurfaceVariant
import com.nxlauncher.ui.theme.NXTextMuted
import com.nxlauncher.ui.theme.NXTextSecondary

@Composable
fun HomeScreen(
    selectedVersion: String,
    onVersionClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPlay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NX",
                    style = MaterialTheme.typography.displaySmall,
                    color = NXGreen,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "LAUNCHER",
                    style = MaterialTheme.typography.labelSmall,
                    color = NXTextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
            AccountChip(username = "Steve", online = false)
        }

        Spacer(Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            NXBackgroundElevated,
                            NXSurface,
                            Color(0xFF0E1A10)
                        )
                    )
                )
                .border(1.dp, NXOutline, RoundedCornerShape(24.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Text(
                        text = "Ready to play",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Minecraft Java Edition",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NXTextSecondary
                    )
                }

                VersionSelector(
                    version = selectedVersion,
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = onVersionClick
                )

                PlayButton(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onClick = onPlay
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Memory,
                label = "Memory",
                value = "2048 MB"
            )
            QuickStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Speed,
                label = "Renderer",
                value = "Zink GL4ES"
            )
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NXSurface)
                .border(1.dp, NXOutline, RoundedCornerShape(16.dp))
                .clickable { onSettingsClick() }
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NXGreen.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Tune, contentDescription = null, tint = NXGreen)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Quick settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Resolution, memory, controls and more",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NXTextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "What's new",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))
        NewsCard(
            title = "Mod browser preview",
            body = "Browse Modrinth and CurseForge directly inside the launcher with version and loader filtering."
        )
        Spacer(Modifier.height(10.dp))
        NewsCard(
            title = "Performance toolkit",
            body = "Lower the render resolution and tune allocated memory to keep frames smooth on weaker devices."
        )
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun AccountChip(username: String, online: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(NXSurfaceVariant)
            .border(1.dp, NXOutline, RoundedCornerShape(50))
            .padding(start = 6.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(NXGreenDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = username,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (online) "Online" else "Offline",
                style = MaterialTheme.typography.labelSmall,
                color = NXTextMuted
            )
        }
    }
}

@Composable
private fun VersionSelector(
    version: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A0D0A).copy(alpha = 0.6f))
            .border(1.dp, NXOutline, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Selected version",
                style = MaterialTheme.typography.labelSmall,
                color = NXTextMuted
            )
            Text(
                text = version,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.width(10.dp))
        Icon(
            Icons.Filled.ExpandMore,
            contentDescription = null,
            tint = NXTextSecondary
        )
    }
}

@Composable
private fun PlayButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(NXGreenDark, NXGreen)
                )
            )
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = Color(0xFF06140A),
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "PLAY",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF06140A),
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = NXGreen, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = NXTextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun NewsCard(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = NXTextSecondary
        )
    }
}
