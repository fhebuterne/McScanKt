plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation("org.kodein.di:kodein-di:7.3.1")
    implementation("org.litote.kmongo:kmongo:4.2.4")
    implementation("org.litote.kmongo:kmongo-async:4.2.4")
    implementation("org.litote.kmongo:kmongo-coroutine:4.2.4")
    implementation(project(":domain"))
}
