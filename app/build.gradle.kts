plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

var variantOutPutFileName = "点名程序.apk"

android {
    namespace = "com.maunc.randomcallname"
    compileSdk = libs.versions.compileSdkVerison.get().toInt()

    defaultConfig {
        applicationId = "com.maunc.randomcallname"
        minSdk = libs.versions.minSdkVersion.get().toInt()
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        //读取的是项目下的gradle.properties文件
        versionCode = providers.gradleProperty("versionCode").get().toInt()
        versionName = providers.gradleProperty("versionName").get()

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

    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
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