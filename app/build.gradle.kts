plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

var variantOutPutFileName = "点名程序.apk"
var nameSpaceString = "com.maunc.randomcallname"
var applicationIdString = "com.maunc.randomcallname"
var propertyNameVersionCodeKey = "versionCode"
var propertyNameVersionNameKey = "versionName"
var jvmTargetVersion = "1.8"

android {
    signingConfigs {
        create("release") {
            storeFile = file("$rootDir/maunc.jks")
            storePassword = "123456"
            keyAlias = "123456"
            keyPassword = "123456"
        }
    }
    namespace = nameSpaceString
    compileSdk = libs.versions.compileSdkVerison.get().toInt()

    defaultConfig {
        applicationId = applicationIdString
        minSdk = libs.versions.minSdkVersion.get().toInt()
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        //读取的是项目下的gradle.properties文件
        versionCode = providers.gradleProperty(propertyNameVersionCodeKey).get().toInt()
        versionName = providers.gradleProperty(propertyNameVersionNameKey).get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }

    kotlinOptions {
        jvmTarget = jvmTargetVersion
    }

//    configurations.all {
//        exclude(group = "androidx.lifecycle", module = "lifecycle-viewmodel-ktx")
//    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                variantOutPutFileName
        }
    }
}

dependencies {

    implementation(libs.bundles.android)

    implementation(libs.ext.baseAdapter)
    implementation(libs.ext.room.runtime)
    kapt(libs.ext.room.compiler)
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.ext.auto.size)
    implementation(libs.ext.mmkv)
}