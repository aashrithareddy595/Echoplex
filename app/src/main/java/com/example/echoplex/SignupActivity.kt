package com.example.echoplex

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.echoplex.databinding.ActivityLoginBinding
import com.example.echoplex.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private val binding : ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.signuplayout)
        ViewCompat.setOnApplyWindowInsetsListener(binding.signuplayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tvLogin = binding.tvLogin
        tvLogin.setOnClickListener{
            val intent : Intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}