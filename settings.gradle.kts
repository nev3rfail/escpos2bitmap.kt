enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

sourceControl {
    gitRepository(uri("https://github.com/mihonapp/bitmap.kt.git")) {
        producesModule("BitmapKt:bitmap")
       // producesModule("BitmapKt:bitmap-jvm")
        //producesModule("BitmapKt:bitmap-android")
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


rootProject.name = "escpos2bmp"
