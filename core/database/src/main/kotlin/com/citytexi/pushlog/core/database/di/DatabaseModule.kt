package com.citytexi.pushlog.core.database.di

import android.content.Context
import androidx.room.Room
import com.citytexi.pushlog.core.database.PushlogDatabase
import com.citytexi.pushlog.core.database.dao.SampleItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): PushlogDatabase = Room.databaseBuilder(context, PushlogDatabase::class.java, "starter.db").build()

    @Provides
    fun provideSampleItemDao(database: PushlogDatabase): SampleItemDao = database.sampleItemDao()
}
