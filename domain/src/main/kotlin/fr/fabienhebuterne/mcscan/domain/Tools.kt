package fr.fabienhebuterne.mcscan.domain

fun String.multilinePattern(): String {
    return this.trim().replace(Regex("\\s"), "")
}

fun getAvailableProcessor(): Int {
    val availableProcessor = Runtime.getRuntime().availableProcessors()
    return if (availableProcessor > 1) {
        availableProcessor - 1
    } else {
        availableProcessor
    }
}
