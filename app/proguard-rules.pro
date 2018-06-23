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
-keepclassmembers class com.teamcore.android.core.Entity.** {
  *;
}
-keepclassmembers class com.teamcore.android.core.MessageActivity.util.** {
  *;
}


##############Firebase Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

####### Butterknife

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**

-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
-keep class butterknife.*
-keep class **_ViewBinding { *; }
-keep class **$$ViewBinder { *; }
-keep class **$ViewHolder { *; }
-keep class butterknife.**$Finder { *; }

-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

################org.slf4j.LoggerFactory:
-dontwarn javax.naming.**
-dontwarn javax.servlet.**
-dontwarn org.slf4j.**

-keep class com.gun0912.tedpermission.** { *; }

-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

######### KEEP ANDROID SUPPORT V7 AND DESIGN
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

######### lib
-keep class com.google.** { *; }
-keep class com.github.** { *; }
-keep class org.apache.** { *; }
-keep class com.android.** { *; }
-keep class junit.** { *; }
-keep class q.** { *; }
-keep class org.jdeferred.**  { *; }

# For Google Play Services
-keep public class com.google.android.gms.ads.**{
   public *;
}

-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.ads.** {*;}

################Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}