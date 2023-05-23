package com.example.noteme


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteme.API.NotesApi
import com.example.noteme.Models.NotesResponse
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.Utils.TokenManagement
import com.example.noteme.Utils.roomDatabase
import com.example.noteme.databinding.FragmentMainBinding
import com.example.noteme.Utils.NetworkStatus
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class main : Fragment() {

    private var fragmentMainBinding: FragmentMainBinding? = null
    private val _binding get() = fragmentMainBinding!!
    private val notesViewModel by viewModels<notesViewModel>()
    private lateinit var listAdapterNotes: ListAdapterNotes


    @Inject
    lateinit var notesApi: NotesApi

    @Inject
    lateinit var roomDatabase: roomDatabase

    @Inject
    lateinit var tokenManagement: TokenManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        fragmentMainBinding = FragmentMainBinding.inflate(layoutInflater, container, false)
        listAdapterNotes = ListAdapterNotes(::onNoteClicked)
        bindObservers()

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel.getNotes()

        _binding.noteList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        _binding.noteList.adapter = listAdapterNotes

        _binding.logout.setOnClickListener {
            notesViewModel.delete_All_Data()
            tokenManagement.removeToken()
            Toast.makeText(context, "LOGOUT SUCCESSFULLY !", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        _binding.addNote.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_note)
        }
    }


    private fun bindObservers() {
        if (NetworkStatus.isNetworkAvailable(requireContext().applicationContext)) {
            //Connected to network
            //we need to launch coroutine in order to call repeatOnLifecycle(suspendedFunc)
            viewLifecycleOwner.lifecycleScope.launch {

                //repeatOnLifecycle is a suspend function that takes a Lifecycle.State as a parameter and is used to automatically create
                //and launch a new coroutine with the block passed to it when the lifecycle reaches that mentioned state, and cancel
                //the ongoing coroutine that’s executing the block when the lifecycle falls below the state.

                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    notesViewModel.mutableInstance.collect {
                        _binding.loadingBar.isVisible = false
                        when (it) {
                            is NetworkResult.Success -> {
                                _binding.Wifi.setImageResource(R.drawable.wifi)
                                val notes = it.data!!.sortedByDescending { it.updatedAt }
                                listAdapterNotes.submitList(notes)
                            }
                            is NetworkResult.Failure -> {
                                Toast.makeText(context,
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is NetworkResult.Loading -> {
                                _binding.loadingBar.isVisible = true
                            }
                            is NetworkResult.StateflowInitialization -> {
                                _binding.loadingBar.isVisible = true
                            }
                        }
                    }
                }
            }
        } else {
            notesViewModel.offlineLiveDb.observe(viewLifecycleOwner, Observer {
                _binding.loadingBar.isVisible = false
                _binding.Wifi.setImageResource(R.drawable.nowifi)
                val notes = it.sortedByDescending { it.updatedAt }
                listAdapterNotes.submitList(notes)
            })
            //Not connected to network
        }
    }


    private fun onNoteClicked(notesResponse: NotesResponse) {
        //we have passed our this onNoteClicked function as a value-parameter to adapter class to get the note which is clicked
        val bundles = Bundle() //used to pass data from one act to another
        bundles.putString("note",
            Gson().toJson(notesResponse)) //as we need string so we will serialize java to json string
        findNavController().navigate(R.id.action_main_to_note, bundles)
    }

}
