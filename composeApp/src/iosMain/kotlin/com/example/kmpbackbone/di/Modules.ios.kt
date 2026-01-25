package com.example.kmpbackbone.di

import com.example.data.local.DatabaseContext
import org.koin.dsl.module

actual fun platformModule(databaseContext: DatabaseContext) = module {
    single<DatabaseContext> { databaseContext }
}
