import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import java.io.FileNotFoundException

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
            implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.1")
            fancyZip("commonMain","libs/bitmap-common.jar")
        }
        commonTest.configure {
        }
        commonTest.dependencies {
            implementation(kotlin("test")) // Brings all the platform dependencies automatically
            implementation("io.kotest:kotest-assertions-core:5.8.1") // For assertions
        }

        jvmMain.dependencies {
            implementation(files("libs/bitmap-jvm.jar"))
        }
        jvmTest.dependencies {
            implementation("io.kotest:kotest-runner-junit5:5.8.1") // For Kotest's JUnit5 runner
            implementation("io.kotest:kotest-framework-engine:5.8.1") // Test engine
            implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
        }
        androidMain.dependencies {
            //api(files("./libs/bitmap-release.aar"))
            implementation(files("libs/bitmap-android.aar"))
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation("androidx.test:core-ktx:1.5.0")
                implementation("androidx.test:runner:1.5.2")
                implementation("androidx.test.espresso:espresso-core:3.5.1")
            }
        }
        androidInstrumentedTest.dependsOn(commonTest.get())
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


fun KotlinDependencyHandler.fancyZip(srcSet: String, /*what: (Any) -> Dependency?, */zipFilePath: String) {
    val sourceSetName = srcSet//name
    val zipFile = File(zipFilePath)
    if(!zipFile.exists()) {
        throw FileNotFoundException(zipFilePath)
    }
    val project: Project = this@fancyZip.project

    val taskName = "unzip${zipFile.nameWithoutExtension.split(Regex("[^A-Za-z0-9]")).joinToString("") {it.capitalize()}}"

    val unzipped = project.layout.buildDirectory.dir("tmp/$taskName").get()
    val unzipTask = project.tasks.register(taskName, Copy::class) {
        val outputDir = project.file(unzipped)
        from(project.zipTree(zipFile))
        into(outputDir)
    }

    project.kotlin.sourceSets.maybeCreate(sourceSetName).kotlin.srcDir {
        unzipped
    }
    project.tasks.whenTaskAdded {
        if (name.matches(Regex("compile.*Kotlin.*")) || name == "ideaProject") {
            dependsOn(unzipTask)
        }

        tasks.sourcesJar.configure {
            dependsOn(unzipTask)
        }
    }
}

/*
val unzipZip by tasks.registering(Copy::class) {
    val zipFile = file("libs/*common.jar")
    val outputDir = file("${layout.buildDirectory}/bitmap-common")

    from(zipTree(zipFile))
    into(outputDir)
}
*/

