plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.watchfacestudio.cosmic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.watchfacestudio.cosmic"
        minSdk = 26
        targetSdk = 35
        versionCode = 10008
        versionName = "3.0.0p"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.wearable)
    implementation(libs.play.services.wearable)
    implementation(libs.swiperefreshlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}