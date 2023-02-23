package com.example.noteme.Utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteme.API.dataAccessObject
import com.example.noteme.Models.NoteRequest
import com.example.noteme.Models.NotesResponse

@Database(entities = [NotesResponse::class], version = 1, exportSchema = false)
abstract class roomDatabase : RoomDatabase() {

    abstract fun daoObjectProvider(): dataAccessObject
    //we will create object using hilt so that only one instance is created and used
}