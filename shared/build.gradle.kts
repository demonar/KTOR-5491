@file:Suppress("UnstableApiUsage")
import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode.BITCODE
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
}

var androidTarget: String = ""

kotlin {
    val android = android {
        publishLibraryVariants("release")
    }
    androidTarget = android.name
    val xcf = XCFramework()
    val iosX64 = iosX64()
    val iosArm64 = iosArm64()
    val iosSim = iosSimulatorArm64()
    configure(listOf(iosX64, iosArm64, iosSim)) {
        binaries {
            framework {
                export(commonlibs.kotlin.stdlib)
                //Date-Time
                export(commonlibs.kotlinx.datetime)
                //Network
                export(commonlibs.ktor.core)
                export(commonlibs.ktor.serialization)
                export(commonlibs.ktor.negotiation)
                export(commonlibs.ktor.json)
                export(commonlibs.ktor.logging)
                //Coroutines
                export(commonlibs.kotlinx.coroutines.core)
                //JSON
                export(commonlibs.kotlinx.serialization.json)
                //Platform specific
                export(commonlibs.ktor.client.ios)

                baseName = commonlibs.versions.ios.basename.get()
                xcf.add(this)
            }
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += listOf("-Xgc=cms")
        }
    }

    cocoapods {
        val iosDefinitions = commonlibs.versions.ios
        name = iosDefinitions.basename.get()
        summary = iosDefinitions.summary.get()
        homepage = iosDefinitions.homepage.get()
        authors = iosDefinitions.authors.get()
        version = commonlibs.versions.library.version.get()
        ios.deploymentTarget = iosDefinitions.deployment.target.get()
        framework {
            baseName = iosDefinitions.basename.get()
            isStatic = false
            transitiveExport = true
            embedBitcode(BITCODE)
        }
        specRepos {
            url("https://github.com/demonar/KTOR-5491.git")
        }
        publishDir = rootProject.file("./")

    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(commonlibs.kotlin.stdlib)
                //Date-Time
                api(commonlibs.kotlinx.datetime)
                //Network
                api(commonlibs.ktor.core)
                api(commonlibs.ktor.serialization)
                api(commonlibs.ktor.negotiation)
                api(commonlibs.ktor.json)
                api(commonlibs.ktor.logging)
                //Coroutines
                api(commonlibs.kotlinx.coroutines.core)
                //JSON
                api(commonlibs.kotlinx.serialization.json)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                //Network
                api(commonlibs.ktor.client.okhttp)
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                //Network
                api(commonlibs.ktor.client.ios)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.example.ktor5491"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    beforeEvaluate {
        libraryVariants.all {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }
    }
}

afterEvaluate {
    tasks.named("podPublishDebugXCFramework") {
        enabled = false
    }
    tasks.named("podSpecDebug") {
        enabled = false
    }
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = JavaVersion.VERSION_11.toString()
    }
}
