package com.example.cafecornerapp.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafecornerapp.DataStore.TransaksiPreference
import com.example.cafecornerapp.Repository.TransaksiRepository
import kotlinx.coroutines.launch


class TransaksiViewModel : ViewModel() {
    private val repository = TransaksiRepository()
//    private var prefRepo = TransaksiPreference



    //    Create item
    val createStatus = MutableLiveData<String>()

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
            if (!success.isEmpty()){
                createStatus.value = success
            }else {
                Log.d("FAILEDCREATE", "FAILED-CREATE ITEM")
            }
        }
    }

    //    Update Transaksi
    val updateStatus = MutableLiveData<Boolean>()

    fun updateTransaksi(
        transaksiId: String,
        totalHarga: Long,
        catatanTambahan: String,
        buktiTransfer: String,
    ) {
        repository.updateTransaksi(transaksiId, totalHarga, catatanTambahan, buktiTransfer) {
            updateStatus.value = it
        }
    }
}