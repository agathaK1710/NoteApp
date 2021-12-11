package com.android.noteapp.ui.account

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.noteapp.R
import com.android.noteapp.utils.Result
import com.android.noteapp.databinding.FragmentRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_registration) {

    private var _binding: FragmentRegistrationBinding? = null
    val binding:FragmentRegistrationBinding?
        get() = _binding

private val userViewModel: UserViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegistrationBinding.bind(view)

        subscribeToRegisterEvents()
        binding?.btnLogin?.setOnClickListener {
            val name = binding!!.retUsername.text.toString()
            val email = binding!!.retEmail.text.toString()
            val password = binding!!.retPassword.text.toString()
            val confirmPassword = binding!!.retRepassword.text.toString()

            userViewModel.createUser(
                name.trim(),
                email.trim(),
                password.trim(),
                confirmPassword.trim()
            )
        }

    }

    private fun subscribeToRegisterEvents() = lifecycleScope.launch{
        userViewModel.registerState.collect {
            when(it){
                is Result.Success -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), "Account Successfully Created!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is Result.Error -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
            }
                is Result.Loading -> {
                    showProgressBar()
                }
            }


        }
    }

    private fun showProgressBar(){
        binding?.createUserProgressBar?.isVisible = true
    }

    private fun hideProgressBar(){
        binding?.createUserProgressBar?.isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}