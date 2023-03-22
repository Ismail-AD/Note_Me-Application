package com.example.noteme.repository

import com.example.noteme.API.NotesApi
import com.example.noteme.API.dataAccessObject
import com.example.noteme.Models.NoteRequest
import com.example.noteme.Models.NotesResponse
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.Utils.NetworkResult.Loading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class NotesRepository @Inject constructor(
    private val notesApi: NotesApi,
    private val dataAccessObject: dataAccessObject,
) {

    private val mutablestateflow = MutableStateFlow<NetworkResult<List<NotesResponse>>>(NetworkResult.StateflowInitialization())
    val responseStateFlow: StateFlow<NetworkResult<List<NotesResponse>>> get() = mutablestateflow


    private val mutableStatus = MutableStateFlow<NetworkResult<String>>(NetworkResult.StateflowInitialization())
    val responseStatusStateFLow: StateFlow<NetworkResult<String>>
        get() = mutableStatus

    fun getNotesOff() = dataAccessObject.getData()

    suspend fun delAll() {
        dataAccessObject.deleteAllData()
    }


    suspend fun getNotes() {
        mutableStatus.emit(Loading())
        val response = notesApi.getNotes()
        if (response.isSuccessful && response.body() != null) {
            mutablestateflow.emit(NetworkResult.Success(response.body()!!))
            dataAccessObject.insertListData(response.body()!!)
        } else if (response.errorBody() != null) {

//          we get error in form of json so we have to parse json object into java/kotlin object
            val errorOBJ = JSONObject(response.errorBody()!!.charStream().readText())

            //it will return read and return string object(message)
            mutablestateflow.emit(NetworkResult.Failure(null,
                errorOBJ.getString("message")))//json object converted the message into string
        } else {
            mutablestateflow.emit(NetworkResult.Failure(null, "Something Went Wrong !!! "))
        }
    }


    suspend fun createNotes(noteRequest: NoteRequest) {
        mutableStatus.emit(Loading())
        val response = notesApi.createNotes(noteRequest)
        handleResponse(response, "Notes Successfully Inserted !")
    }


    suspend fun updateNotes(noteId: String, noteRequest: NoteRequest) {
        mutableStatus.emit(Loading())
        val response = notesApi.updateNotes(noteId, noteRequest)
        handleResponse(response, "Notes Successfully Updated !")
    }

    suspend fun deleteNotes(noteId: String) {
        mutableStatus.emit(Loading())
        val response = notesApi.delNotes(noteId)
        handleResponse(response, "Notes Successfully Deleted !")
    }

    private suspend fun handleResponse(response: Response<NotesResponse>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            mutableStatus.emit(NetworkResult.Success(message))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            mutableStatus.emit(NetworkResult.Failure(null, errorObj.getString("message")))
        } else {
            mutableStatus.emit(NetworkResult.Failure(null, "Something Went Wrong !!! "))
        }
    }


}

