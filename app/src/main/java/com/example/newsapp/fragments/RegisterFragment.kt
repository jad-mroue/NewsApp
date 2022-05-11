package com.example.newsapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(R.layout.fragment_register) {
    lateinit var auth: FirebaseAuth
    lateinit var userViewModel: UserViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = (activity as NewsActivity).auth
        userViewModel = (activity as NewsActivity).userViewModel

        buttonRegister.setOnClickListener{
            var email = etEmailRegister.text.toString()
            var password = etPasswordRegister.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){
                userViewModel.registerUser(auth, email, password, this.context)
            }
        }
        btnGotoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}