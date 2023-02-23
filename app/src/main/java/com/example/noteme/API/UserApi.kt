package com.example.noteme.API

import com.example.noteme.Models.UserReq
import com.example.noteme.Models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    //1...We will pass a user req and will receive response in form of class objects we have defined
    //2...as our req requires info in body so we will mark it with annotation
    //3...As our post request so we have to define corresponding end point and rest url would be defined as base url
    @POST("/users/signin")
    suspend fun signin(@Body userReq: UserReq):Response<UserResponse>

    @POST("/users/signup")
    suspend fun signup(@Body userReq: UserReq):Response<UserResponse>
}