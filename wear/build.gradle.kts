plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "dev.veryniche.stitchcounter"
    compileSdk = rootProject.extra["compileSdk"] as Int

    defaultConfig {
        applicationId = "dev.veryniche.stitchcounter"
        minSdk = rootProject.extra["minSdk"] as Int
        targetSdk = rootProject.extra["targetSdk"] as Int
        versionCode = rootProject.extra["appVersionCode"] as Int
        versionName = rootProject.extra["appVersionName"] as String
    }

    buildTypes {
        debug {
            versionNameSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.wear)
    implementation(libs.lifecycle.runtime.compose.android)
    implementation(libs.wear.tiles)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(project(":data"))
    implementation(project(":storage"))
    implementation(project(":core"))

    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.compose.ui.tooling.preview)

    // Compose for Wear OS Dependencies
    // NOTE: DO NOT INCLUDE a dependency on androidx.compose.material:material.
    // androidx.wear.compose:compose-material is designed as a replacement not an addition to
    // androidx.compose.material:material. If there are features from that you feel are missing from
    // androidx.wear.compose:compose-material please raise a bug to let us know:
    // https://issuetracker.google.com/issues/new?component=1077552&template=1598429&pli=1
    implementation(libs.wear.compose.material)
    implementation(libs.compose.material.iconscore)
    implementation(libs.compose.material.iconsext)

    // Foundation is additive, so you can use the mobile version in your Wear OS app.
    implementation(libs.wear.compose.foundation)

    // If you are using Compose Navigation, use the Wear OS version (NOT the
    // androidx.navigation:navigation-compose version), that is, uncomment the line below.
    implementation(libs.wear.compose.navigation)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.horologist.composables)
    implementation(libs.horologist.compose.layout)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.splashscreen)

    implementation(libs.wear.input)
    implementation(libs.wear.interactions)

    implementation(libs.playservices.wearable)

    implementation(libs.compose.runtime.livedata)

    implementation(libs.timber)

    implementation(libs.hilt.android)
    implementation(libs.wear.tooling.preview)
    implementation(libs.wear.tiles.tooling.preview)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.wear.protolayout)
    implementation(libs.wear.protolayout.expression)
    implementation(libs.wear.protolayout.material)
    implementation(libs.horologist.tiles)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.wear.tiles.renderer)
    debugImplementation(libs.wear.tiles.tooling)
}
