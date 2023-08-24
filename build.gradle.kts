plugins {
    kotlin("jvm") version "1.9.10"
    application
}

group = "org.hildan"
version = "0.1"

application {
    mainClass.set("org.hildan.minecraft.mining.optimizer.McMiningOptimizerKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation(kotlin("test-junit5"))
}
