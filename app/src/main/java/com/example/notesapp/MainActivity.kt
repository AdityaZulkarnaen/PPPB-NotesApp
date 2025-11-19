package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
// Ikon (pastikan tambahkan dependency material-icons-extended)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Close
import com.example.notesapp.Note
import com.example.notesapp.NotesViewModel
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NotesScreen()
                }
            }
        }
    }
}

@Composable
fun NotesScreen(vm: NotesViewModel = viewModel()) {
    val notes by vm.notes.collectAsState()
    var input by rememberSaveable { mutableStateOf("") }

    // State untuk Tahap 3 (edit inline)
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                label = { Text("Catatan baru") }
            )
            Button(onClick = { vm.addNote(input); input = "" }) {
                Text("Tambah")
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn {
            items(notes, key = { it.id }) { note ->
                val isEditing = editingId == note.id
                if (isEditing) {
                    // Mode EDIT (Tahap 3)
                    NoteRowEditing(
                        text = editingText,
                        onTextChange = { editingText = it },
                        onSave = {
                            val newText = editingText.trim()
                            if (newText.isNotEmpty()) vm.updateNote(note.copy(title = newText))
                            editingId = null
                        },
                        onCancel = { editingId = null }
                    )
                } else {
                    // Mode normal + Delete & Edit (Tahap 2 & 3)
                    NoteRowEditable(
                        note = note,
                        onEdit = {
                            editingId = note.id
                            editingText = note.title
                        },
                        onDelete = { vm.deleteNote(note) }
                    )
                }
                Divider()
            }
        }
    }
}

// Tahap 1: versi read-only (dipakai kalau kamu ingin hanya tahap 1)
@Composable
fun NoteRowReadOnly(note: Note) {
    ListItem(
        headlineContent = { Text(note.title) }
    )
}

// Tahap 2: baris dengan tombol HAPUS
@Composable
fun NoteRowDeletable(note: Note, onDelete: (Note) -> Unit) {
    ListItem(
        headlineContent = { Text(note.title) },
        trailingContent = {
            IconButton(onClick = { onDelete(note) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Hapus")
            }
        }
    )
}

// Tahap 3: baris normal dengan tombol EDIT & DELETE
@Composable
fun NoteRowEditable(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(note.title) },
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus")
                }
            }
        }
    )
}

// Tahap 3: baris saat sedang EDIT
@Composable
fun NoteRowEditing(
    text: String,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    ListItem(
        headlineContent = {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ubah catatan") }
            )
        },
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onSave) {
                    Icon(Icons.Filled.Done, contentDescription = "Simpan")
                }
                IconButton(onClick = onCancel) {
                    Icon(Icons.Filled.Close, contentDescription = "Batal")
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotesScreenPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NotesScreen()
        }
    }
}