package com.example.cafecornerapp.Domain

import java.io.Serializable

data class CartModel(
    var documentId : String = "",
    var transaksiId : String = "",
    var userId : String = "",
    var productId : String = "",
    var jumlah : Long = 0,
    var createdAt : String = ""
) : Serializable
