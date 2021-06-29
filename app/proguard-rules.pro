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
-keep class * implements androidx.navigation.NavArgs
-keep class * extends androidx.navigation.NavArgsLazy
-keep class kotlin.reflect.KClass
-keep class * extends android.os.Bundle
# Workaround for Kotlin Safe Args issue
-if public class ** implements androidx.navigation.NavArgs
-keepclassmembers public class <1> {
    public static ** Companion;
    ** fromBundle(android.os.Bundle);
}
-keepnames class android.os.Bundle
-keep class android.os.Parcelable