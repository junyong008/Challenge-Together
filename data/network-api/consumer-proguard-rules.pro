# Model
-keep class com.yjy.data.network.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*,EnclosingMethod,InnerClasses
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}

-keepclasseswithmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

-keep class kotlinx.serialization.internal.** { *; }