package com.example.playlistmaker.di

import org.koin.dsl.module

val appModule = module {
    includes(dataModule, domainModule, presentationModule)
}
