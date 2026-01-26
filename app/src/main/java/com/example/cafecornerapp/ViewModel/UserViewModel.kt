package com.example.cafecornerapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cafecornerapp.Domain.UsersModel
import com.example.cafecornerapp.Repository.UserRepository

class UserViewModel : ViewModel() {
    private val repository = UserRepository()

    //    Get userByUid
    private val _userLoggin = MutableLiveData<UsersModel?>()
    val userLogin: LiveData<UsersModel?> = _userLoggin
    fun getUserByUid () {
        repository.getUsersByUid() {
                result ->
            _userLoggin.value = result
        }
    }
}