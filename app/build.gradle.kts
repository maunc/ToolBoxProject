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
        //读取的是项目下的libs.versions.toml文件
        minSdk = libs.versions.minSdkVersion.get().toInt()
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        //读取的是项目下的gradle.properties文件
        versionCode = providers.gradleProperty(propertyNameVersionCodeKey).get().toInt()
        versionName = providers.gradleProperty(propertyNameVersionNameKey).get()
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            // 仅保留 arm64，可显著降低 APK 体积（代价：不支持 32 位设备）
            //noinspection ChromeOsAbiSupport
            abiFilters += arrayListOf("arm64-v8a")
        }
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
            // release 不打 native 调试符号，进一步减小包体
            ndk {
                debugSymbolLevel = "none"
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    // 使用 AAB 发布时，按设备下发资源/ABI，可减少用户下载体积
    bundle {
        abi {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        language {
            enableSplit = true
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

    packaging {
        resources {
            // 排除冲突的 DEPENDENCIES,META-INF文件
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/*.kotlin_module"
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl)
                .outputFileName = variantOutPutFileName
        }
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(project(":library_custom_view"))
    implementation(project(":library_live_eventbus"))
    implementation(project(":library_file_download"))
    implementation(project(":library_web_bridge"))
    implementation(project(":library_un_peek_livedata"))
    implementation(project(":library_video_player"))
    implementation(project(":library_usb_manager"))
    implementation(project(":library_ftp_server"))

    implementation(libs.bundles.android)
    implementation(libs.bundles.smart.refrsh.layout)
    implementation(libs.bundles.exo.player)
    implementation(libs.bundles.ext.glide)
    implementation(libs.ext.gson)

    implementation(libs.ext.baseAdapter)
    implementation(libs.ext.swipeRecyclerView)
    implementation(libs.ext.room.ktx)
    implementation(libs.ext.room.runtime)
    kapt(libs.ext.room.compiler)
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.ext.auto.size)
    implementation(libs.ext.mmkv) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
    }
    implementation(libs.ext.picture.selector)
    implementation(libs.ext.immersionbar)
    implementation(libs.ext.flex.box)
    implementation(libs.ext.ffmpeg.kit)

    // 核心GenAI依赖
//    implementation ("com.google.mediapipe:tasks-genai:0.10.32")
//    // 依赖配套的基础库（版本需和genai一致）
//    implementation ("com.google.mediapipe:tasks-core:0.10.32")
//    // 如需文本生成能力（可选，0.10.29已内置，但显式依赖更稳定）
//    implementation ("com.google.mediapipe:tasks-text:0.10.32")
}
