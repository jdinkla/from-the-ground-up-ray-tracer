import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val junitVersion = "5.5.0"
val javafxVersion = "11-ea+25"
val logbackVersion = "1.2.3"
val coroutinesVersion = "1.1.1"

plugins {
    kotlin("jvm") version  "1.3.41"  // TODO update
    id("io.gitlab.arturbosch.detekt").version("1.6.0")
    id("org.openjfx.javafxplugin").version("0.0.8")
    idea
}

dependencies {

    compile(kotlin("stdlib"))
    compile(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:" + Deps.coroutinesVersion)
    implementation("org.openjfx:javafx:11.0.2")

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

task<JavaExec>("javafx") {
    main = "net.dinkla.raytracer.gui.FromTheGroundUpRayTracer"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--add-modules=javafx.controls,javafx.base,javafx.graphics")
}

javafx {
    version = "11.0.2"
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.base")
}