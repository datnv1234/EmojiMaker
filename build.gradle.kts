// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id ("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}