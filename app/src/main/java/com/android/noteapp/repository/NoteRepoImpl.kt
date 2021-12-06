package com.android.noteapp.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.android.noteapp.data.local.dao.NoteDao
import com.android.noteapp.data.local.models.LocalNote
import com.android.noteapp.data.remote.NoteApi
import com.android.noteapp.data.remote.models.RemoteNote
import com.android.noteapp.data.remote.models.User
import com.android.noteapp.utils.Result
import com.android.noteapp.utils.SessionManager
import com.android.noteapp.utils.isNetworkConnected
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepoImpl @Inject constructor(
    val noteApi: NoteApi,
    val noteDao: NoteDao,
    val sessionManager: SessionManager
): NoteRepo{
    override fun getAllNotes(): Flow<List<LocalNote>> = noteDao.getAllNotesOrderedByDate()

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun syncNotes() {
        try {
            sessionManager.getJwtToken() ?: return
            if (!isNetworkConnected(sessionManager.context)) {
                return
            }

            val locallyDeletedNotes = noteDao.getAllLocallyDeletedNotes()
            locallyDeletedNotes.forEach {
                deleteNote(it.noteId)
            }

            val notConnectedNotes = noteDao.getAllLocalNotes()
            notConnectedNotes.forEach {
                createNote(it)
            }

            val notUpdatedNotes = noteDao.getAllLocalNotes()
            notUpdatedNotes.forEach {
                updateNote(it)
            }

        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun deleteNote(noteId: String) {
        try{
            noteDao.deleteNoteLocally(noteId)
            val token = sessionManager.getJwtToken() ?: kotlin.run {
                noteDao.deleteNote(noteId)
                return
            }
            if (!isNetworkConnected(sessionManager.context)) {
                return
            }

            val response = noteApi.deleteNote(
                "Bearer $token",
                noteId
            )

            if(response.success){
                noteDao.deleteNote(noteId)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun getAllNotesFromServer() {
        try{
            val token = sessionManager.getJwtToken() ?: return
            if (!isNetworkConnected(sessionManager.context)) {
                return
            }
            val result = noteApi.getAllNote("Bearer $token")
            result.forEach { remoteNote ->
                noteDao.insertNote(
                    LocalNote(
                        noteTitle = remoteNote.noteTitle,
                        desription = remoteNote.description,
                        date = remoteNote.date,
                        connected = true,
                        noteId = remoteNote.id
                    )
                )
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun createNote(note: LocalNote): Result<String> {
        try {
            noteDao.insertNote(note)
            val token = sessionManager.getJwtToken()
                ?: return Result.Success("Note is Saved in Local Database!")
            if(!isNetworkConnected(sessionManager.context)){
                return Result.Error("No Internet connection!")
            }

            val result = noteApi.createNote(
                "Bearer $token",
                RemoteNote(
                    noteTitle = note.noteTitle,
                    description = note.desription,
                    date = note.date,
                    id = note.noteId
                )
            )

            return if(result.success){
                noteDao.insertNote(note.also { it.connected = true })
                Result.Success("Note Saved Successfully!")
            } else {
                Result.Error(result.message)
            }
        } catch (e:Exception){
            e.printStackTrace()
            return Result.Error(e.message ?: "Some Problem Occurred!")
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun updateNote(note: LocalNote): Result<String> {
        try {
            noteDao.insertNote(note)
            val token = sessionManager.getJwtToken()
                ?: return Result.Success("Note is Updated in Local Database!")

            if(!isNetworkConnected(sessionManager.context)){
                return Result.Error("No Internet connection!")
            }

            val result = noteApi.updateNote(
                "Bearer $token",
                RemoteNote(
                    noteTitle = note.noteTitle,
                    description = note.desription,
                    date = note.date,
                    id = note.noteId
                )
            )

            return if(result.success){
                noteDao.insertNote(note.also { it.connected = true })
                Result.Success("Note Updated Successfully!")
            } else {
                Result.Error(result.message)
            }
        } catch (e:Exception){
            e.printStackTrace()
            return Result.Error(e.message ?: "Some Problem Occurred!")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun createUser(user: User): Result<String> {
        return try {
            if(!isNetworkConnected(sessionManager.context)){
                Result.Error<String>("No Internet Connection!")
            }

            val result = noteApi.createAccount(user)
            if(result.success){
                sessionManager.updateSession(result.message,user.name ?:"",user.email)
                Result.Success<String>("User Created Successfully!")
            } else {
                Result.Error<String>(result.message)
            }
        }catch (e:Exception) {
            e.printStackTrace()
            Result.Error<String>(e.message ?: "Some Problem Occurred!")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun login(user: User): Result<String> {
        return try {
            if(!isNetworkConnected(sessionManager.context)){
                Result.Error<String>("No Internet Connection")
            }

            val result = noteApi.login(user)
            if(result.success){
                sessionManager.updateSession(result.message,user.name ?:"",user.email)
                getAllNotesFromServer()
                Result.Success<String>("Logged In Successfully!")
            } else {
                Result.Error<String>(result.message)
            }
        }catch (e:Exception) {
            e.printStackTrace()
            Result.Error<String>(e.message ?: "Some Problem Occurred!")
        }
    }

    override suspend fun getUser(): Result<User> {
        return try {
            val name = sessionManager.getCurrentUserName()
            val email = sessionManager.getCurrentUserEmail()
            if(name == null || email == null){
                Result.Error<User>("User not Logged In!")
            }
            Result.Success(User(name,email!!,""))
        } catch (e:Exception){
            e.printStackTrace()
            Result.Error(e.message ?: "Some Problem Occurred!")
        }
    }

    override suspend fun logout(): Result<String> {
        return try {
            sessionManager.logout()
            Result.Success<String>("Logged Out Succesfully")
        } catch (e:Exception){
            e.printStackTrace()
            Result.Error(e.message ?: "Some Problem Occurred!")
        }
    }

}