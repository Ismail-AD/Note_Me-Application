package com.example.noteme

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//In Android App whenever we need to integrate hilt , we need to define an application class from where
//all the code will be generated...this annotation generate dagger Dependency_Injection code for us.
@HiltAndroidApp
class ApplicationNote : Application() {
}