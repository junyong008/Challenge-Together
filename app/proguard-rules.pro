# AppsFlyer SDK ProGuard Rule
-keep class com.appsflyer.** { *; }
-keep class kotlin.jvm.internal.** { *; }

# Google Play Install Referrer ProGuard Rule
-keep public class com.android.installreferrer.** { *; }