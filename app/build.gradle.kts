plugins {
    alias(libs.plugins.li.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.li.android.hilt)
}

android {
    namespace = "cn.li.nowinli"
    compileSdk = 34

    defaultConfig {
        applicationId = "cn.li.nowinli"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
//        aidl = true
    }

    sourceSets {
        this.getByName("debug").apply {
            java {
                java.srcDirs(arrayOf("src/main/java", "build/data_binding_base_class_source_out/debug/out"))
            }
        }
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.jackson)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging)
    implementation(libs.okhttp3.sse)
    implementation(libs.jackson.kotlin.module)
    implementation(libs.viewModel.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}