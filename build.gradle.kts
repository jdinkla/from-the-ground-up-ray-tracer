import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val junitVersion = "5.5.0"
val javafxVersion = "11-ea+25"
val logbackVersion = "1.2.3"
val coroutinesVersion = "1.1.1"

plugins {
    kotlin("jvm") version  "1.3.41"
    id("io.gitlab.arturbosch.detekt").version("1.6.0")
    idea
}

dependencies {
    compile(kotlin("stdlib"))
    compile(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:" + Deps.coroutinesVersion)

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
