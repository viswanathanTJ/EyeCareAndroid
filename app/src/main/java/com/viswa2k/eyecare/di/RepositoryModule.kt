package com.viswa2k.eyecare.di

import com.viswa2k.eyecare.data.repository.BreakRepository
import com.viswa2k.eyecare.data.repository.BreakRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<BreakRepository> { BreakRepositoryImpl(get(), get()) }
}
