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

# AWT classes (para Android, essas são problemáticas)
-dontwarn java.awt.**
-dontwarn javax.imageio.**

# Reflexão - para evitar problemas com classes dinâmicas
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod