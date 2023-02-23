package com.example.noteme

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteme.API.NotesApi
import com.example.noteme.API.dataAccessObject
import com.example.noteme.Models.NotesResponse
import com.example.noteme.Utils.Constants.TAG
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.Utils.TokenManagement
import com.example.noteme.Utils.roomDatabase
import com.example.noteme.databinding.FragmentMainBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.noteList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        _binding.noteList.adapter = listAdapterNotes

        if (!internetIsConnected() && tokenManagement.getToken() != null) {
            notesViewModel.data.observe(viewLifecycleOwner) {
                val notes=it.sortedByDescending { it.updatedAt }
                listAdapterNotes.submitList(notes)
                Toast.makeText(context, "VIEW ONLY MODE ACTIVATED !", Toast.LENGTH_SHORT).show()
                _binding.addNote.isVisible = false
            }
        } else {
            _binding.addNote.isVisible = true
            bindObservers()
            notesViewModel.getNotes()
        }
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

    private fun internetIsConnected(): Boolean {
        return try {
            val command = "ping -c 1 google.com"
            Runtime.getRuntime().exec(command).waitFor() == 0

        } catch (e: Exception) {
            false
        }
    }


    private fun bindObservers() {
        notesViewModel.mutableInstance.observe(viewLifecycleOwner, Observer {
            _binding.loadingBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    val notes=it.data!!.sortedByDescending { it.updatedAt }
                    listAdapterNotes.submitList(notes)
                }
                is NetworkResult.Failure -> {
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    _binding.loadingBar.isVisible = true
                }
            }
        })
    }

    private fun onNoteClicked(notesResponse: NotesResponse) {
        //we have passed our this onNoteClicked function as a value-parameter to adapter class to get the note which is clicked
        val bundles = Bundle() //used to pass data from one act to another
        bundles.putString("note",
            Gson().toJson(notesResponse)) //as we need string so we will serialize java to json string
        findNavController().navigate(R.id.action_main_to_note, bundles)
    }

}