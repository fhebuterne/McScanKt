import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.32"
    application
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

allprojects {
    group = "fr.fabienhebuterne"
    version = "1.0.1"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://kotlin.bintray.com/kotlinx")
        jcenter()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}

application {
    mainClassName = "fr.fabienhebuterne.mcscan.McScanKt"
}
