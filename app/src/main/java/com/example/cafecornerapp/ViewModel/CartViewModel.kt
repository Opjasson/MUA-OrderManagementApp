package com.example.cafecornerapp.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cafecornerapp.Repository.CartRepository

class CartViewModel : ViewModel() {
    private val repository = CartRepository()

    //    Create item
    val createStatus = MutableLiveData<Boolean>()

    fun addCart(
        userId: String,
        transaksiId: String,
        productId: String,
        jumlah: Long
    ) {
        repository.addCart(
            userId,
            transaksiId,
            productId,
            jumlah) {
                success ->
            if (success){
                createStatus.value = success
            }else {
                Log.d("FAILEDCREATE", "FAILED-CREATE ITEM")
            }
        }
    }
}