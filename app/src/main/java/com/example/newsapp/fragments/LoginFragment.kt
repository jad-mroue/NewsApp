package com.example.newsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var auth: FirebaseAuth
    lateinit var userViewModel: UserViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = (activity as NewsActivity).auth

        userViewModel = (activity as NewsActivity).userViewModel

        buttonLogin.setOnClickListener{
            var email = etEmailLogin.text.toString()
            var password = etPasswordLogin.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()) {
                userViewModel.loginUser(auth, email, password, this.context)
            }

        }
        btnGotoRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}