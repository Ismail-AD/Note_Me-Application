package com.example.noteme

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.noteme.Models.NotesResponse
import com.example.noteme.databinding.NoteItemBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ListAdapterNotes(private var onNoteClicked_ln: (NotesResponse) -> Unit) :
    ListAdapter<NotesResponse, ListAdapterNotes.NotesViewHolder>(DiffUtilComparator()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binder = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binder)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = getItem(position)

        //The let function is useful when you wish to perform a null safe operation on an Object
        // by using the the safe call operator ?.
        // When doing this the let code block will only be executed if the object is not null
        note?.let {
            holder.bind(note)
        }
    }

    inner class NotesViewHolder(private val noteItemBinding: NoteItemBinding) :
        ViewHolder(noteItemBinding.root) {
        fun bind(notesResponse: NotesResponse) {
            noteItemBinding.title.text = notesResponse.title
            noteItemBinding.desc.text = notesResponse.description
            //Locale is used to represent specific country / language date or information
            val format: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("PKT")
            val date = format.parse(notesResponse.updatedAt)

            if (date != null) {
                noteItemBinding.time.text = date.toLocaleString()
            }
            //whenever a root node/layout is clicked execute this function as we don't need to define our functionality
            //here in adapter class so pass that to the fragment
            noteItemBinding.root.setOnClickListener {
                onNoteClicked_ln(notesResponse)
            }
        }
    }

    class DiffUtilComparator : DiffUtil.ItemCallback<NotesResponse>() {
        override fun areItemsTheSame(oldItem: NotesResponse, newItem: NotesResponse): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: NotesResponse, newItem: NotesResponse): Boolean {
            return oldItem == newItem
        }

    }
}