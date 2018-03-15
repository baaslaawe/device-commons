

-keep class !**.c_master.** { *; }

-keepnames class !**.c_master.** { *; }

-keep class **.keep.** { *; }

#
#
-keepattributes *Annotation*, Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
#
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.**

-dontwarn rx.**
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn com.appsflyer.**
-dontwarn com.google.**
-dontwarn javax.annotation.ParametersAreNonnullByDefault