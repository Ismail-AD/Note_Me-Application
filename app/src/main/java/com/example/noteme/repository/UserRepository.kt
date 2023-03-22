package com.example.noteme.repository

import com.example.noteme.API.UserApi
import com.example.noteme.Models.UserReq
import com.example.noteme.Models.UserResponse
import com.example.noteme.Utils.NetworkResult
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response
import javax.inject.Inject


//Creating Repository to Interact with Database
class UserRepository @Inject constructor(private val userApi: UserApi) {

    //Live data to manage View Model Data and help in manage or update online data as it is aware of activity/fragment Lifecycle

    private val _userResponseLD = MutableStateFlow<NetworkResult<UserResponse>>(NetworkResult.StateflowInitialization())
    val stateflowInstance: StateFlow<NetworkResult<UserResponse>>
        get() = _userResponseLD

    suspend fun signUp(userReq: UserReq) {
        _userResponseLD.emit(NetworkResult.Loading())
        val response = userApi.signup(userReq)
        handleresponse(response)
    }


    suspend fun signIp(userReq: UserReq) {
        _userResponseLD.emit(NetworkResult.Loading())
        val response = userApi.signin(userReq)
        handleresponse(response)
    }

    private suspend fun handleresponse(response: Response<UserResponse>) {

        if (response.isSuccessful && response.body() != null) {
            _userResponseLD.emit(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val parser = JsonParser()
            //The charStream() function is used to convert the response body into a character stream, which can be read as text.
            // The readText() function is used to read the JSON string from the character stream that was created from the response body.
            // Once the JSON string is read, it can be passed to the JsonParser instance to parse it into a JSON object.
            val errorDetails = parser.parse(response.errorBody()!!.charStream().readText()).asString
            _userResponseLD.emit(NetworkResult.Failure(null, errorDetails))
        } else {
            _userResponseLD.emit(NetworkResult.Failure(null, "Something Went Wrong !!! "))
        }
    }

}