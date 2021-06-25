import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.serialization") version "1.5.20"
    application
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("jacoco")
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "jacoco")

    jacoco {
        toolVersion = "0.8.6"
    }

    group = "fr.fabienhebuterne"
    version = "1.0.1"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://kotlin.bintray.com/kotlinx")
        jcenter()
    }

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.isEnabled = true
            csv.isEnabled = true
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}

application {
    mainClassName = "fr.fabienhebuterne.mcscan.McScanKt"
}
