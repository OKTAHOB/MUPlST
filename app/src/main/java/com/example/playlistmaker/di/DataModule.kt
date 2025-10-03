package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "favorites.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().favoritesDao() }
}
