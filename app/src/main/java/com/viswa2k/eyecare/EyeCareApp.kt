package com.viswa2k.eyecare

import android.app.Application
import com.viswa2k.eyecare.di.appModule
import com.viswa2k.eyecare.di.databaseModule
import com.viswa2k.eyecare.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class EyeCareApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@EyeCareApp)
            modules(appModule, databaseModule, repositoryModule)
        }
    }
}
