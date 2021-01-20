import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21-2"
    application
    java
}

group = "fr.fabienhebuterne"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://kotlin.bintray.com/kotlinx")
    jcenter()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation("br.com.gamemods:region-manipulator:2.0.0")
    implementation("br.com.gamemods:nbt-manipulator:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3")
    implementation("org.kodein.di:kodein-di:7.2.0")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.4")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.7.30")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

val isCiOrCd: String? by project

tasks.withType<Jar>() {
    manifest {
        attributes["Main-Class"] = "fr.fabienhebuterne.mcscan.McScanKt"
    }
    if (isCiOrCd != null) {
        archiveFileName.set("McScanKt.jar")
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}
