package com.example.brewtrack.di

import android.content.Context
import com.example.brewtrack.model.AppDatabase
import com.example.brewtrack.model.CoffeeLogDao
import com.example.brewtrack.model.CoffeeLogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCoffeeLogDao(appDatabase: AppDatabase): CoffeeLogDao {
        return appDatabase.coffeeLogDao()
    }

    @Provides
    @Singleton
    fun provideCoffeeLogRepository(coffeeLogDao: CoffeeLogDao): CoffeeLogRepository {
        return CoffeeLogRepository(coffeeLogDao)
    }
}
