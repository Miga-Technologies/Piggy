# Enum preservation - Essential for preventing NoSuchMethodException
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep all enum classes and their methods
-keep public enum * {
    **[] $VALUES;
    public *;
}

# iText PDF
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# BouncyCastle
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
-keep class com.itextpdf.bouncycastleconnector.** { *; }
-keep class com.itextpdf.commons.bouncycastle.** { *; }

# Jackson - Regras mais específicas
-keep class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.**
-keepnames class com.fasterxml.jackson.** { *; }
-keepclassmembers public final enum com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility {
    public static final com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility *;
}

# Firebase and Google Play Services
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep all classes with native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all public classes in main packages
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# AWT classes (para Android, essas são problemáticas)
-dontwarn java.awt.**
-dontwarn javax.imageio.**

# Reflexão - para evitar problemas com classes dinâmicas
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations