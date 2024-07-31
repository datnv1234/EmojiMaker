import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    jacoco
}
jacoco {
    toolVersion = "0.8.1"
}

val apikeyPropertiesFile = rootProject.file("apikey.properties")
val apikeyProperties = Properties()
apikeyProperties.load(FileInputStream(apikeyPropertiesFile))

var contentProviderAuthority = ""

android {
    namespace = "com.wa.ai.emojimaker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wa.ai.emojimaker"
        minSdk = 24
        targetSdk = 33
        versionCode = 16
        versionName = "1.6"

        val formattedDate = SimpleDateFormat("MM.dd.yyyy").format(Date())
        base.archivesBaseName = "AI_Emoji_Maker_v${versionName}(${versionCode})_${formattedDate}"

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        contentProviderAuthority = "$applicationId.provider.StickerContentProvider"
        manifestPlaceholders["contentProviderAuthority"] = contentProviderAuthority
        buildConfigField("String", "CONTENT_PROVIDER_AUTHORITY", "\"${contentProviderAuthority}\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    kapt {
        correctErrorTypes = true
    }

    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.datastore:datastore-preferences-core-jvm:1.1.1")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    //App update
    implementation ("com.google.android.play:app-update-ktx:2.1.0")
    implementation ("com.google.android.play:app-update:2.1.0")

    //noinspection LifecycleAnnotationProcessorWithJava8
    kapt("androidx.lifecycle:lifecycle-compiler:2.7.0")

    implementation("com.tbuonomo:dotsindicator:5.0")

    //Nav

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:31.2.3"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation ("com.google.firebase:firebase-storage:21.0.0")

    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Lottie
    implementation("com.airbnb.android:lottie:6.4.0")

    //picasso to load image
    implementation ("com.squareup.picasso:picasso:2.71828")

    // Ads
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.google.android.ump:user-messaging-platform:2.2.0")
    implementation("androidx.window:window:1.2.0")


    //Adjust
    implementation("com.adjust.sdk:adjust-android:4.38.3")
    implementation("com.android.installreferrer:installreferrer:2.2")

    // Add the following if you are using the Adjust SDK inside web views on your app
    implementation("com.adjust.sdk:adjust-android-webbridge:4.38.3")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.google.android.gms:play-services-ads:23.0.0")
    implementation("com.google.android.gms:play-services-appset:16.0.2")

    //Dimens
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation("com.intuit.sdp:sdp-android:1.1.1")

    // Room
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")

    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:2.6.1")
    androidTestImplementation("androidx.room:room-testing:2.6.1")

    // RETROFIT ------------------------------------------------------------------------------------
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation ("com.github.kittinunf.fuel:fuel-android:2.3.1")


    implementation ("org.msgpack:msgpack-core:0.8.22")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}