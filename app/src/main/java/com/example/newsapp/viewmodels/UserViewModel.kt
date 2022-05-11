package com.example.newsapp.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.example.newsapp.NewsActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {

    fun loginUser(auth: FirebaseAuth, email: String, password: String, context: Context?){
        val email = email
        val password = password
        CoroutineScope(Dispatchers.IO).launch{
            try{
                auth.signInWithEmailAndPassword(email, password).await()
                context?.startActivity(Intent(context, NewsActivity::class.java))
            }catch(e: Exception){
                Log.e("Login error",e.message.toString())
            }
        }
    }
    fun registerUser(auth: FirebaseAuth, email: String, password: String, context: Context?){
        val email = email
        val password = password

        CoroutineScope(Dispatchers.IO).launch{
            try{
                auth.createUserWithEmailAndPassword(email, password).await()
                context?.startActivity(Intent(context, NewsActivity::class.java))
            }catch(e: Exception){
                withContext(Dispatchers.Main){

                }
            }
        }
    }
}