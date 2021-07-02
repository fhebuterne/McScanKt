plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    java
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:${Versions.kotlinxCli}")
    implementation("org.kodein.di:kodein-di:${Versions.kodein}")
    implementation("io.github.microutils:kotlin-logging-jvm:${Versions.kotlinLogging}")
    implementation("org.slf4j:slf4j-api:${Versions.slf4jApi}")
    implementation("ch.qos.logback:logback-core:${Versions.logback}")
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")
    implementation(project(":domain"))
    implementation(project(":storage:storage-mongo"))
}

tasks.test {
    useJUnitPlatform()
    testLogging.events("started", "passed", "skipped", "failed")
}

val isCiOrCd: String? by project

tasks.shadowJar {
    application {
        mainClass.set("fr.fabienhebuterne.mcscan.McScanKt")
    }

    minimize()

    if (isCiOrCd == null) {
        archiveFileName.set("McScanKt-${archiveVersion.getOrElse("unknown")}.jar")
    } else {
        // For ci/cd
        archiveFileName.set("McScanKt.jar")
    }

    destinationDirectory.set(file(System.getProperty("outputDir") ?: "$rootDir/build/"))
}

tasks.build {
    dependsOn("shadowJar")
}
