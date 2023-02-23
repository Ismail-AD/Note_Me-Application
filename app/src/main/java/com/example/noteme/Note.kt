package com.example.noteme

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.noteme.Models.NoteRequest
import com.example.noteme.Models.NotesResponse
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.databinding.FragmentNoteBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Note : Fragment() {

    private var fragmentNoteBinding: FragmentNoteBinding? = null
    private val _binding get() = fragmentNoteBinding!!
    private var notes: NotesResponse? = null
    private val noteVM by viewModels<notesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        fragmentNoteBinding = FragmentNoteBinding.inflate(layoutInflater, container, false)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setInitialData()
        if (!internetIsConnected()) {
            _binding.btnSub.isVisible=false
            _binding.btndelete.isVisible=false
            _binding.txtTitle.isFocusable = false
            _binding.txtTitle.isEnabled = false
            _binding.txtTitle.isCursorVisible = false
            _binding.txtTitle.keyListener = null
            _binding.txtTitle.setBackgroundColor(Color.TRANSPARENT)
            _binding.txtDescription.isFocusable = false
            _binding.txtDescription.isEnabled = false
            _binding.txtDescription.isCursorVisible = false
            _binding.txtDescription.keyListener = null
            _binding.txtDescription.setBackgroundColor(Color.TRANSPARENT)
        }

        HandleClicks()
        bindObserver()

    }

    private fun bindObserver() {
        noteVM.statusInstance.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    findNavController().popBackStack()
                }
                is NetworkResult.Failure -> {}
                is NetworkResult.Loading -> {}
            }
        })
    }

    private fun HandleClicks() {

        _binding.btndelete.setOnClickListener {
            notes?.let {
                noteVM.deleteNotes(it!!._id)
            }
            Toast.makeText(context, "Deleted Successfully !", Toast.LENGTH_SHORT).show()
        }

        _binding.btnSub.setOnClickListener {
            val title = _binding.txtTitle.text.toString()
            val description = _binding.txtDescription.text.toString()
            val noteRequest = NoteRequest(description, title)
            if (notes == null) {
                noteVM.createNotes(noteRequest)
            } else{
                noteVM.updateNotes(notes!!._id, noteRequest)
            }
        }
    }


    private fun setInitialData() {



        //we can receive our fragment bundles data into 'arguments'
        val jsonData = arguments?.getString("note")
        if (jsonData != null) { //it means we got an edit format
            notes = Gson().fromJson(jsonData, NotesResponse::class.java)
            //bind the data
            notes?.let {
                _binding.txtTitle.setText(it.title)
                _binding.txtDescription.setText(it.description)

            }
        } else { //we got to add new note as its not edit format
            _binding.addEditText.text = "Add Note"
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

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentNoteBinding = null
    }


}