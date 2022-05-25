import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val junitVersion = "5.8.2"
val logbackVersion = "1.2.11"
val kotlinxCoroutinesVersion = "1.6.1"

plugins {
    kotlin("jvm") version  "1.6.20"
    id("io.gitlab.arturbosch.detekt").version("1.20.0")
    idea
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
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

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
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
