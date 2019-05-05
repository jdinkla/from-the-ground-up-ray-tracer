import Deps.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Deps.kotlinVersion
    id("io.gitlab.arturbosch.detekt").version(Deps.detektVersion)
    idea
}

dependencies {
    compile(kotlin("stdlib"))
    compile(group = "ch.qos.logback", name = "logback-classic", version = Deps.logbackVersion)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:" + Deps.coroutinesVersion)
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:" + Deps.coroutinesVersion)

    testImplementation("org.junit.jupiter:junit-jupiter-api:" + Deps.junitVersion)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:" + Deps.junitVersion)
}


repositories {
    jcenter()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
}

val compileTestKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

detekt {
    input = files("src/main/kotlin", "src/test/kotlin")
    filters = ".*/resources/.*,.*/build/.*"
    config = files("detekt-config.yml")
}

task<JavaExec>("commandline") {
    main = "net.dinkla.raytracer.gui.CommandLineUi"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("World10.kt", "build/World10.png")
}

task<JavaExec>("javafx") {
    main = "net.dinkla.raytracer.gui.FromTheGroundUpRayTracer"
    classpath = sourceSets["main"].runtimeClasspath
}
