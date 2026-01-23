package com.example.kmpbackbone.di

import com.example.data.local.DatabaseContext
import com.example.data.local.IosDatabaseContext
import org.koin.dsl.module

actual fun platformModule() = module {
    single<DatabaseContext> { IosDatabaseContext() }
}
