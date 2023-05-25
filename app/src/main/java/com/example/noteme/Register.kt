package com.example.noteme

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.noteme.Models.UserReq
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.Utils.TokenManagement
import com.example.noteme.databinding.FragmentRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match

@AndroidEntryPoint
class Register : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!! //with get method the pointer value is assigned
    private val viewModelClass by viewModels<ViewModelClass>() //it is kt extensions ---> Rather than calling ViewModelProvider

    // to get instance of viewModel we create Instance using this pattern that will provide instance to us BTS
    @Inject
    lateinit var tokenManagement: TokenManagement


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        if (tokenManagement.getToken() != null)
            findNavController().navigate(R.id.action_register_to_main)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignUp.setOnClickListener {
            //we will check boolean value of pair to perform further operations
            val validationRes = validateUserInput()
            hideMyKeyboard(binding.txtEmail)
            if (validationRes.first) {
                viewModelClass.registerUser(getUserInfo())
            } else {
                binding.txtError.text = validationRes.second
            }
        }
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)

        }
        bind_Observer_to_fragment()

    }

    private fun hideMyKeyboard(view: View) {
        val iManger = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        iManger.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //TO TAKE INPUT
    private fun getUserInfo(): UserReq {
        val username = binding.txtUsername.text.toString()
        val email = binding.txtEmail.text.toString()
        val passWord = binding.txtPassword.text.toString()
        return UserReq(email, passWord, username)
    }

    //TO CHECK CREDENTIALS VALIDATION
    private fun validateUserInput(): Pair<Boolean, String> {
        val userInstance = getUserInfo()
        return viewModelClass.validateIt(userInstance.username,
            userInstance.email,
            userInstance.password,
            false)
    }

    private fun bind_Observer_to_fragment() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelClass.stateflow.collect {
                    binding.loadingBar.isVisible = false
                    when (it) {
                        is NetworkResult.Success -> {
                            tokenManagement.saveToken(it.data!!.token)
                            //to get token from response
                            findNavController().navigate(R.id.action_register_to_main)
                        }
                        is NetworkResult.Failure -> {
                            binding.txtError.text = it.message
                        }
                        is NetworkResult.Loading -> {
                            binding.loadingBar.isVisible = true
                        }
                        is NetworkResult.StateflowInitialization -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}