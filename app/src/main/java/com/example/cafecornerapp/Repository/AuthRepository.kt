package com.example.cafecornerapp.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val database = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

//    Login Repository
fun loginAuth (
    email : String,
    password : String,
    onResult: (Boolean, String?) -> Unit
    ) {

    auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
        onResult(true, null)
    }.addOnFailureListener {
        e ->
        onResult(false, e.message)
    }
}

//    Registrasi Repository
fun registAuth (
    username : String,
    email : String,
    password : String,
    onResult: (Boolean, String?)-> Unit
) {
    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
    result ->

    val uid = result.user!!.uid

    val user = hashMapOf(
        "username" to username,
        "email" to email,
    )

        database.collection("users").document(uid).set(user)
        onResult(true, null)
    }.addOnFailureListener {
    e ->
        onResult(false, e.message)
    }
}

//    Forgot password repository
fun forgotPassword (
    email : String,
    callback: (Boolean) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                callback(true)
            }

}

}