package com.example.noteme

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteme.Models.UserReq
import com.example.noteme.Models.UserResponse
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelClass @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val liveData: LiveData<NetworkResult<UserResponse>>
        get() = userRepository.liveDataInstance

    fun registerUser(userReq: UserReq) {
        //As long as ViewModel Class is active Scope will maintain all background work / functions
        viewModelScope.launch {
            userRepository.signUp(userReq) //as suspended function so should be launch in a coroutine
        }
    }

    fun loginUser(userReq: UserReq) {
        //As long as ViewModel Class is active Scope will maintain all background work / functions
        viewModelScope.launch {
            userRepository.signIp(userReq) //as suspended function so should be launch in a coroutine
        }
    }

    fun validateIt(
        username: String,
        emailadd: String,
        password: String,
        isLogin: Boolean,
    ): Pair<Boolean, String> {
        var resultForPair = Pair(true, "")
        //if Login then check remaining
        if ((!isLogin && TextUtils.isEmpty(username)) || TextUtils.isEmpty(emailadd) || TextUtils.isEmpty(
                password)
        )
        {
            resultForPair = Pair(false, "Please Provide Complete Credentials !")
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailadd).matches()){
            resultForPair = Pair(false, "Please Provide Valid Email !")
        }
        else if (password.length <= 5)
        {
            resultForPair = Pair(false, "Please Provide Password having more than 6 characters !")
        }

        return resultForPair

    }
}