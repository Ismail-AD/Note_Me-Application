package com.example.noteme.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notesResponse")
data class NotesResponse(
    val __v: Int,
    @PrimaryKey
    val _id: String,
    val createdAt: String,
    val description: String,
    val title: String,
    val updatedAt: String,
    val userId: String
)