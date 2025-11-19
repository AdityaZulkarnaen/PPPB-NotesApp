package com.example.notesapp
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(app: Application): AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app).noteDao()

    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addNote(title: String) {
        val t = title.trim()
        if (t.isEmpty()) return
        viewModelScope.launch {
            dao.insert(Note(title = t))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { dao.delete(note) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { dao.update(note) }
    }
}