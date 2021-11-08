package com.example.notefirebase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




class MainViewModel(application: Application): AndroidViewModel(application) {
    private val notes: MutableLiveData<List<Note>> = MutableLiveData()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun getNotes(): LiveData<List<Note>> {
        return notes
    }

    fun getData(){
        db.collection("notes")
            .get()
            .addOnSuccessListener { result ->
                val tempNotes = arrayListOf<Note>()
                for (document in result) {
                    document.data.map { (key, value) -> tempNotes.add(Note(document.id, value.toString())) }
                }
                notes.postValue(tempNotes)
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting documents.", exception)
            }
    }

    fun addNote(note: Note){
        CoroutineScope(Dispatchers.IO).launch {
            val newNote = hashMapOf(
                "noteText" to note.noteText,
            )
            db.collection("notes").add(newNote)
            getData()
        }
    }

    fun editNote(noteID: String, noteText: String){
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("notes")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        println("DB: ${document.id}")
                        println("LOCAL: $noteID")
                        if(document.id == noteID){
                            db.collection("notes").document(noteID).update("noteText", noteText)
                        }
                    }
                    getData()
                }
                .addOnFailureListener { exception ->
                    Log.w("MainActivity", "Error getting documents.", exception)
                }
        }
    }

    fun deleteNote(noteID: String){
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("notes")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        println("DB: ${document.id}")
                        println("LOCAL: $noteID")
                        if(document.id == noteID){
                            db.collection("notes").document(noteID).delete()
                        }
                    }
                    getData()
                }
                .addOnFailureListener { exception ->
                    Log.w("MainActivity", "Error getting documents.", exception)
                }
        }
    }
}