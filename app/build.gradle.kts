plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    java
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.1")
    implementation("org.kodein.di:kodein-di:7.4.0")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.4")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
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
        mainClassName = "fr.fabienhebuterne.mcscan.McScanKt"
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
