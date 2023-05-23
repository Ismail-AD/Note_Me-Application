package com.example.noteme

import android.app.Application
import androidx.work.*
import com.example.noteme.WorkManager.WorkerClass
import com.example.noteme.repository.NotesRepository
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

//In Android App whenever we need to integrate hilt , we need to define an application class from where
//all the code will be generated...this annotation generate dagger Dependency_Injection code for us.
@HiltAndroidApp
class ApplicationNote : Application() {

    //Referencing repository object so that worker can access it by context
    @Inject
    lateinit var repository: NotesRepository

    override fun onCreate() {
        super.onCreate()
        WorkerRequest()
    }

    private fun WorkerRequest() {
        //specify that the task requires an internet connection.
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val request = PeriodicWorkRequest.Builder(WorkerClass::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constraints).build()

        //By enqueueUniquePeriodicWork you will ensure that only one instance of your work request with that name can be active at a time.
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("SyncingProcess", ExistingPeriodicWorkPolicy.KEEP, request)
    }
}