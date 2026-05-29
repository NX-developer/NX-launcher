package com.nxlauncher.data

object JavaRuntimes {

    val ordered = listOf("Java 8", "Java 17", "Java 21", "Java 25")

    fun recommendedFor(version: String): String {
        val v = version.trim()
        val firstSegment = v.substringBefore('.').toIntOrNull()

        if (firstSegment != null && firstSegment >= 26) return "Java 25"
        if (firstSegment != null && firstSegment in 2..25 && firstSegment != 1) return "Java 25"

        if (v.startsWith("1.")) {
            val rest = v.removePrefix("1.")
            val minor = rest.substringBefore('.').toIntOrNull() ?: return "Java 21"
            val patch = rest.substringAfter('.', "").substringBefore('-').toIntOrNull() ?: 0
            return when {
                minor >= 21 -> "Java 21"
                minor == 20 && patch >= 5 -> "Java 21"
                minor in 17..20 -> "Java 17"
                else -> "Java 8"
            }
        }

        return "Java 21"
    }
}
