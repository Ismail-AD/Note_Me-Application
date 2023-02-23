package com.example.noteme.API

import com.example.noteme.Models.NoteRequest
import com.example.noteme.Models.NotesResponse
import retrofit2.Response
import retrofit2.http.*

interface NotesApi {

    @GET("/notes")
    suspend fun getNotes(): Response<List<NotesResponse>> //list to use coz user may have more than one notes


    @POST("/notes") //have to pass arguments as note request instance in the body
    suspend fun createNotes(@Body noteRequest: NoteRequest): Response<NotesResponse>

//    @POST("/notes") //have to pass arguments as note request instance in the body
//    suspend fun createNotesFromOff(@Body noteRequest: List<NoteRequest>): Response<List<NotesResponse>>

    @PUT("/notes/{noteId}") //noteId for each user would be dynamically generated at signup phase
    //we have to use it to update notes of respective user
    //thus we tell retrofit how to get this noteId using 'path'
    suspend fun updateNotes(@Path("noteId") noteId: String, @Body noteRequest: NoteRequest, ): Response<NotesResponse>


    @DELETE("/notes/{noteId}")
    suspend fun delNotes(@Path("noteId") noteId: String): Response<NotesResponse>

}