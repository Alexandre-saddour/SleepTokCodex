package com.example.kmpbackbone.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Initialize Koin DI for shared module.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        networkModule,
        dataModule,
        domainModule,
        presentationModule,
        platformModule()
    )
    // Initialize Napier for logging
    Napier.base(DebugAntilog())
}

expect fun platformModule(): Module

val networkModule = module {

}

val dataModule = module {
}

val domainModule = module {
}

val presentationModule = module {
}
