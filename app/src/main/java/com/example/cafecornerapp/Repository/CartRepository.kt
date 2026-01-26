package com.example.cafecornerapp.Repository

import com.example.cafecornerapp.Helper.ConvertDateTime
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class CartRepository {
    private val database = FirebaseFirestore.getInstance()
    private val convertDate = ConvertDateTime()

    //    Add cart
    fun addCart(
        userId: String,
        transaksiId: String,
        productId: String,
        jumlah: Long,
        onResult: (Boolean) -> Unit
    ) {
        var data = mapOf(
            "userId" to userId,
            "transaksiId" to transaksiId,
            "productId" to productId,
            "jumlah" to jumlah,
            "createdAt" to convertDate.formatTimestamp(Timestamp.now())
        )
        database.collection("cart")
            .add(data)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
}