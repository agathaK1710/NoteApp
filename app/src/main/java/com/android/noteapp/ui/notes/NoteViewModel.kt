package com.android.noteapp.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.noteapp.data.local.models.LocalNote
import com.android.noteapp.repository.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    val noteRepo: NoteRepo
): ViewModel(){

    val notes = noteRepo.getAllNotes()
    var oldNote: LocalNote? = null

    fun syncNotes(
        onDone: (()->Unit)? = null
    ) = viewModelScope.launch{

        noteRepo.syncNotes()
        onDone?.invoke()
    }

    fun createNote(
        noteTitle:String?,
        description:String?
    ) = viewModelScope.launch(Dispatchers.IO){
        val localNote = LocalNote(
            noteTitle = noteTitle,
            desription = description
        )
        noteRepo.createNote(localNote)
    }

    fun updateNote(
        noteTitle:String?,
        description:String?
    ) = viewModelScope.launch(Dispatchers.IO) {

        if(noteTitle == oldNote?.noteTitle && description == oldNote?.desription && oldNote?.connected == true){
            return@launch
        }

        val note = LocalNote(
            noteTitle = noteTitle,
            desription = description,
            noteId = oldNote!!.noteId
        )
        noteRepo.updateNote(note)
    }

    fun deleteNote(
        noteId:String
    ) = viewModelScope.launch {
        noteRepo.deleteNote(noteId)
    }

    fun undoDelete(
        note:LocalNote
    ) = viewModelScope.launch {
        noteRepo.createNote(note)
    }

    fun milliToDate(time:Long):String {
        val date = Date(time)
        val simpleDateFormat = SimpleDateFormat("hh:mm a | MMM d, yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

}
