package com.example.cafecornerapp.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cafecornerapp.Domain.CartModel
import com.example.cafecornerapp.Domain.ProductModel
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
        Log.d("DATAKU", "dipanggil $userId $transaksiId $productId")
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

    //    get product by kategori
    private val _cartResult = MutableLiveData<List<CartModel>>()
    val cartResult: LiveData<List<CartModel>> = _cartResult

    fun getCartByTransaksiId(transaksiId : String) {
        repository.getCartByTransaksiId(transaksiId) {
            _cartResult.value = it
        }
    }
}