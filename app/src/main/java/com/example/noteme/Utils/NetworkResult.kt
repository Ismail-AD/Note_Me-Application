package com.example.noteme.Utils

import androidx.lifecycle.LiveData
import com.example.noteme.Models.NotesResponse

sealed class NetworkResult<T>(val data: T? =null, val message:String?=null) {
    //Sealed class restrict the new extension classes or classes inherited in other files

    class Success<T>(data: T):NetworkResult<T>(data)
    class Failure<T>(data:T?=null,message: String):NetworkResult<T>(data,message)
    class Loading<T>:NetworkResult<T>()
}