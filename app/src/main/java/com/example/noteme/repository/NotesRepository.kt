package com.example.noteme.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.noteme.API.NotesApi
import com.example.noteme.API.dataAccessObject
import com.example.noteme.Models.NoteRequest
import com.example.noteme.Models.NoteRequestOffline
import com.example.noteme.Models.NotesResponse
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.Utils.NetworkResult.Loading
import com.example.noteme.Utils.NetworkStatus
import com.example.noteme.Utils.roomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class NotesRepository @Inject constructor(
    private val notesApi: NotesApi,
    private val dataAccessObject: dataAccessObject,
    @ApplicationContext private val context: Context,
) {

    private val mutablestateflow =
        MutableStateFlow<NetworkResult<List<NotesResponse>>>(NetworkResult.StateflowInitialization())
    val responseStateFlow: StateFlow<NetworkResult<List<NotesResponse>>> get() = mutablestateflow


    private val mutableStatus =
        MutableStateFlow<NetworkResult<String>>(NetworkResult.StateflowInitialization())
    val responseStatusStateFLow: StateFlow<NetworkResult<String>>
        get() = mutableStatus

    private val LiveDataOfRoom = MutableLiveData<List<NotesResponse>>()
    val getRoomData: LiveData<List<NotesResponse>>
        get() = LiveDataOfRoom

    suspend fun delAll() = dataAccessObject.deleteAllNotes()

    suspend fun getNotes() {
        if (NetworkStatus.isNetworkAvailable(context)) {
            mutableStatus.emit(Loading())
            val response = notesApi.getNotes()
            val dataForRoom = response.body()!!
            if (response.isSuccessful && response.body() != null) {
                dataAccessObject.deleteAllNotes()
                dataAccessObject.insertListData(dataForRoom)
                mutablestateflow.emit(NetworkResult.Success(response.body()!!))
            } else if (response.errorBody() != null) {

//          we get error in form of json so we have to parse json object into java/kotlin object
                val errorOBJ = JSONObject(response.errorBody()!!.charStream().readText())

                //it will return read and return string object(message)
                mutablestateflow.emit(NetworkResult.Failure(null,
                    errorOBJ.getString("message")))//json object converted the message into string
            } else {
                mutablestateflow.emit(NetworkResult.Failure(null, "Something Went Wrong !!! "))
            }
        } else {
            val response = dataAccessObject.getData()
            LiveDataOfRoom.postValue(response)
        }

    }

    suspend fun createNotes(noteRequest: NoteRequest) {
        if (NetworkStatus.isNetworkAvailable(context)) {
            mutableStatus.emit(Loading())
            val response = notesApi.createNotes(noteRequest)
            dataAccessObject.insertData(response.body()!!)
            handleResponse(response, "Notes Successfully Inserted !")
        } else {
            dataAccessObject
                .insertDataOffline(NoteRequestOffline(noteRequest.description,
                    noteRequest.title,
                    false))
            mutableStatus.emit(NetworkResult.Success("Notes Created Offline"))
            Toast.makeText(context, "Displayed After On-Network", Toast.LENGTH_SHORT).show();
        }

    }


    suspend fun updateNotes(noteId: String, noteRequest: NoteRequest) {
        if (NetworkStatus.isNetworkAvailable(context)) {
            mutableStatus.emit(Loading())
            val response = notesApi.updateNotes(noteId, noteRequest)
            handleResponse(response, "Notes Successfully Updated !")
        } else {
            Toast.makeText(context, "Requires Internet Connection !", Toast.LENGTH_SHORT).show();
        }

    }

    suspend fun deleteNotes(noteId: String) {
        if (NetworkStatus.isNetworkAvailable(context)) {
            mutableStatus.emit(Loading())
            val response = notesApi.delNotes(noteId)
            handleResponse(response, "Notes Successfully Deleted !")
        } else {
            Toast.makeText(context, "Requires Internet Connection !", Toast.LENGTH_SHORT).show();
        }
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

    suspend fun functionPerformedWhenInternet() {
        val notesOffline = dataAccessObject.getDataOffline()
        val listToPush = ArrayList<NoteRequestOffline>()
        notesOffline.forEach {   //Only push newly Created Notes During No Internet.
            if (!it.syncedOrNot) {
                listToPush.add(it)
            } else {
                dataAccessObject.deleteDataOffline(it)
            }
        }
        listToPush.forEach {
            val response = notesApi.createNotes(NoteRequest(it.description, it.title))
            handleResponse(response, "Notes Synced Successfully !")

            //Now update the pushed notes as synced in offline db as they don't need to be pushed in next event
            dataAccessObject
                .updateDataOffline(NoteRequestOffline(it.description, it.title, true))
        }
    }
}

