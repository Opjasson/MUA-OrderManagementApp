package com.example.cafecornerapp.Repository

import android.util.Log
import com.example.cafecornerapp.Domain.CartModel
import com.example.cafecornerapp.Domain.ProductModel
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

    //     get product by kategori
    fun getCartByTransaksiId(
        transaksiId : String,
        callback : (List<CartModel>) -> Unit
    ) {
        database.collection("cart")
            .whereEqualTo("transaksiId", transaksiId)
            .get()
            .addOnSuccessListener {
                    snapshots ->
                val list = snapshots.documents.mapNotNull { doc ->
                    doc.toObject(CartModel::class.java)?.apply {
                        documentId = doc.id   // 🔥 isi documentId
                    }
                }
                Log.d("LISTDATA", list.toString())
                callback(list)
            }
    }
}