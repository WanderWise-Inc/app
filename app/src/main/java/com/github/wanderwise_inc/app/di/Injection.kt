package com.github.wanderwise_inc.app.di

import com.github.wanderwise_inc.app.BuildConfig

object Injection {
    val moduleProvider: ModuleProvider = if (isInTestMode()) EndToEndModule else AppModule

    private fun isInTestMode(): Boolean {
        return BuildConfig.DEBUG
    }
}