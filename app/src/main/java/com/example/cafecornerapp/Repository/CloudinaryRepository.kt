package com.example.cafecornerapp.Repository

import android.content.Context
import android.net.Uri
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

class CloudinaryRepository {
    fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload.jpg")

        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }

    fun uploadImageToCloudinary(context: Context
                                ,uri: Uri
                                ,onSuccess: (String) -> Unit
                                ,onError: () -> Unit) {
        val file = uriToFile(context, uri)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("image/*".toMediaType())
            )
            .addFormDataPart("upload_preset", "Cloudinary_my_first_time")
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/dqcnnluof/image/upload")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError()
            }

            override fun onResponse(call: Call, response: Response) {
                val imageUrl = JSONObject(response.body!!.string())
                    .getString("secure_url")
                onSuccess(imageUrl)
            }
        })
    }
}