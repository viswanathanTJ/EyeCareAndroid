package com.viswa2k.eyecare.di

import androidx.room.Room
import com.viswa2k.eyecare.data.db.EyeCareDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            EyeCareDatabase::class.java,
            "eye_care_database"
        ).build()
    }
    single { get<EyeCareDatabase>().breakEventDao() }
    single { get<EyeCareDatabase>().dailyStatsDao() }
}
