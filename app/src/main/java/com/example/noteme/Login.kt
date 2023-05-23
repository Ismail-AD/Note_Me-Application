package com.example.noteme

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.noteme.Models.UserReq
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.Utils.TokenManagement
import com.example.noteme.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Login : Fragment() {

    private var _bindingLogin: FragmentLoginBinding? = null
    private val binding get() = _bindingLogin!!
    private val viewModelClass by viewModels<ViewModelClass>()

    @Inject
    lateinit var tokenManagement: TokenManagement
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _bindingLogin = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            findNavController().popBackStack() //to remove the top stack layer that is login to go back to register layer
        }

        binding.btnLogin.setOnClickListener {
            //we will check boolean value of pair to perform further operations
            val validationRes = validateUserInput()
            hideMyKeyboard(binding.txtEmail)
            if (validationRes.first) {
                viewModelClass.loginUser(getUserInfo())
            } else {
                binding.txtError.text = validationRes.second
            }
        }

        //Observer implemented for this fragment(which is lifecycle owner) on Live data instance created in view model
        //if anything updated in live data it will directly trigger observer lambda function
        bind_Observer_to_fragment()
    }

    private fun hideMyKeyboard(view: View) {
        val iManger = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        iManger.hideSoftInputFromWindow(view.windowToken,0)
    }

    //TO TAKE INPUT
    private fun getUserInfo(): UserReq {

        val email = binding.txtEmail.text.toString()
        val passWord = binding.txtPassword.text.toString()
        return UserReq(email, passWord, "")
    }

    //TO CHECK CREDENTIALS VALIDATION
    private fun validateUserInput(): Pair<Boolean, String> {
        val userInstance = getUserInfo()
        return viewModelClass.validateIt(userInstance.username,
            userInstance.email,
            userInstance.password,
            true)
    }

    private fun bind_Observer_to_fragment() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelClass.stateflow.collect {
                    binding.loadingBar2.isVisible = false
                    when (it) {
                        is NetworkResult.Success -> {
                            tokenManagement.saveToken(it.data!!.token) //to get token from response
                            findNavController().navigate(R.id.action_login_to_main)
                        }
                        is NetworkResult.Failure -> {
                            binding.txtError.text = it.message
                        }
                        is NetworkResult.Loading -> {
                            binding.loadingBar2.isVisible = true
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingLogin = null
    }


}