# Add project specific ProGuard rules here.

# Preserve line numbers for stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Koin
-keep class org.koin.** { *; }
-keep class com.viswa2k.eyecare.di.** { *; }

# Keep all app classes used by Koin DI (ViewModels, Services, Repositories)
-keep class com.viswa2k.eyecare.ui.** { *; }
-keep class com.viswa2k.eyecare.service.** { *; }
-keep class com.viswa2k.eyecare.receiver.** { *; }
-keep class com.viswa2k.eyecare.domain.** { *; }
-keep class com.viswa2k.eyecare.data.repository.** { *; }

# Glance
-keep class androidx.glance.** { *; }
-keep class com.viswa2k.eyecare.widget.** { *; }

# Keep data classes used with DataStore
-keep class com.viswa2k.eyecare.data.datastore.** { *; }
-keep class com.viswa2k.eyecare.data.db.entity.** { *; }
-keep class com.viswa2k.eyecare.data.db.dao.** { *; }
-keep class com.viswa2k.eyecare.data.db.EyeCareDatabase { *; }
