val kotestVersion = "5.8.0"
val coroutinesVersion = "1.7.3"
val logbackVersion = "1.4.14"
val korioVersion = "2.2.0"
val korimVersion = "2.2.0"
val cliktVersion = "4.2.1"

plugins {
    kotlin("jvm") version "2.0.0-Beta3"
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
    idea
    application
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.soywiz.korlibs.korio:korio:$korioVersion")
    implementation("com.soywiz.korlibs.korim:korim:$korimVersion")
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
    sourceSets["main"].kotlin.srcDir("src/commonMain/kotlin")
    sourceSets["main"].kotlin.srcDir("src/jvmMain/kotlin")
    sourceSets["main"].kotlin.srcDir("src/examples/kotlin")
    sourceSets["test"].kotlin.srcDir("src/commonTest/kotlin")
    sourceSets["test"].kotlin.srcDir("src/jvmTest/kotlin")
}

tasks.test {
    useJUnitPlatform()
}

task<JavaExec>("commandline") {
    mainClass.set("MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("World10.kt", "build/World10.png")
}

task<JavaExec>("swing") {
    mainClass.set("net.dinkla.raytracer.swing.FromTheGroundUpRayTracerKt")
    classpath = sourceSets["main"].runtimeClasspath
}

application {
    mainClass.set("net.dinkla.raytracer.MainKt")
}

detekt {
    config.setFrom("detekt-config.yml")
    source.setFrom(
        "src/main/java",
        "src/main/kotlin",
        "src/commonMain/kotlin",
        "src/jvmMain/kotlin",
        "src/commonTest/kotlin",
        "src/jvmTest/kotlin",
    )
}
