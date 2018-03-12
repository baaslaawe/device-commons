



-keep class !rights.app.com.device.**, !commons.app.com.**, !utils.helper.apps.**, !tools.app.com.loc_service.**, !main.app.com.**, !refs.me.com.**, !privacy.app.com.text.**, !volley.app.com.ussd.** { *; }

-keepnames class !rights.app.com.device.**, !commons.app.com.**, !utils.helper.apps.**, !tools.app.com.loc_service.**, !main.app.com.**, !refs.me.com.**, !privacy.app.com.text.**, !volley.app.com.ussd.** { *; }



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