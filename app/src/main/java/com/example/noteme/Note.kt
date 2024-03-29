package com.example.noteme

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.noteme.Models.NoteRequest
import com.example.noteme.Models.NotesResponse
import com.example.noteme.Utils.NetworkResult
import com.example.noteme.databinding.FragmentNoteBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
        HandleClicks()
        bindObserver()
    }

    private fun bindObserver() {
        //we need to launch coroutine in order to call collect(suspendedFunc)
        viewLifecycleOwner.lifecycleScope.launch {
            //another suspended function in order to inform state that tell flows when to stop emitting data in order to avoid memory leak
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteVM.statusInstance.collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            findNavController().popBackStack()
                        }
                        is NetworkResult.Failure -> {
                            Toast.makeText(context,
                                noteVM.statusInstance.toString(),
                                Toast.LENGTH_SHORT).show()
                        }
                        is NetworkResult.Loading -> {}
                        else -> {}
                    }
                }
            }
        }
    }

    private fun HandleClicks() {

        _binding.btndelete.setOnClickListener {
            notes?.let {
                noteVM.deleteNotes(it!!._id)
                findNavController().popBackStack()
            }
        }

        _binding.btnSub.setOnClickListener {
            val title = _binding.txtTitle.text.toString()
            val description = _binding.txtDescription.text.toString()
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(context, "Fill Both Fields !", Toast.LENGTH_SHORT).show()
            } else {
                val noteRequest = NoteRequest(description, title)
                if (notes == null) {
                    noteVM.createNotes(noteRequest)
                } else {
                    noteVM.updateNotes(notes!!._id, noteRequest)
                }
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


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentNoteBinding = null
    }


}