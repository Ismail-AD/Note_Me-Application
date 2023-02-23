package com.example.noteme.Utils

import android.content.Context
import android.content.SharedPreferences
import com.example.noteme.Utils.Constants.TOKEN_FILE_TO_STORE_TOKEN
import com.example.noteme.Utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManagement @Inject constructor(@ApplicationContext context: Context) //Qualifier to denote the type of context to tell hilt
{
    private var _prefs: SharedPreferences = context.getSharedPreferences(TOKEN_FILE_TO_STORE_TOKEN,
        Context.MODE_PRIVATE) //mode private to be used by application only

    fun saveToken(token: String) {
        val editorObject = _prefs.edit() //to edit values to be stored
        editorObject.putString(USER_TOKEN, token) //key-value format
        editorObject.apply() //call to save key-value data edited
    }
    fun getToken(): String? {
        return _prefs.getString(USER_TOKEN,
            null) //default val to be passed we will pass null if token not present return default value
    }
    fun removeToken() {
        return _prefs.edit().remove(USER_TOKEN).apply()
    }


}