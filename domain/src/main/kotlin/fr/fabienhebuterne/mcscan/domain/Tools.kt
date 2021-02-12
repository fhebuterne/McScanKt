package fr.fabienhebuterne.mcscan.domain

fun String.multilinePattern(): String {
    return this.trim().replace(Regex("\\s"), "")
}
