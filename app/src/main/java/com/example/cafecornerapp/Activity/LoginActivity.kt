package com.example.cafecornerapp.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.AuthViewModel
import com.example.cafecornerapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            LGotoRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            forgotPassTxt.setOnClickListener {
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            }

            lAlertForm.visibility = View.GONE

            loginBtn.setOnClickListener {
                val email = emailForm.text.toString().trim()
                val password = passwordForm.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    lAlertForm.visibility = View.VISIBLE
                }else {
                    viewModel.login(email, password)
                }
            }
        }

        viewModel.loginResult.observe(this) {
            result ->
            result.onSuccess {
                message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
                result.onFailure {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

        }


    }
}