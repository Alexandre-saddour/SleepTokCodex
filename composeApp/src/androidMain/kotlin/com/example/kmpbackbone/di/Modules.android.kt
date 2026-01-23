package com.example.kmpbackbone.di

import com.example.data.local.DatabaseContext
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    single<DatabaseContext> { androidContext() }
}
