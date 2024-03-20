plugins {
    kotlin("multiplatform") version "1.9.23" // Use the appropriate Kotlin version
    `maven-publish`
}

group = "io.nev3rfail.escpos2bmp"
version = "0.1.0"

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}


kotlin {

    jvmToolchain(8)
    /*androidTarget {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += "-Xexpect-actual-classes"
                jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }
    }*/
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
            api("BitmapKt:bitmap") {
                version {
                    branch = "main"
                }
            }
        }
        commonTest.configure {
            /*kotlin {
                srcDirs("src/commonMain/resources")
            }*/
        }
        commonTest.dependencies {
            implementation("io.kotest:kotest-assertions-core:5.8.1") // For assertions
        }

        /*jvmMain.dependencies {
            implementation("BitmapKt:bitmap") {
                version {
                    branch = "main"
                }

            }
        }*/
        jvmTest.dependencies {
            implementation("io.kotest:kotest-runner-junit5:5.8.1") // For Kotest's JUnit5 runner
            implementation("io.kotest:kotest-framework-engine:5.8.1") // Test engine
            implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
        }
    }
}/*

kotlin {
    jvm() // Start with JVM, you can add more platforms here like ios(), js(), etc.
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.github.g0dkar:qrcode-kotlin:4.1.1")
                api("BitmapKt:bitmap") {
                    version {
                        branch = "main"
                    }
                }
                // Add common dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("io.kotest:kotest-assertions-core:5.8.1") // For assertions
                // Add common test dependencies here
            }
        }
        val jvmMain by getting {

            dependencies {
                implementation(kotlin("stdlib"))
                // Add JVM specific dependencies here
            }
        }
        val jvmTest by getting {
            dependencies {

            }
        }
    }
}
*/
/*

dependencies {
    implementation(kotlin("stdlib"))

    // Kotest dependencies
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1") // For Kotest's JUnit5 runner
    testImplementation("io.kotest:kotest-assertions-core:5.8.1") // For assertions
    testImplementation("io.kotest:kotest-framework-engine:5.8.1") // Test engine

    implementation("io.github.g0dkar:qrcode-kotlin:4.1.1")
    api("BitmapKt:bitmap") {
        version {
            branch = "main"
        }
    }

}*/
/*sourceSets {
    test {
        kotlin {
            srcDirs("src/main/resources") // , "src/commonMain/resources")
        }
    }
}*/


tasks.withType<Test> {
    useJUnitPlatform()
}