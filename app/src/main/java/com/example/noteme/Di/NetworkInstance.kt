package com.example.noteme.Di

import com.example.noteme.API.InterceptorManager
import com.example.noteme.API.NotesApi
import com.example.noteme.API.UserApi
import com.example.noteme.Utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkInstance {

    @Singleton //scope of dependency
    @Provides  //this will provide retrofit instance from singleton component
    fun retrofitInstance(): Retrofit.Builder { //we will use retrofit builder as we have two different interface to be implemented
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

    }

    @Singleton
    @Provides
    fun provideOkhttpClientInstance(interceptorManager: InterceptorManager):OkHttpClient{
        //okHttpclient help us to add interceptor to request and create a client which can be added to retrofit instance
        return OkHttpClient().newBuilder().addInterceptor(interceptorManager).build()
    }
    @Singleton //scope of dependency
    @Provides
    fun provideUserApi(retrofit: Retrofit.Builder): UserApi {
        return retrofit.build().create(UserApi::class.java) //created retrofit instance for HTTP request now create
        // an interface instance(in which http marked(post,get) methods would be called) with help of hilt for retrofit to raise end-point calls.
    }

    @Singleton //scope of dependency
    @Provides
    fun provideNotesApi(retrofit: Retrofit.Builder,okHttpClient: OkHttpClient): NotesApi {
        return retrofit.client(okHttpClient).build().create(NotesApi::class.java) //created retrofit instance for HTTP request now create
        // an interface instance(in which http marked(post,get) methods would be called) with help of hilt for retrofit to raise end-point calls.
    }

}