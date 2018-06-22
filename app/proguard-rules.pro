# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\android-sdk_r24.4.1-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#################Firebase
# Firebase Authentication
-keepattributes *Annotation*

# Firebase Realtime database
-keepattributes Signature
-keepclassmembers class Entity.** {
  *;
}
#################ButterKnife
-dontwarn butterknife.internal.**

-keep class **$$ViewInjector { *; }

-keepnames class * { @butterknife.InjectView *;}

-dontwarn butterknife.Views$InjectViewProcessor

-dontwarn com.gc.materialdesign.views.**

################org.slf4j.LoggerFactory:
-dontwarn javax.naming.**
-dontwarn javax.servlet.**
-dontwarn org.slf4j.**