package com.example.notesapp

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String
)
