pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/repository/public/") }
        maven { // ffmpeg使用
            setUrl("https://github.com/arthenica/ffmpeg-kit/releases")
            content {
                includeGroup("com.arthenica")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/repository/public/") }
        maven { // ffmpeg使用
            setUrl("https://github.com/arthenica/ffmpeg-kit/releases")
            content {
                includeGroup("com.arthenica")
            }
        }
    }
}

rootProject.name = "ToolBox"
include(":app")
include(":library_custom_view") //自定义view库
include(":library_live_eventbus") //LiveEventBus库
include(":library_file_download") //下载库
include(":library_web_bridge") // webToAndroid库
include(":library_un_peek_livedata") //UnPeekLiveData库