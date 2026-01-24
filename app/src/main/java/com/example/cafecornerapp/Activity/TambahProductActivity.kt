package com.example.cafecornerapp.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.ProductViewModel
import com.example.cafecornerapp.databinding.ActivityTambahProductBinding


class TambahProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahProductBinding
    private var viewModel = ProductViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTambahProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initFormCreate()

    }

    private fun initFormCreate () {
        binding.picItem.visibility = View.GONE

        var imgUrl : String = ""

        val pickImage =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                Log.d("URL", uri.toString())
                uri?.let {
                    Glide.with(applicationContext).load(uri).into(binding.picItem)
                    viewModel.upload(this, uri)
                }
            }

        viewModel.imageUrl.observe(this){
            imgUrl = it.toString()
        }

        var kategori = ""
        var kategoriId : String = ""

//        show data config

        binding.gambarBarangForm.setOnClickListener {
            binding.picItem.visibility = View.VISIBLE
            pickImage.launch("image/*")
        }

        viewModel.createStatus.observe(this){
            success ->
            if (success) {
                Toast.makeText(this, "Data berhasil dibuat", Toast.LENGTH_SHORT).show()
                finish()
            }
        }


//    Setting drop down
        val items = listOf("minuman", "makanan")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            items
        )

        if (binding.dropdownMenu.text.isNullOrEmpty()) {
            binding.dropdownLayout.error = "Harus dipilih"
        } else {
            binding.dropdownLayout.error = null
        }

        binding.dropdownMenu.setAdapter(adapter)

        binding.dropdownMenu.setOnItemClickListener { _, _, position, _ ->
            val selected = items[position]
            kategori = selected
        }

        //    Setting drop down 2
        val items2 = listOf("Ya", "Tidak")

        val adapter2 = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            items2
        )

        if (binding.dropdownMenu2.text.isNullOrEmpty()) {
            binding.dropdownLayout2.error = "Harus dipilih"
        } else {
            binding.dropdownLayout2.error = null
        }

        binding.dropdownMenu2.setAdapter(adapter2)

        binding.dropdownMenu2.setOnItemClickListener { _, _, position, _ ->
            val selected2 = items2[position]
            kategoriId = selected2

            var kategoriId2 : Boolean = false

            if (kategoriId == "Ya") {
                kategoriId2 = true
            }else {
                kategoriId2 = false
            }

            binding.addProductBtn.setOnClickListener {
                viewModel.createItem(
                    binding.nameItemFormTxt.text.toString().toLowerCase(),
                    binding.descEdt.text.toString(),
                    binding.hargaItemFormTxt.text.toString().toLong(),
                    kategori,
                    imgUrl,
                    kategoriId2,
                )

            }

        }
    }
}