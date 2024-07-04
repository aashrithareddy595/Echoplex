package com.example.echoplex

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.echoplex.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    override fun onStart() {
        super.onStart()
        //check if user already logged-in
        val currentUser :FirebaseUser? = auth.currentUser
        if( currentUser!= null){
            startActivity(Intent(this , MainActivity::class.java))
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.main)
        auth = FirebaseAuth.getInstance()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tvSignup = binding.tvSignup
        tvSignup.setOnClickListener{
            startActivity(Intent(this , SignupActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener{
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if( email.isEmpty()||password.isEmpty()){
                Toast.makeText(this , "Please fill all the details!" , Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(email , password)
                    .addOnCompleteListener{task->
                        if(task.isSuccessful){
                            Toast.makeText(this , "Log-In successful!" , Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this , MainActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this , "${task.exception?.message}" , Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}