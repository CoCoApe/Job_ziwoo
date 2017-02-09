# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ljc/Library/Android/sdk/tools/proguard/proguard-android.txt
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


-keep public class * extends android.content.BroadcastReceiver
-dontwarn com.tendcloud.tenddata.**
-keep class com.tendcloud.** {*;}
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}
-keepattributes EnclosingMethod
#testIn
#-libraryjars /libs/tncrash.jar
-dontwarn com.testin.agent.**
-keep  class  com.testin.agent.** {*;}
-keepattributes Exceptions,InnerClasses
-keepattributes Signature

# RongCloud SDK
-keep class io.rong.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**

#GeTui
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }

#JiGuang
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}

#第三方包
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

-dontwarn org.**
-keep class org.**{*;}

-dontwarn com.handmark.**
-keep class com.handmark.**{*;}

-dontwarn com.viewpagerindicator.**
-keep class com.viewpagerindicator.**{*;}

-dontwarn com.alipay.**
-keep class com.alipay.**{*;}

#默认
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#-keepclasseswithmembernames class * {
#    native ;
#}

-keepclasseswithmembers class * {
    public void *(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public void *(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
  }
-keep class * implements java.io.Serializable {
    public static final android.os.Parcelable$Creator *;
  }

-keep class cn.com.zhiwoo.bean.**{*;}
