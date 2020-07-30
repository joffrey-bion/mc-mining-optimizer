import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    application
}

group = "org.hildan"
version = "0.1"

application {
    mainClassName = "org.hildan.minecraft.mining.optimizer.McMiningOptimizerKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions.jvmTarget = "1.8"
