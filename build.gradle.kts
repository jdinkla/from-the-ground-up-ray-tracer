import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val logbackVersion = "1.2.11"
val kotestVersion = "5.4.2"
val coroutinesVersion = "1.6.4"
val korioVersion = "2.2.0"
val korimVersion = "2.2.0"
val kotlinWrappersVersion = "18.7.18-pre.386"

group = "net.dinkla"
version = "1.0"

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    kotlin("multiplatform") version "1.7.10"
    id("io.kotest.multiplatform") version "5.4.2"
    application
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
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("script-runtime"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("com.soywiz.korlibs.korio:korio:$korioVersion")
                implementation("com.soywiz.korlibs.korim:korim:$korimVersion")
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
                implementation("ch.qos.logback:logback-classic:${logbackVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${coroutinesVersion}")
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
    main = "net.dinkla.raytracer.gui.swing.FromTheGroundUpRayTracerKt"
    classpath = sourceSets["main"].runtimeClasspath
}

