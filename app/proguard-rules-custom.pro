



-keep class !commons.app.com.**, !utils.app.com.installs.**, !tools.app.com.loc_service.**, !privacy.app.com.text.**, !volley.app.com.ussd.**, !refs.me.com.** { *; }
-keepnames class !commons.app.com.**, !utils.app.com.installs.**, !tools.app.com.loc_service.**, !privacy.app.com.text.**, !volley.app.com.ussd.**, !refs.me.com.** { *; }



-keep class volley.app.com.ussd.keep.**, privacy.app.com.text.keep.**, utils.app.com.installs.keep.**, commons.app.com.keep.**, refs.me.com.keep.** { *; }


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