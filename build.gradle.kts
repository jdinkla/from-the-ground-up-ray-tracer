plugins {
    kotlin("jvm")
    id("dev.detekt")
    jacoco
    idea
    application
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    // Embedded Kotlin scripting host: lets the ray tracer compile & evaluate external
    // `.scene.kts` DSL files at runtime (TASK-17). JVM-only, so it lives in jvmMain.
    implementation(kotlin("scripting-common"))
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-jvm-host"))
    implementation(KotlinX.coroutines.core)
    // Dispatchers.Swing for the desktop UI: keeps the CPU render off the Event Dispatch Thread
    // while every Swing component touch hops back onto it (TASK-33).
    implementation(KotlinX.coroutines.swing)
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
    jvmToolchain(25)
    sourceSets["main"].kotlin.srcDir("src/commonMain/kotlin")
    sourceSets["main"].kotlin.srcDir("src/jvmMain/kotlin")
    sourceSets["main"].kotlin.srcDir("src/examples/kotlin")
    sourceSets["test"].kotlin.srcDir("src/commonTest/kotlin")
    sourceSets["test"].kotlin.srcDir("src/jvmTest/kotlin")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "net/dinkla/raytracer/examples/**",
                    "net/dinkla/raytracer/MainKt.class",
                    "net/dinkla/raytracer/ui/swing/**",
                    // Entry-point glue for the `audit` task; the rest of the audit package is tested.
                    "net/dinkla/raytracer/audit/AuditMainKt.class",
                )
            }
        })
    )
}

tasks.register<JavaExec>("commandline") {
    mainClass.set("MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("World10.kt", "build/World10.png")
}

tasks.register<JavaExec>("swing") {
    mainClass.set("net.dinkla.raytracer.ui.swing.FromTheGroundUpRayTracerKt")
    classpath = sourceSets["main"].runtimeClasspath
}

// Scene/example coverage & health audit (TASK-38). Builds and low-res renders every example scene
// and reports uncovered classes, per-class multiplicity, and suspect (near-black) renders. Intentionally
// NOT wired into `test`/`check` — it renders ~70 scenes and some need downloaded .ply meshes.
tasks.register<JavaExec>("audit") {
    group = "verification"
    description = "Audits example scenes: class coverage, multiplicity, and suspect (near-black) renders."
    mainClass.set("net.dinkla.raytracer.audit.AuditMainKt")
    classpath = sourceSets["main"].runtimeClasspath
    maxHeapSize = "4g" // some scenes (e.g. ManySpheresOnAPlane: 10k spheres) are memory-heavy to build
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
