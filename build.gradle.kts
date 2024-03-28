plugins {
    id("com.android.library") version "8.2.0-beta05"//"8.3.1"
    kotlin("multiplatform") version "1.9.23"
    `maven-publish`
}

group = "io.nev3rfail.escpos2bmp"
version = "0.1.0"


repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    /*flatDir {
        dirs("libs", "app/libs") // Add this line
    }*/
}


kotlin {

    jvmToolchain(8)
    androidTarget {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += "-Xexpect-actual-classes"
                jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }

    }

    jvm {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += "-Xexpect-actual-classes"
                jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }
    }

    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation("io.github.g0dkar:qrcode-kotlin:4.1.1")
        }
        commonTest.configure {
        }
        commonTest.dependencies {
            implementation(kotlin("test")) // Brings all the platform dependencies automatically
            implementation("io.kotest:kotest-assertions-core:5.8.1") // For assertions
        }

        jvmMain.dependencies {
            api(files("libs/bitmap-jvm.jar"))
        }
        jvmTest.dependencies {
            implementation("io.kotest:kotest-runner-junit5:5.8.1") // For Kotest's JUnit5 runner
            implementation("io.kotest:kotest-framework-engine:5.8.1") // Test engine
            implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
        }
        androidMain.dependencies {
            //api(files("./libs/bitmap-release.aar"))
            api(files("libs/bitmap-android.aar"))
        }
    }
}
android {
    namespace = "io.nev3rfail.escpos2bmp"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}