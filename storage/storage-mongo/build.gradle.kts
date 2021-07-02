plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation("org.kodein.di:kodein-di:${Versions.kodein}")
    implementation("org.litote.kmongo:kmongo:${Versions.kmongo}")
    implementation("org.litote.kmongo:kmongo-async:${Versions.kmongo}")
    implementation("org.litote.kmongo:kmongo-coroutine:${Versions.kmongo}")
    implementation(project(":domain"))
}
