plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    java
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("io.mockk:mockk:1.10.5")
    testImplementation("io.strikt:strikt-core:0.29.0")
    testImplementation("io.strikt:strikt-mockk:0.29.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    implementation("br.com.gamemods:region-manipulator:2.0.0")
    implementation("br.com.gamemods:nbt-manipulator:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.1")
    implementation("org.kodein.di:kodein-di:7.3.1")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.4")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2-native-mt")
}

tasks.test {
    useJUnitPlatform()
    testLogging.events("started", "passed", "skipped", "failed")
}
