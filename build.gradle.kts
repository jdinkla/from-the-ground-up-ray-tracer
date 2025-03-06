plugins {
    kotlin("jvm") version "2.0.21"
    id("io.gitlab.arturbosch.detekt")
    idea
    application
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(KotlinX.coroutines.core)
    implementation("ch.qos.logback:logback-classic:_")
    implementation("com.soywiz.korlibs.korim:korim:_")
    implementation("com.github.ajalt.clikt:clikt:_")
    implementation("io.github.classgraph:classgraph:_")

    testImplementation(Testing.kotest.runner.junit5)
    testImplementation(Testing.kotest.assertions.core)
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
    mainClass.set("net.dinkla.raytracer.ui.swing.FromTheGroundUpRayTracerKt")
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
