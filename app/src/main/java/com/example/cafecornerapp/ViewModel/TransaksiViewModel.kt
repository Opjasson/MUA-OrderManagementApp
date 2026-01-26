package com.example.cafecornerapp.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cafecornerapp.Repository.TransaksiRepository


class TransaksiViewModel : ViewModel() {
    private val repository = TransaksiRepository()

    //    Create item
    val createStatus = MutableLiveData<Boolean>()

    fun createTransaksi(
        userId: String,
        totalHarga: Long,
        catatanTambahan: String,
        buktiTransfer: String,
    ) {
        repository.createTransaksi(
            userId,
            totalHarga,
            catatanTambahan,
            buktiTransfer) {
                success ->
            if (success){
                createStatus.value = success
            }else {
                Log.d("FAILEDCREATE", "FAILED-CREATE ITEM")
            }
        }
    }
}