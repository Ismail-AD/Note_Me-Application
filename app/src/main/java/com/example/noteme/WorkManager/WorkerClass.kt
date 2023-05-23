package com.example.noteme.WorkManager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.noteme.ApplicationNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkerClass(private val context: Context, params:WorkerParameters) :
    Worker(context, params) {
    //Task needs to be executed by work manager
    override fun doWork(): Result {

        //referencing repository to call our function
        val repository = (context as ApplicationNote).repository

        CoroutineScope(Dispatchers.IO).launch{
            repository.functionPerformedWhenInternet()
        }
        return Result.success()
    }

}