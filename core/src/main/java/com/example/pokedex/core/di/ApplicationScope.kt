package com.example.pokedex.core.di

import javax.inject.Qualifier

/**
 * Qualifier per il CoroutineScope dell'applicazione.
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
