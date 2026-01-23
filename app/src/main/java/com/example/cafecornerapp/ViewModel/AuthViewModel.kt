package com.example.cafecornerapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cafecornerapp.Repository.AuthRepository

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()

//  Login Model
    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult : LiveData<Result<String>> = _loginResult

    fun login(email: String, password: String) {
        repository.loginAuth(email, password) {
            success, message ->
                if (success) {
                    _loginResult.value = Result.success("Login Berhasil")
                }else {
                    _loginResult.value = Result.failure(Exception(message))
                }
            }
    }

//  Register Model
    private val _regisResult = MutableLiveData<Result<String>>()
    val registResult: LiveData<Result<String>> = _regisResult

    fun register(username : String, email : String, password : String){
        repository.registAuth(username, email, password) {

            success, message ->
            if(success) {
                _regisResult.value = Result.success("Register Berhasil")
            }else {
                _regisResult.value = Result.failure(Exception(message))
            }
        }
    }

//    Forgot Password Model

    private val _forgotPassResult = MutableLiveData<Boolean>()
    val forgotPassResult : LiveData<Boolean> = _forgotPassResult

    fun forgotPass(email : String) {
        repository.forgotPassword(email) {
            _forgotPassResult.value = it
        }
    }

}