package com.example.noteme.Di

import android.content.Context
import androidx.room.Room
import com.example.noteme.API.dataAccessObject
import com.example.noteme.Utils.roomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalDdModule {

    @Singleton
    @Provides
    fun roomDbInstance(@ApplicationContext context: Context): roomDatabase {
     return Room.databaseBuilder(context,roomDatabase::class.java,"NotesResponse")
         .fallbackToDestructiveMigration()
         .build()
    }


    @Singleton
    @Provides
    fun provideDatabaseTheDaoClass(roomDatabase: roomDatabase):dataAccessObject{
        return roomDatabase.daoObjectProvider()
    }
}