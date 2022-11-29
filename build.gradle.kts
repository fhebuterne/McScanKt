import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.kotlinJvm
    kotlin("plugin.serialization") version 1.7.22
    application
    java
    id("com.github.johnrengelman.shadow") version Versions.shadowJar
    id("jacoco")
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "jacoco")

    jacoco {
        toolVersion = Versions.jacoco
    }

    group = "fr.fabienhebuterne"
    version = "1.0.1"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://kotlin.bintray.com/kotlinx")
    }

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            csv.required.set(true)
        }
    }

    val javaVersion = 17

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }
}

application {
    mainClass.set("fr.fabienhebuterne.mcscan.McScanKt")
}
