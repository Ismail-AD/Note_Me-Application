package com.example.noteme

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteme.Models.NoteRequest
import com.example.noteme.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class notesViewModel @Inject constructor(private val notesRepository: NotesRepository) :
    ViewModel() {

    val mutableInstance get() = notesRepository.responseStateFlow
    val statusInstance get() = notesRepository.responseStatusStateFLow


    var data = notesRepository.getNotesOff()
    fun delete_All_Data(){
        viewModelScope.launch {
            notesRepository.delAll()
        }
    }

    fun getNotes() {
        viewModelScope.launch {
            notesRepository.getNotes()
        }
    }

    fun createNotes(noteRequest: NoteRequest) {
        viewModelScope.launch {
            notesRepository.createNotes(noteRequest)
        }
    }

    fun updateNotes(noteId: String, noteRequest: NoteRequest) {
        viewModelScope.launch {
            notesRepository.updateNotes(noteId, noteRequest)
        }
    }

    fun deleteNotes(noteId: String) {
        viewModelScope.launch {
            notesRepository.deleteNotes(noteId)
        }
    }

}