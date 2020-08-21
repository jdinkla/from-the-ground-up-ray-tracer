import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val junitVersion = "5.6.2"
val logbackVersion = "1.2.3"
val coroutinesVersion = "1.3.9"

plugins {
    kotlin("jvm") version  "1.4.0"
    id("io.gitlab.arturbosch.detekt").version("1.7.4")
    idea
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

repositories {
    jcenter()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "11"
}

val compileTestKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

detekt {
    input = files("src/main/kotlin", "src/test/kotlin")
    config = files("detekt-config.yml")
}

task<JavaExec>("commandline") {
    main = "net.dinkla.raytracer.gui.CommandLineUi"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("World10.kt", "build/World10.png")
}

task<JavaExec>("swing") {
    main = "net.dinkla.raytracer.gui.swing.FromTheGroundUpRayTracer"
    classpath = sourceSets["main"].runtimeClasspath
}
