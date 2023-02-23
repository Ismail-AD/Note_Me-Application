package com.example.noteme.API

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.noteme.Models.NotesResponse

@Dao
interface dataAccessObject {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListData(notesResponse: List<NotesResponse>)

    @Update
    suspend fun updateData(notesResponse: NotesResponse)

    @Delete
    suspend fun deleteData(notesResponse: NotesResponse)

    @Query("DELETE FROM notesResponse")
    suspend fun deleteAllData()

    @Query("SELECT * FROM notesResponse")
    fun getData(): LiveData<List<NotesResponse>>
    //room db is compatible with livedata so we don't need to mark it as suspend function it will by default run on background thread
}