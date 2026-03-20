plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.liulishuo.filedownloader"
    compileSdk = libs.versions.compileSdkVerison.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdkVersion.get().toInt()
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
        sourceSets {
            getByName("main") {
                aidl.srcDirs("src/main/aidl") // Add other paths if needed
            }
        }

        buildFeatures {
            aidl = true
            buildConfig = true
        }
    }
}

dependencies {
}