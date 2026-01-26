package com.example.cafecornerapp.Domain

import java.io.Serializable

data class TransaksiModel(
    var userId : String = "",
    var totalHarga : Long = 0,
    var catatanTambahan : String = "",
    var buktiTransfer : String = "",
    var createdAt : String = ""
) : Serializable
