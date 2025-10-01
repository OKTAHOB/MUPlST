package com.example.playlistmaker.di

import com.example.playlistmaker.features.media.di.mediaModule
import org.koin.dsl.module

val appModule = module {
    includes(dataModule, searchModule,  playerModule, mediaModule, settingsModule)
}
