plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    java
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junitJupiter}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation("io.strikt:strikt-core:${Versions.strikt}")
    testImplementation("io.strikt:strikt-mockk:${Versions.strikt}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junitJupiter}")
    implementation("br.com.gamemods:region-manipulator:${Versions.regionManipulator}")
    implementation("br.com.gamemods:nbt-manipulator:${Versions.nbtManipulator}")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:${Versions.kotlinxCli}")
    implementation("org.kodein.di:kodein-di:${Versions.kodein}")
    implementation("io.github.oshai:kotlin-logging-jvm:${Versions.kotlinLogging}")
    implementation("org.slf4j:slf4j-api:${Versions.slf4jApi}")
    implementation("ch.qos.logback:logback-core:${Versions.logback}")
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationJson}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutinesCore}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${Versions.jacksonDataformatYaml}")
}

tasks.test {
    useJUnitPlatform()
    testLogging.events("started", "passed", "skipped", "failed")
}
