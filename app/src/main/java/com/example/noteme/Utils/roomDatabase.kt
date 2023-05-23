package com.example.noteme.Utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteme.API.dataAccessObject
import com.example.noteme.Models.NoteRequest
import com.example.noteme.Models.NoteRequestOffline
import com.example.noteme.Models.NotesResponse

// RoomDatabase allows you to define your database schema using annotations and access your data using data access objects (DAOs).
@Database(entities = [NotesResponse::class,NoteRequestOffline::class], version = 1, exportSchema = false)
abstract class roomDatabase : RoomDatabase() {

    abstract fun daoObjectProvider():dataAccessObject //you need an abstract method to perform queries on data store in DB.
    //we will create object using hilt so that only one instance is created and used
}