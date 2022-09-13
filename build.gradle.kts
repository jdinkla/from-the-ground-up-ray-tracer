import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotestVersion = "5.4.2"
val coroutinesVersion = "1.6.4"
val logbackVersion = "1.2.11"

plugins {
    kotlin("jvm") version "1.7.10"
    id("io.gitlab.arturbosch.detekt").version("1.20.0")
    idea
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

repositories {
    jcenter()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    mavenCentral()
}

val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "17"
}

val compileTestKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

detekt {
    config = files("detekt-config.yml")
    source = files("src/main/kotlin", "src/test/kotlin")

}

task<JavaExec>("commandline") {
    main = "net.dinkla.raytracer.gui.CommandLineUi"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("World10.kt", "build/World10.png")
}

task<JavaExec>("swing") {
    main = "net.dinkla.raytracer.gui.swing.FromTheGroundUpRayTracerKt"
    classpath = sourceSets["main"].runtimeClasspath
}
