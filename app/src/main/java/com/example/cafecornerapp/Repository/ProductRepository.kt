package com.example.cafecornerapp.Repository

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp

class ProductRepository : ViewModel() {

    //    Add item
    fun createItem(
        nama_product: String,
        deskripsi_product: String,
        harga_product: Long,
        kategori_product: String,
        imgUrl: String,
        promo: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        var data = mapOf(
            "nama_product" to nama_product,
            "deskripsi_product" to deskripsi_product,
            "harga_product" to ,
            "kategori_product" to popular,
            "imgUrl" to imgUrl,
            "promo" to kategoriId,
            "createdAt" to Timestamp.now()
        )
        database.collection("product")
            .add(data)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
}