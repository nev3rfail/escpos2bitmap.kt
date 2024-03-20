enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

sourceControl {
    gitRepository(uri("https://github.com/mihonapp/bitmap.kt.git")) {
        producesModule("BitmapKt:bitmap")
    }
}


pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "escpos2bmp-kt"
