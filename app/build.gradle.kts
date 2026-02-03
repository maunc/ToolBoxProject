plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

var variantOutPutFileName = "MauncToolBox.apk"
var nameSpaceString = "com.maunc.toolbox"
var applicationIdString = "com.maunc.toolbox"
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
        multiDexEnabled = true
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
            isMinifyEnabled = true
            isShrinkResources = true
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

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(project(":ViewLib"))
    implementation(project(":LiveEventBus"))
    implementation(project(":FileDownload"))
    implementation(project(":WebBridgeLib"))

    implementation(libs.bundles.android)
    implementation(libs.ext.gson)

    implementation(libs.ext.baseAdapter)
    implementation(libs.ext.room.runtime)
    kapt(libs.ext.room.compiler)
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.ext.auto.size)
    implementation(libs.ext.mmkv) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
    }
    implementation(libs.bundles.smart.refrsh.layout)
    implementation(libs.bundles.exo.player)
    implementation(libs.ext.glide)
    implementation(libs.ext.picture.selector)
    implementation(libs.ext.immersionbar)
}
