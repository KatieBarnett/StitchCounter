plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dagger.hilt.android) apply (false)
    alias(libs.plugins.ksp) apply (false)
    alias(libs.plugins.protobuf) apply (false)
    alias(libs.plugins.firebase.crashlytics) apply (false)
    alias(libs.plugins.google.services) apply (false)
    alias(libs.plugins.compose.compiler) apply (false)
}

ext {
    //Set the first two digits of the version code to the targetSdkVersion, such as 28.
    //Set the next three digits to the product version, such as 152 for a product version of 1.5.2.
    //Set the next two digits to build or release number, such as 01.
    //Reserve the last two digits for a multi-APK variant, such as 00.

    // phone: xx.xxx.xx.00
    // watch: xx.xxx.xx.01

    //35.201.01.00

    extra["appVersionName"] = "2.0.2"
    extra["appVersionCode"] = 352020100
    extra["compileSdk"] = 35
    extra["targetSdk"] = 33
    extra["targetSdkMobile"] = 35
    extra["minSdk"] = 26
}
