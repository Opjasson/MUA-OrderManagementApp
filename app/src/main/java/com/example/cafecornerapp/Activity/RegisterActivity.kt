package com.example.cafecornerapp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.AuthViewModel
import com.example.cafecornerapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            RGotoLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }

            rAlertForm.visibility = View.GONE
        }

        //        Handle register
       binding.registBtn.setOnClickListener {
            val username = binding.RUsernameForm.text.toString().trim()
            val email = binding.REmailForm.text.toString().trim()
            val password = binding.RPasswordForm.text.toString().trim()
            val confPassword = binding.confPasswordForm.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confPassword.isEmpty()){
                binding.rAlertForm.text = "Lengkapi formulir!"
                binding.rAlertForm.visibility = View.VISIBLE
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.rAlertForm.text = "Format email tidak valid!"
                binding.rAlertForm.visibility = View.VISIBLE
            }else if (password != confPassword) {
                binding.apply {
                    rAlertForm.text = "Password dan Confirm Password Tidak Sama!"
                    rAlertForm.visibility = View.VISIBLE
                }
            }else {
                viewModel.register(username, email, password)
            }
        }

        viewModel.registResult.observe(this){
                result ->
            result.onSuccess {
                    message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            }

            result.onFailure {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}