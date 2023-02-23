package com.example.noteme.API

import com.example.noteme.Utils.TokenManagement
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class InterceptorManager @Inject constructor() : Interceptor {

    @Inject
    lateinit var tokenM: TokenManagement
    override fun intercept(chain: Interceptor.Chain): Response {

        //chain instance will get request for us before request will act
        val request = chain.request().newBuilder() //to rebuild request
        val token = tokenM.getToken()
        request.addHeader("Authorization",
            "Bearer $token") //As we know api standard to add header using Bearer

        return chain.proceed(request.build()) //proceed request is edited now move forward
    }


}