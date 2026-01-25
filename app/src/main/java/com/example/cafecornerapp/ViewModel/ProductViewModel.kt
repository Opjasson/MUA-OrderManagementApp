package com.example.cafecornerapp.ViewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cafecornerapp.Domain.ProductModel
import com.example.cafecornerapp.Repository.CloudinaryRepository
import com.example.cafecornerapp.Repository.ProductRepository

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()

    //    Backend cloudinary
    private val repo = CloudinaryRepository()
    val imageUrl = MutableLiveData<String>()

    fun upload(context: Context, uri: Uri) {
        repo.uploadImageToCloudinary(
            context,
            uri,
            onSuccess = {
                Log.d("imgUrlView", it.toString())
                imageUrl.postValue(it)
            },
            onError = { Log.d("ERROR", "Internal Error") }
        )
    }

    //    Create item
    val createStatus = MutableLiveData<Boolean>()

    fun createItem(
        nama_product: String,
        deskripsi_product: String,
        harga_product: Long,
        kategori_product: String,
        imgUrl: String,
        promo: Boolean,
    ) {
        repository.createItem(nama_product, deskripsi_product,harga_product, kategori_product,
            imgUrl, promo) {
                success ->
            if (success){
                createStatus.value = success
            }else {
                Log.d("FAILEDCREATE", "FAILED-CREATE ITEM")
            }
        }
    }

    //    Get all items
    private val _searchResult = MutableLiveData<List<ProductModel>>()
    val searchResult: LiveData<List<ProductModel>> = _searchResult

    fun loadAllItems() {
        repository.getAllItems() {
            _searchResult.value = it
        }
    }

//    Delete product
    var _successDelete = MutableLiveData<Boolean>()

    fun deleteProduct(productId : String) {
        repository.deleteProduct(productId) {
            _successDelete.value = it
        }
    }

}