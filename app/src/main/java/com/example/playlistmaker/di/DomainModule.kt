package com.example.playlistmaker.di
import com.example.playlistmaker.features.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.features.player.domain.usecase.PlayerInteractor
import com.example.playlistmaker.features.settings.domain.usecase.SettingsUseCase
import com.example.playlistmaker.features.settings.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.features.settings.domain.usecase.SaveThemeSettingsUseCase
import org.koin.dsl.module

val domainModule = module {
    single {
        SearchTracksUseCase(
        trackRepository = get(),
        searchHistoryRepository = get()
    )     }
    single {
        PlayerInteractor(
        playerRepository = get()
        )
    }
    single {
        SettingsUseCase(
            settingsRepository = get()
        )
    }
    single {
        GetThemeSettingsUseCase(
            settingsRepository = get()
        )
    }
    single {
        SaveThemeSettingsUseCase(
            settingsRepository = get()
        )
    }
}
