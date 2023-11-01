import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val logbackVersion = "1.4.11"
val kotestVersion = "5.7.2"
val coroutinesVersion = "1.7.3"
val korioVersion = "2.2.0"
val korimVersion = "2.2.0"
val kotlinWrappersVersion = "18.16.12-pre.636"
val cliktVersion = "4.2.1"

group = "net.dinkla"
version = "1.0"

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    kotlin("multiplatform") version "1.9.20"
    id("io.kotest.multiplatform") version "5.7.2"
    application
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    maven {
        url = uri("https://kotlin.bintray.com/kotlinx")
    }
}

kotlin {
    jvm() {
        withJava()
    }
    js() {
        nodejs()
        binaries.executable()
    }
    linuxX64() {
        binaries.executable()
    }
    macosX64 {
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("script-runtime"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("com.soywiz.korlibs.korio:korio:$korioVersion")
                implementation("com.soywiz.korlibs.korim:korim:$korimVersion")
                implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
            }
        }
        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
            }
        }
        val jvmTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
            }
        }

        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-node:$kotlinWrappersVersion")
            }
        }
        val jsTest by getting {
            dependsOn(commonTest)
        }
        val linuxX64Main by getting {
            dependsOn(commonMain)
        }
        val linuxX64Test by getting {
            dependsOn(commonTest)
        }
        val macosX64Main by getting {
            dependsOn(commonMain)
        }
        val macosX64Test by getting {
            dependsOn(commonTest)
        }
    }

    targets.withType(KotlinNativeTarget::class.java) {
        binaries.all {
            binaryOptions["memoryModel"] = "experimental"
            binaryOptions["freezing"] = "disabled"
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}

task<JavaExec>("swing") {
    mainClass.set("net.dinkla.raytracer.swing.FromTheGroundUpRayTracerKt")
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("cmd-jvm") {
    mainClass.set("MainKt")
    classpath = sourceSets["main"].runtimeClasspath
}

task<Exec>("cmd-linux") {
    val args: String = if (project.hasProperty("args")) {
        project.properties["args"] as String
    } else {
        ""
    }
    commandLine = listOf("build/bin/linuxX64/releaseExecutable/from-the-ground-up-ray-tracer.kexe")
    setArgs(args.split(" "))
}

task<Exec>("cmd-macos") {
    val args: String = if (project.hasProperty("args")) {
        project.properties["args"] as String
    } else {
        ""
    }
    commandLine = listOf("build/bin/macosX64/releaseExecutable/from-the-ground-up-ray-tracer.kexe")
    setArgs(args.split(" "))
}

task<Exec>("cmd-js") {
    val args: String = if (project.hasProperty("args")) {
        project.properties["args"] as String
    } else {
        ""
    }
    val js = "packages/from-the-ground-up-ray-tracer/kotlin/from-the-ground-up-ray-tracer.js"
    workingDir = File("build/js")
    commandLine = listOf("node")
    setArgs(listOf(js) + args.split(" "))
}
