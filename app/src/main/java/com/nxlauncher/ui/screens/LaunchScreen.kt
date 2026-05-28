package com.nxlauncher.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxlauncher.ui.vm.LaunchStep
import com.nxlauncher.ui.vm.LaunchViewModel
import com.nxlauncher.ui.vm.StepStatus
import com.nxlauncher.ui.theme.NXAmber
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXOutline
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXSurfaceVariant
import com.nxlauncher.ui.theme.NXTextMuted
import com.nxlauncher.ui.theme.NXTextSecondary

@Composable
fun LaunchScreen(version: String, onBack: () -> Unit) {
    val vm: LaunchViewModel = viewModel()
    val context = LocalContext.current
    LaunchedEffect(version) { vm.start(context, version) }

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

        Spacer(Modifier.height(26.dp))
        Text(
            text = "Launching",
            style = MaterialTheme.typography.labelLarge,
            color = NXTextMuted
        )
        Text(
            text = "Minecraft " + version,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.height(28.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(NXSurface)
                .border(1.dp, NXOutline, RoundedCornerShape(18.dp))
                .padding(8.dp)
        ) {
            vm.steps.forEachIndexed { index, step ->
                StepRow(step)
                if (index != vm.steps.lastIndex) {
                    Box(
                        modifier = Modifier
                            .padding(start = 35.dp)
                            .width(1.dp)
                            .height(10.dp)
                            .background(NXOutline)
                    )
                }
            }
        }

        if (vm.finished) {
            Spacer(Modifier.height(22.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(NXAmber.copy(alpha = 0.10f))
                    .border(1.dp, NXAmber.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = NXAmber, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Rendering engine required",
                        style = MaterialTheme.typography.titleMedium,
                        color = NXAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "The launcher found your files and the right Java runtime. Actually drawing the game on screen needs a native engine: an ARM Java runtime, the LWJGL library ported to Android and an OpenGL ES translation layer. This is the part every mobile Java launcher builds on top of, and it is the next step for NX Launcher.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NXTextSecondary
                )
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun StepRow(step: LaunchStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepIndicator(step.status)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (step.detail.isNotBlank()) {
                Text(
                    text = step.detail,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor(step.status)
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(status: StepStatus) {
    val color = statusColor(status)
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        when (status) {
            StepStatus.DONE -> Icon(Icons.Filled.Check, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            StepStatus.WARN -> Icon(Icons.Filled.PriorityHigh, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            StepStatus.BLOCKED -> Icon(Icons.Filled.Bolt, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            StepStatus.RUNNING -> {
                val transition = rememberInfiniteTransition(label = "pulse")
                val pulse by transition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                    label = "pulseValue"
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .alpha(pulse)
                        .clip(CircleShape)
                        .background(color)
                )
            }
            StepStatus.PENDING -> Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NXTextMuted)
            )
        }
    }
}

private fun statusColor(status: StepStatus): Color = when (status) {
    StepStatus.DONE -> NXGreen
    StepStatus.RUNNING -> NXGreen
    StepStatus.WARN -> NXAmber
    StepStatus.BLOCKED -> NXAmber
    StepStatus.PENDING -> NXTextMuted
}
