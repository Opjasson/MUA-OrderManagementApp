package com.example.cafecornerapp.Repository

import androidx.lifecycle.ViewModel
import com.example.cafecornerapp.Adapter.ConvertDateTime
import com.google.firebase.Timestamp

class ProductRepository : ViewModel() {

    private lateinit var convertDate : ConvertDateTime()

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
            "createdAt" to convertDate.formatTimestamp(Timestamp.now())
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