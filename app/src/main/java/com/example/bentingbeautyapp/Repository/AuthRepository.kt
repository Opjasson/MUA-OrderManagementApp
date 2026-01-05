package com.example.bentingbeautyapp.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val database = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

// Registrasi Repository
fun registAuth (
    email : String,
    password : String
    onResult: (Boolean, String?)-> Unit
) {
    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
result ->
    val uid = result.user!!.uid
    val user =hashMapOf(
        "email" to email,
        "password" to password
    )

database.collection("users").document(uid).set(user)
        onResult(true, null)
    }.addOnFailureListener {
    e ->
        onResult(false, e.message)
    }
}
}