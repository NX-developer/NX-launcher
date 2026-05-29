package com.nxlauncher.ui.components

fun formatReleaseTime(iso: String): String {
    if (iso.length < 10) return iso
    val date = iso.substring(0, 10)
    val time = if (iso.length >= 16 && iso[10] == 'T') iso.substring(11, 16) else ""
    return if (time.isNotEmpty()) "$date · $time" else date
}
