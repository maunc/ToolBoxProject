# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class android.content.Intent { *; }
-keep class android.os.Bundle { *; }
-keep class android.content.ComponentName { *; }

# 保留所有 Activity 及其子类（包括匿名内部类/泛型 Activity）
-keep public class * extends android.app.Activity { *; }
-keep public class * extends androidx.appcompat.app.AppCompatActivity { *; }
-keep public class * extends android.view.ContextThemeWrapper { *; }
-keep public class * extends android.app.Activity {
    public <init>();
}

# 保留所有 Service 及其子类
-keep public class * extends android.app.Service { *; }
-keep public class * extends androidx.core.app.JobIntentService { *; }
-keep public class * extends android.app.Service {
    public <init>();
}

# 保留所有 BroadcastReceiver 及其子类
-keep public class * extends android.content.BroadcastReceiver { *; }
-keep public class * extends android.content.BroadcastReceiver {
    public <init>();
}

# 保留所有 ContentProvider 及其子类
-keep public class * extends android.content.ContentProvider { *; }
-keep class * extends android.content.ContentProvider {
    public <init>();
}


-keep class * extends androidx.databinding.ViewDataBinding { *; }
-keep class androidx.databinding.** { *; }

# 保留泛型类/方法的签名（避免泛型类型被擦除导致反射异常）
-keepattributes Signature, InnerClasses, EnclosingMethod