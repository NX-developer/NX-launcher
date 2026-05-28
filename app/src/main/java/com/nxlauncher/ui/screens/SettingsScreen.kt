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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nxlauncher.ui.components.FilterChip
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXGreenDark
import com.nxlauncher.ui.theme.NXOutline
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXSurfaceVariant
import com.nxlauncher.ui.theme.NXTextMuted
import com.nxlauncher.ui.theme.NXTextSecondary
import kotlin.math.roundToInt

@Composable
fun SettingsScreen() {
    var resolution by remember { mutableFloatStateOf(100f) }
    var ram by remember { mutableIntStateOf(2048) }
    var renderer by remember { mutableStateOf("Zink") }
    var runtime by remember { mutableStateOf("Java 17") }
    var touchControls by remember { mutableStateOf(true) }
    var gamepad by remember { mutableStateOf(false) }
    var gesture by remember { mutableStateOf(true) }
    var keepScreenOn by remember { mutableStateOf(true) }
    var highPriority by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(20.dp))
        SettingsGroup(icon = Icons.Filled.Tune, title = "Performance") {
            SliderSetting(
                title = "Resolution scale",
                description = "Lower the resolution to gain more FPS on weaker devices",
                value = resolution,
                valueLabel = resolution.roundToInt().toString() + "%",
                range = 25f..100f,
                steps = 14,
                onChange = { resolution = it }
            )
            DividerLine()
            SliderSetting(
                title = "Allocated memory",
                description = "Amount of RAM reserved for the game",
                value = ram.toFloat(),
                valueLabel = ram.toString() + " MB",
                range = 512f..6144f,
                steps = 10,
                onChange = { ram = (it / 256).roundToInt() * 256 }
            )
            DividerLine()
            ChipSetting(
                title = "Renderer",
                options = listOf("Zink", "GL4ES", "VirGL", "Holy GL4ES"),
                selected = renderer,
                onSelect = { renderer = it }
            )
        }

        Spacer(Modifier.height(16.dp))
        SettingsGroup(icon = Icons.Filled.Memory, title = "Runtime") {
            ChipSetting(
                title = "Java runtime",
                options = listOf("Java 8", "Java 17", "Java 21"),
                selected = runtime,
                onSelect = { runtime = it }
            )
            DividerLine()
            JvmArgsRow()
        }

        Spacer(Modifier.height(16.dp))
        SettingsGroup(icon = Icons.Filled.SportsEsports, title = "Controls") {
            ToggleSetting(
                title = "Touch controls",
                description = "On screen buttons and joystick",
                checked = touchControls,
                onChange = { touchControls = it }
            )
            DividerLine()
            ToggleSetting(
                title = "Gamepad support",
                description = "Map a physical controller",
                checked = gamepad,
                onChange = { gamepad = it }
            )
            DividerLine()
            ToggleSetting(
                title = "Gesture controls",
                description = "Swipe and pinch interactions",
                checked = gesture,
                onChange = { gesture = it }
            )
        }

        Spacer(Modifier.height(16.dp))
        SettingsGroup(icon = Icons.Filled.Monitor, title = "System") {
            ToggleSetting(
                title = "Keep screen on",
                description = "Prevent the display from sleeping while playing",
                checked = keepScreenOn,
                onChange = { keepScreenOn = it }
            )
            DividerLine()
            ToggleSetting(
                title = "High process priority",
                description = "Request more CPU time for the game",
                checked = highPriority,
                onChange = { highPriority = it }
            )
        }

        Spacer(Modifier.height(16.dp))
        SettingsGroup(icon = Icons.Filled.DeveloperMode, title = "About") {
            InfoRow(label = "App version", value = "0.1")
            DividerLine()
            InfoRow(label = "License", value = "AGPL-3.0")
            DividerLine()
            InfoRow(label = "Developer", value = "NX-developer")
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun SettingsGroup(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(NXSurface)
            .border(1.dp, NXOutline, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NXGreen.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = NXGreen, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(14.dp))
        content()
    }
}

@Composable
private fun SliderSetting(
    title: String,
    description: String,
    value: Float,
    valueLabel: String,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NXGreen.copy(alpha = 0.16f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(valueLabel, style = MaterialTheme.typography.labelSmall, color = NXGreen, fontWeight = FontWeight.Bold)
            }
        }
        Text(description, style = MaterialTheme.typography.bodyMedium, color = NXTextSecondary)
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = NXGreen,
                activeTrackColor = NXGreen,
                inactiveTrackColor = NXSurfaceVariant
            )
        )
    }
}

@Composable
private fun ChipSetting(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(option, option == selected) { onSelect(option) }
            }
        }
    }
}

@Composable
private fun ToggleSetting(
    title: String,
    description: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = NXTextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NXGreenDark,
                uncheckedThumbColor = NXTextMuted,
                uncheckedTrackColor = NXSurfaceVariant,
                uncheckedBorderColor = NXOutline
            )
        )
    }
}

@Composable
private fun JvmArgsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NXSurfaceVariant)
            .clickable { }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Custom JVM arguments", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Text("-XX:+UnlockExperimentalVMOptions", style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = NXTextSecondary)
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .height(1.dp)
            .background(NXOutline)
    )
}
