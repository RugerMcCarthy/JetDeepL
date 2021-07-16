// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val hilt_version: String by extra("2.37")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.1.0-alpha02")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
task<Delete>("clean") {
    delete(rootProject.buildDir)
}