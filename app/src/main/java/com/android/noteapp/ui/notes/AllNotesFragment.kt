package com.android.noteapp.ui.notes

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.noteapp.R
import com.android.noteapp.databinding.FragmentAllnotesBinding
import com.android.noteapp.ui.adapter.NoteAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllNotesFragment: Fragment(R.layout.fragment_allnotes) {

    private var _binding: FragmentAllnotesBinding? = null
    val binding: FragmentAllnotesBinding?
        get() = _binding

    private lateinit var noteAdapter: NoteAdapter
    private val noteViewModel:NoteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllnotesBinding.bind(view)
        //(activity as AppCompatActivity).setSupportActionBar(binding!!.customToolBar)

        binding?.newNoteFab?.setOnClickListener {
            findNavController().navigate(R.id.action_allNotesFragment_to_newNoteFragment)
        }
        setupRecyclerView()
        subscribeToNotes()
        setUpSwipeLayout()
        noteViewModel.syncNotes()
    }

    private fun setupRecyclerView(){

        noteAdapter = NoteAdapter()
        noteAdapter.setOnItemClickListener {
            val action = AllNotesFragmentDirections.actionAllNotesFragmentToNewNoteFragment(it)
            findNavController().navigate(action)
        }

        binding?.noteRecyclerView?.apply {
            adapter = noteAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            ItemTouchHelper(itemTouchHelperCallback)
                .attachToRecyclerView(this)

        }
    }

    private fun subscribeToNotes() = lifecycleScope.launch{
        noteViewModel.notes.collect {
            noteAdapter.notes = it
        }
    }

    private fun setUpSwipeLayout(){
        binding?.swipeRefeeshLayout?.setOnRefreshListener {
            noteViewModel.syncNotes {
                binding?.swipeRefeeshLayout?.isRefreshing = false
            }
        }
    }

    val itemTouchHelperCallback = object :ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notes[position]
            noteViewModel.deleteNote(note.noteId)
            Snackbar.make(
                requireView(),
                "Note Deleted Successfully!",
                Snackbar.LENGTH_LONG
            ).apply {
                setAction(
                    "Undo"
                ) {
                    noteViewModel.undoDelete(note)
                }

                show()
            }
        }
    }


        override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.account -> {
                findNavController().navigate(R.id.action_allNotesFragment_to_userInfoFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}