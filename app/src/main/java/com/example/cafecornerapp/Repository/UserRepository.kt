package com.example.cafecornerapp.Repository

import com.example.cafecornerapp.Domain.UsersModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    val database = FirebaseFirestore.getInstance()


    fun getUsersByUid (
        onResult: (UsersModel?) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        database.collection("users")
            .document(uid.toString())
            .get()
            .addOnSuccessListener {
                    document ->
                val user = document.toObject(UsersModel::class.java)
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}