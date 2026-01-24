package com.example.cafecornerapp.Repository

import androidx.lifecycle.ViewModel
import com.example.cafecornerapp.Adapter.ConvertDateTime
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class ProductRepository {
    private val database = FirebaseFirestore.getInstance()
    private val convertDate = ConvertDateTime()

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
            "harga_product" to harga_product,
            "kategori_product" to kategori_product,
            "imgUrl" to imgUrl,
            "promo" to promo,
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