package com.nxlauncher.ui.components

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DISPLAY_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale.ENGLISH)

fun formatReleaseTime(iso: String): String {
    if (iso.isBlank()) return ""
    return runCatching {
        OffsetDateTime.parse(iso)
            .atZoneSameInstant(ZoneId.systemDefault())
            .format(DISPLAY_FORMAT)
    }.getOrDefault(iso.take(10))
}
