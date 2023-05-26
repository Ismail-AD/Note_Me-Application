package com.example.noteme.API

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.noteme.Models.NoteRequestOffline
import com.example.noteme.Models.NotesResponse

@Dao
interface dataAccessObject {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListData(notesResponse: List<NotesResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(notesResponse: NotesResponse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataOffline(noteRequestOffline: NoteRequestOffline)

    @Update
    suspend fun updateData(notesResponse: NotesResponse)

    @Update
    suspend fun updateDataOffline(noteRequestOffline: NoteRequestOffline)

    @Delete
    suspend fun deleteDataOffline(noteRequestOffline: NoteRequestOffline)

    @Query("DELETE FROM NotesResponse")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM NotesResponse")
    suspend fun getData(): List<NotesResponse>
    //room db is compatible with livedata so we don't need to mark it as suspend function it will by default run on background thread

    @Query("SELECT * FROM NoteRequestOffline")
    fun getDataOffline(): List<NoteRequestOffline>
}
