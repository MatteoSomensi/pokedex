package com.example.pokedex.core.di

import javax.inject.Qualifier

/** Qualifies the [kotlinx.coroutines.CoroutineScope] that lives for the application process. */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
