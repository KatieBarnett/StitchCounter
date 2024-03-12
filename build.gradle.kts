// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.dagger.hilt.android) apply (false)
    alias(libs.plugins.ksp) apply (false)
    alias(libs.plugins.protobuf) apply (false)
    alias(libs.plugins.firebase.crashlytics) apply (false)
    alias(libs.plugins.google.services) apply (false)
}

ext {
    //Set the first two digits of the version code to the targetSdkVersion, such as 28.
    //Set the next three digits to the product version, such as 152 for a product version of 1.5.2.
    //Set the next two digits to build or release number, such as 01.
    //Reserve the last two digits for a multi-APK variant, such as 00.

    //34.200.00.00

    extra["appVersionName"] = "2.0.0"
    extra["appVersionCode"] = 342000000
    extra["compileSdk"] = 34
    extra["targetSdkWear"] = 33
    extra["targetSdkMobile"] = 34
    extra["minSdk"] = 26
}

true // Needed to make the Suppress annotation work for the plugins block
