package com.example.echoplex

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.echoplex.databinding.ActivityLoginBinding
import com.example.echoplex.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private val binding : ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.signuplayout)
        //initialize firebase auth
        auth = FirebaseAuth.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(binding.signuplayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val tvLogin = binding.tvLogin
        tvLogin.setOnClickListener{
            startActivity(Intent(this , LoginActivity::class.java))
            finish()
        }
        binding.submitBtn.setOnClickListener{
            val email = binding.etEmail.text.toString()
            val name = binding.etName.text.toString()
            val password = binding.etPassword.text.toString()
            val password2 = binding.etPassword2.text.toString()

            if(email.isEmpty()||name.isEmpty()||password.isEmpty()||password2.isEmpty()){
                Toast.makeText(this , "Please fill all the details!" , Toast.LENGTH_SHORT).show()
            }else if( password!=password2){
                Toast.makeText(this , "Passwords do not match!" , Toast.LENGTH_SHORT).show()
            }else{
                auth.createUserWithEmailAndPassword(email , password)
                    .addOnCompleteListener(this){task->
                        if(task.isSuccessful){
                            Toast.makeText(this , "Sign up successful!" , Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this , LoginActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this , "${task.exception?.message}" , Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
    }
}