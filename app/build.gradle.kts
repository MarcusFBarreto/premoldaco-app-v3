plugins {
    // Mantemos a forma moderna de declarar o plugin. A outra foi removida.
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.pesquisapromo.premoldaco.premoldacoapp.v1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pesquisapromo.premoldaco.premoldacoapp.v1"
        minSdk = 21
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dependência da Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Dependências do Firebase
    // Mantemos apenas a declaração do BoM mais recente.
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    // O BoM acima gerencia as versões das bibliotecas abaixo:
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
}