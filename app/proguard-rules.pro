# =====================================================
# TreeTask ProGuard / R8 Rules
# =====================================================

# ==================== DEBUG ====================
# Giữ thông tin dòng code để Crashlytics có thể hiển thị stacktrace chính xác
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ==================== KOTLINX SERIALIZATION ====================
# Dự án dùng @Serializable + @SerialName để parse JSON từ API
# R8 KHÔNG ĐƯỢC đổi tên hoặc xóa các field này, nếu không JSON sẽ parse sai hoàn toàn

# Giữ toàn bộ annotation của kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Giữ các class Serializer được tạo tự động
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Giữ tất cả data class có annotation @Serializable trong project
-keep,includedescriptorclasses class com.doannd3.treetask.core.network.model.**$$serializer { *; }
-keepclassmembers class com.doannd3.treetask.core.network.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.doannd3.treetask.core.network.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Giữ tất cả response/request model — đây là các class mà JSON cần map vào
-keep class com.doannd3.treetask.core.network.model.response.** { *; }
-keep class com.doannd3.treetask.core.network.model.request.** { *; }

# ==================== RETROFIT ====================
# Retrofit tạo implementation từ interface lúc runtime
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Giữ các interface Service (Retrofit dùng reflection để đọc annotation)
-keep,allowobfuscation interface com.doannd3.treetask.core.network.service.** { *; }

# ==================== OKHTTP ====================
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ==================== ROOM ====================
# Room dùng annotation processing, cần giữ các Entity và Dao
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# ==================== HILT / DAGGER ====================
# Hilt dùng code generation, thường tự xử lý tốt với R8
# Nhưng cần giữ các annotation
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclassmembers class * {
    @javax.inject.* <fields>;
    @javax.inject.* <methods>;
    @dagger.* <fields>;
    @dagger.* <methods>;
}

# ==================== FIREBASE ====================
# Firebase SDK tự bundle rules, nhưng cần giữ custom event class
-keep class com.doannd3.treetask.core.analytics.AnalyticsEvent { *; }
-keep class com.doannd3.treetask.core.analytics.Param { *; }

# ==================== DOMAIN MODEL ====================
# Giữ các model trong core:model (dùng khắp app)
-keep class com.doannd3.treetask.core.model.** { *; }

# ==================== COROUTINES ====================
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ==================== ENUM ====================
# Giữ tất cả enum (hay bị R8 tối ưu hóa sai)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}