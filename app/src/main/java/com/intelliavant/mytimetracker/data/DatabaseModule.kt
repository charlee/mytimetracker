package com.intelliavant.mytimetracker.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideWorkTypeDao(appDatabase: AppDatabase): WorkTypeDao {
        return appDatabase.workTypeDao()
    }

    @Provides
    fun provideWorkDao(appDatabase: AppDatabase): WorkDao {
        return appDatabase.workDao()
    }
}