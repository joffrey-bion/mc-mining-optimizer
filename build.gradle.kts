plugins {
    kotlin("jvm") version "2.1.0"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(kotlin("test-junit5"))
}
