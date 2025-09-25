plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.liulishuo.filedownloader"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 35
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



