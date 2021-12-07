package com.android.noteapp.ui.account

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.noteapp.R
import com.android.noteapp.utils.Result
import com.android.noteapp.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment: Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    val binding:FragmentLoginBinding?
        get() = _binding

    private val userViewModel: UserViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        subscribeToLoginEvents()

        binding?.btnLogin?.setOnClickListener {
            val name = binding!!.etUsername.text.toString()
            val email = binding!!.etEmail.text.toString()
            val password = binding!!.etPassword.text.toString()

            userViewModel.loginUser(
                name.trim(),
                email.trim(),
                password.trim()
            )
        }

    }

    private fun subscribeToLoginEvents() = lifecycleScope.launch{
        userViewModel.loginState.collect {
            when(it){
                is Result.Success -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), "Welcome!", Toast.LENGTH_SHORT).show()
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
        binding?.loginProgressBar?.isVisible = true
    }

    private fun hideProgressBar(){
        binding?.loginProgressBar?.isVisible = false
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}