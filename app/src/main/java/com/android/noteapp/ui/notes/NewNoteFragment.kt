package com.android.noteapp.ui.notes

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.android.noteapp.R
import com.android.noteapp.databinding.FragmentNewNoteBinding
import com.android.noteapp.ui.account.OK
import com.android.noteapp.ui.account.USER_LOGGED

class NewNoteFragment: Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    val binding: FragmentNewNoteBinding?
        get() = _binding

    val noteViewModel: NoteViewModel by activityViewModels()
    val args: NewNoteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewNoteBinding.bind(view)

        noteViewModel.oldNote = args.note

        if(USER_LOGGED == true && OK == true){
            noteViewModel.oldNote?.noteTitle?.let {
                binding?.newNoteTitleEditText?.setText(it)
            }

            noteViewModel.oldNote?.desription?.let {
                binding?.newNoteDescriptionEditText?.setText(it)
            }

            binding?.date?.isVisible = noteViewModel.oldNote != null
            noteViewModel.oldNote?.date?.let {
                binding?.date?.text = noteViewModel.milliToDate(it)
            }

        } else {
            binding?.newNoteTitleEditText?.isVisible = false
            binding?.newNoteTitleTextView?.isVisible = true
            binding?.newNoteDescriptionEditText?.isVisible = false
            binding?.newNoteDescriptionTextView?.isVisible = true
            binding?.date?.isVisible = false
            binding?.date1?.isVisible = true
            binding?.scroll?.isVisible = true

            noteViewModel.oldNote?.noteTitle?.let {
                binding?.newNoteTitleTextView?.setText(it)
            }

            noteViewModel.oldNote?.desription?.let {
                binding?.newNoteDescriptionTextView?.setText(it)
            }

            binding?.date1?.isVisible = noteViewModel.oldNote != null
            noteViewModel.oldNote?.date?.let {
                binding?.date1?.text = noteViewModel.milliToDate(it)
            }
        }


    }

    override fun onPause() {
        super.onPause()
        if(noteViewModel.oldNote == null){
            createNote()
        } else {
            updateNote()
        }
    }

    private fun createNote() {
        val noteTitle: String?
        val description: String?

        if(USER_LOGGED == true) {
            noteTitle = binding?.newNoteTitleEditText?.text?.toString()?.trim()
            description = binding?.newNoteDescriptionEditText?.text?.toString()?.trim()
        } else {
            noteTitle = binding?.newNoteTitleTextView?.text?.toString()?.trim()
            description = binding?.newNoteDescriptionTextView?.text?.toString()?.trim()
        }

        if(noteTitle.isNullOrEmpty() && description.isNullOrEmpty()){
            Toast.makeText(requireContext(), "Note is Empty!", Toast.LENGTH_SHORT).show()
            return
        }

        noteViewModel.createNote(noteTitle,description)
    }
    private fun updateNote() {

        val noteTitle: String?
        val description: String?

        if(USER_LOGGED == true) {
            noteTitle = binding?.newNoteTitleEditText?.text?.toString()?.trim()
            description = binding?.newNoteDescriptionEditText?.text?.toString()?.trim()
        } else {
            noteTitle = binding?.newNoteTitleTextView?.text?.toString()?.trim()
            description = binding?.newNoteDescriptionTextView?.text?.toString()?.trim()
        }

        if(noteTitle.isNullOrEmpty() && description.isNullOrEmpty()) {
            noteViewModel.deleteNote(noteViewModel.oldNote!!.noteId)
            return
        }
        noteViewModel.updateNote(noteTitle,description)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}