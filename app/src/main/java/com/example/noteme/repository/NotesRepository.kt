package com.example.noteme.repository

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.noteme.API.NotesApi
import com.example.noteme.API.dataAccessObject
import com.example.noteme.Models.NoteRequest
import com.example.noteme.Models.NotesResponse
import com.example.noteme.Utils.Constants.TAG
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.Utils.roomDatabase
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class NotesRepository @Inject constructor(
    private val notesApi: NotesApi,
    private val dataAccessObject: dataAccessObject,
) {

    //TO RAISE REQUEST AND RECEIVE RESPONSE IN DATA/ERROR_MESSAGE (mutableLiveData Instance)
    private val mutableLiveData = MutableLiveData<NetworkResult<List<NotesResponse>>>()
    val responseLiveData get() = mutableLiveData



    private val mutableStatus = MutableLiveData<NetworkResult<String>>()
    val responseStatusLiveData: LiveData<NetworkResult<String>>
        get() = mutableStatus

    fun getNotesOff() = dataAccessObject.getData()

    suspend fun delAll(){
        dataAccessObject.deleteAllData()
    }


    suspend fun getNotes() {
        mutableLiveData.postValue(NetworkResult.Loading())

        val response = notesApi.getNotes()
        if (response.isSuccessful && response.body() != null) {
            mutableLiveData.postValue(NetworkResult.Success(response.body()!!))
            dataAccessObject.insertListData(response.body()!!)
        } else if (response.errorBody() != null) {

//               we get error in form of json so we have to parse json object into java/kotlin object
            val errorOBJ = JSONObject(response.errorBody()!!.charStream().readText())

            //it will return read and return string object(message)
            mutableLiveData.postValue(NetworkResult.Failure(null,errorOBJ.getString("message")))//json object converted the message into string
        } else {
            mutableLiveData.postValue(NetworkResult.Failure(null,"Something Went Wrong !!! "))
        }
    }


    suspend fun createNotes(noteRequest: NoteRequest) {
        mutableStatus.postValue(NetworkResult.Loading())
        val response = notesApi.createNotes(noteRequest)
        handleResponse(response, "Notes Successfully Inserted !")
    }


    suspend fun updateNotes(noteId: String, noteRequest: NoteRequest) {
        mutableStatus.postValue(NetworkResult.Loading())
        val response = notesApi.updateNotes(noteId, noteRequest)
        handleResponse(response, "Notes Successfully Updated !")
    }

    suspend fun deleteNotes(noteId: String) {
        mutableStatus.postValue(NetworkResult.Loading())
        val response = notesApi.delNotes(noteId)
        handleResponse(response, "Notes Successfully Deleted !")
    }

    private fun handleResponse(response: Response<NotesResponse>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            mutableStatus.postValue(NetworkResult.Success(message))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            mutableStatus.postValue(NetworkResult.Failure(null,errorObj.getString("message")))
        } else {
            mutableStatus.postValue(NetworkResult.Failure(null,"Something Went Wrong !!! "))
        }
    }


}

