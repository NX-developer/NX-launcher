package com.nxlauncher.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nxlauncher.data.Constants
import com.nxlauncher.data.DeviceInfo
import com.nxlauncher.data.JavaRuntimes
import com.nxlauncher.ui.components.FilterChip
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXGreenDark
import com.nxlauncher.ui.theme.NXError
import com.nxlauncher.ui.theme.NXOutline
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXSurfaceVariant
import com.nxlauncher.ui.theme.NXTextMuted
import com.nxlauncher.ui.theme.NXTextSecondary
import kotlin.math.roundToInt

private val GAME_LANGUAGES = listOf(
    "English" to "en_us",
    "Türkçe" to "tr_tr",
    "Deutsch" to "de_de",
    "Español" to "es_es",
    "Français" to "fr_fr",
    "Italiano" to "it_it",
    "Português (Brasil)" to "pt_br",
    "Русский" to "ru_ru",
    "日本語" to "ja_jp",
    "한국어" to "ko_kr",
    "简体中文" to "zh_cn",
    "繁體中文" to "zh_tw",
    "Polski" to "pl_pl",
    "Nederlands" to "nl_nl",
    "Українська" to "uk_ua",
    "العربية" to "ar_sa"
)

@Composable
fun SettingsScreen() {
    var resolution by remember { mutableIntStateOf(100) }
    var ram by remember { mutableIntStateOf(2048) }
    var renderer by remember { mutableStateOf("Zink") }
    var runtime by remember { mutableStateOf("Auto") }
    var gameLanguage by remember { mutableStateOf("English") }
    var jvmArgs by remember { mutableStateOf("") }
    var touchControls by remember { mutableStateOf(true) }
    var gamepad by remember { mutableStateOf(false) }
    var gesture by remember { mutableStateOf(true) }
    var keepScreenOn by remember { mutableStateOf(true) }
    var highPriority by remember { mutableStateOf(false) }

    var showResolutionDialog by remember { mutableStateOf(false) }
    var showRamDialog by remember { mutableStateOf(false) }
    var showJvmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val deviceStats = remember { DeviceInfo.read(context) }

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
                value = resolution.toFloat(),
                valueLabel = "$resolution%",
                range = 25f..100f,
                steps = 14,
                onChange = { resolution = it.roundToInt() },
                onValueClick = { showResolutionDialog = true }
            )
            DividerLine()
            SliderSetting(
                title = "Allocated memory",
                description = "Amount of RAM reserved for the game",
                value = ram.toFloat(),
                valueLabel = "$ram MB",
                range = 512f..8192f,
                steps = 14,
                onChange = { ram = (it / 256).roundToInt() * 256 },
                onValueClick = { showRamDialog = true }
            )
            MemoryInfoBlock(allocatedMb = ram, stats = deviceStats)
            DividerLine()
            ChipSetting(
                title = "Renderer",
                options = listOf("Zink", "GL4ES", "Holy GL4ES", "VirGL"),
                selected = renderer,
                onSelect = { renderer = it }
            )
        }

        Spacer(Modifier.height(16.dp))
        SettingsGroup(icon = Icons.Filled.Memory, title = "Runtime") {
            ChipSetting(
                title = "Java runtime",
                options = listOf("Auto") + JavaRuntimes.ordered,
                selected = runtime,
                onSelect = { runtime = it }
            )
            if (runtime == "Auto") {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Tries the runtimes and uses the one that matches each Minecraft version",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NXTextSecondary
                )
            }
            DividerLine()
            ActionRow(
                title = "Custom JVM arguments",
                value = jvmArgs.ifBlank { "Tap to add" },
                onClick = { showJvmDialog = true }
            )
        }

        Spacer(Modifier.height(16.dp))
        SettingsGroup(icon = Icons.Filled.Language, title = "Game") {
            LanguageSetting(
                selectedDisplay = gameLanguage,
                onSelect = { gameLanguage = it }
            )
        }

        Spacer(Modifier.height(16.dp))
        SettingsGroup(icon = Icons.Filled.SportsEsports, title = "Controls") {
            ToggleSetting("Touch controls", "On screen buttons and joystick", touchControls) { touchControls = it }
            DividerLine()
            ToggleSetting("Gamepad support", "Map a physical controller", gamepad) { gamepad = it }
            DividerLine()
            ToggleSetting("Gesture controls", "Swipe and pinch interactions", gesture) { gesture = it }
        }

        Spacer(Modifier.height(16.dp))
        SettingsGroup(icon = Icons.Filled.Monitor, title = "System") {
            ToggleSetting("Keep screen on", "Prevent the display from sleeping while playing", keepScreenOn) { keepScreenOn = it }
            DividerLine()
            ToggleSetting("High process priority", "Request more CPU time for the game", highPriority) { highPriority = it }
        }

        Spacer(Modifier.height(16.dp))
        SettingsGroup(icon = Icons.Filled.DeveloperMode, title = "About") {
            InfoRow(label = "App version", value = "0.1")
            DividerLine()
            InfoRow(label = "License", value = "AGPL-3.0")
            DividerLine()
            InfoRow(label = "Developer", value = "NX-developer")
            DividerLine()
            WebsiteRow(onClick = {
                runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEBSITE_URL)))
                }
            })
        }

        Spacer(Modifier.height(28.dp))
    }

    if (showResolutionDialog) {
        NumberInputDialog(
            title = "Resolution scale",
            suffix = "%",
            initial = resolution,
            min = 25,
            max = 100,
            onDismiss = { showResolutionDialog = false },
            onConfirm = { resolution = it; showResolutionDialog = false }
        )
    }
    if (showRamDialog) {
        NumberInputDialog(
            title = "Allocated memory",
            suffix = "MB",
            initial = ram,
            min = 512,
            max = 8192,
            onDismiss = { showRamDialog = false },
            onConfirm = { ram = it; showRamDialog = false }
        )
    }
    if (showJvmDialog) {
        TextInputDialog(
            title = "Custom JVM arguments",
            initial = jvmArgs,
            placeholder = "-XX:+UseG1GC -Xss2M",
            onDismiss = { showJvmDialog = false },
            onConfirm = { jvmArgs = it.trim(); showJvmDialog = false }
        )
    }
}

@Composable
private fun SettingsGroup(icon: ImageVector, title: String, content: @Composable () -> Unit) {
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
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
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
    onChange: (Float) -> Unit,
    onValueClick: () -> Unit
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
                    .clickable { onValueClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipSetting(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(option, option == selected) { onSelect(option) }
            }
        }
    }
}

@Composable
private fun MemoryInfoBlock(allocatedMb: Int, stats: com.nxlauncher.data.DeviceStats) {
    val exceeds = allocatedMb > stats.availableRamMb
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(NXSurfaceVariant)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Available RAM", style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
            Text(
                stats.availableRamMb.toString() + " / " + stats.totalRamMb + " MB",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Free storage", style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
            Text(
                stats.freeStorageReadable(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
    if (exceeds) {
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(NXError.copy(alpha = 0.12f))
                .border(1.dp, NXError.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = NXError, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Allocated memory is higher than the available RAM on this device",
                style = MaterialTheme.typography.bodyMedium,
                color = NXError
            )
        }
    }
}

@Composable
private fun LanguageSetting(selectedDisplay: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val code = GAME_LANGUAGES.firstOrNull { it.first == selectedDisplay }?.second ?: "en_us"
    Column {
        Text("In game language", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        Text("Language used inside Minecraft. The launcher stays in English.", style = MaterialTheme.typography.bodyMedium, color = NXTextSecondary)
        Spacer(Modifier.height(10.dp))
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NXSurfaceVariant)
                    .clickable { expanded = true }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(selectedDisplay, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Text(code, style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
                }
                Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = NXTextSecondary)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(NXSurfaceVariant)
                    .heightIn(max = 360.dp)
            ) {
                GAME_LANGUAGES.forEach { (display, langCode) ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(display, color = MaterialTheme.colorScheme.onBackground)
                                Text(langCode, style = MaterialTheme.typography.labelSmall, color = NXTextMuted)
                            }
                        },
                        onClick = { onSelect(display); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleSetting(title: String, description: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
private fun ActionRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NXSurfaceVariant)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(value, style = MaterialTheme.typography.labelSmall, color = NXTextMuted, maxLines = 1)
        }
        Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = NXTextSecondary)
    }
}

@Composable
private fun WebsiteRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Website", style = MaterialTheme.typography.bodyLarge, color = NXTextSecondary)
            Text(Constants.WEBSITE_URL, style = MaterialTheme.typography.labelSmall, color = NXGreen)
        }
        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = NXGreen, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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

@Composable
private fun NumberInputDialog(
    title: String,
    suffix: String,
    initial: Int,
    min: Int,
    max: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var text by remember { mutableStateOf(initial.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NXSurface,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        title = { Text(title) },
        text = {
            Column {
                Text("Enter a value between $min and $max $suffix", style = MaterialTheme.typography.bodyMedium, color = NXTextSecondary)
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = text,
                    onValueChange = { input -> text = input.filter { it.isDigit() }.take(6) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = dialogFieldColors()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsed = text.toIntOrNull() ?: initial
                onConfirm(parsed.coerceIn(min, max))
            }) { Text("Save", color = NXGreen) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = NXTextSecondary) }
        }
    )
}

@Composable
private fun TextInputDialog(
    title: String,
    initial: String,
    placeholder: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NXSurface,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        title = { Text(title) },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(placeholder, color = NXTextMuted) },
                colors = dialogFieldColors()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) { Text("Save", color = NXGreen) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = NXTextSecondary) }
        }
    )
}

@Composable
private fun dialogFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = NXSurfaceVariant,
    unfocusedContainerColor = NXSurfaceVariant,
    focusedIndicatorColor = NXGreen,
    unfocusedIndicatorColor = NXOutline,
    cursorColor = NXGreen,
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
)
