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
import com.example.cafecornerapp.databinding.ActivityTambahProductBinding


class TambahProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahProductBinding
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

        var popular = ""
        var kategoriId : String = ""

//        show data config
        binding.plusBtn.setOnClickListener {
            val current = binding.ADMJumlahBarangForm.text.toString().toIntOrNull() ?: 0
            val newJumlah = current + 1
            binding.ADMJumlahBarangForm.setText(newJumlah.toString())
        }
        binding.minBtn.setOnClickListener {
            val current = binding.ADMJumlahBarangForm.text.toString().toIntOrNull() ?: 0
            val newJumlah = current - 1
            binding.ADMJumlahBarangForm.setText(newJumlah.toString())
        }

        binding.ADMPicItem.visibility = View.GONE
        binding.gambarBarangBtn.setOnClickListener {
            binding.ADMPicItem.visibility = View.VISIBLE
            pickImage.launch("image/*")
        }

        viewModel.createStatus.observe(this){
                documentId ->
            if (!documentId.isEmpty()) {
                viewModel.addStokAwal(documentId,
                    binding.ADMJumlahBarangForm.text.toString().toLong())
                Toast.makeText(this, "Data berhasil dibuat", Toast.LENGTH_SHORT).show()
                finish()
            }
        }


//    Setting drop down
        val items = listOf("Populer", "Tidak")

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
            popular = selected
        }

        //    Setting drop down 2
        val items2 = listOf("Alat Makan", "Alat Cuci", "Alat Masak")

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

            var kategoriId2 : Long = 0

            if (kategoriId == "Alat Makan") {
                kategoriId2 = 2
            }else if (kategoriId == "Alat Cuci") {
                kategoriId2 = 1
            }else {
                kategoriId2 = 0
            }

            binding.addItemBtn.setOnClickListener {
                viewModel.createItem(
                    binding.ADMNamaItemForm.text.toString().toLowerCase(),
                    binding.ADMDescEdt.text.toString(),
                    binding.ADMJumlahBarangForm.text.toString().toLong(),
                    if (popular == "Populer") true else false,
                    imgUrl,
                    kategoriId2,
                )

            }

        }
    }
}