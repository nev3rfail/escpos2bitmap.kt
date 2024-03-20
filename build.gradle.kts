plugins {
    kotlin("jvm") version "1.9.23" // Use the appropriate Kotlin version
    `maven-publish`
}

group = "io.nev3rfail.escpos2bmp-kt"
version = "0.1.0"

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}


dependencies {
    implementation(kotlin("stdlib"))

    // Kotest dependencies
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1") // For Kotest's JUnit5 runner
    testImplementation("io.kotest:kotest-assertions-core:5.8.1") // For assertions
    testImplementation("io.kotest:kotest-framework-engine:5.8.1") // Test engine



    api("BitmapKt:bitmap") {
        version {
            branch = "main"
        }
    }

    implementation("io.github.g0dkar:qrcode-kotlin:4.1.1")
}
sourceSets {


    test {
        kotlin {
            srcDirs("src/main/resources") // , "src/commonMain/resources")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

}