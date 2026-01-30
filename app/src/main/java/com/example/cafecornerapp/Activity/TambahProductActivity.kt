package com.example.cafecornerapp.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.cafecornerapp.DataStore.UserPreference
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.AuthViewModel
import com.example.cafecornerapp.ViewModel.ProductViewModel
import com.example.cafecornerapp.ViewModel.UserViewModel
import com.example.cafecornerapp.databinding.ActivityTambahProductBinding
import kotlinx.coroutines.launch


class TambahProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahProductBinding
    private var viewModel = ProductViewModel()

    private lateinit var userPreference: UserPreference
    private lateinit var drawerLayout: DrawerLayout

    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTambahProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initFormCreate()
        initSideBar()

        userViewModel.getUserByUid()

        userViewModel.userLogin.observe(this) { user ->
            user?.let {

                val headerView = binding.navigationView.getHeaderView(0)
                headerView.findViewById<TextView>(R.id.tvNameHeader).text = user?.username
                headerView.findViewById<TextView>(R.id.tvEmailHeader).text = user?.email

                val menu = binding.navigationView.menu
                if (user?.documentId != "JTER5kKcDvRerpk6c9pJYGxhd7D2") {
                    menu.findItem(R.id.menu_laporan)?.isVisible = false
                    menu.findItem(R.id.menu_manageProduct)?.isVisible = false
                }
            }
        }

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

    private fun initSideBar () {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout

        val navigationView = binding.navigationView

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.menu_manageProduct -> {
                    startActivity(Intent(this, ManageProductActivity::class.java))
                }
                R.id.menu_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                }
                R.id.menu_history -> {
                    startActivity(Intent(this, HistoryTransaksiActivity::class.java))
                }
                R.id.menu_laporan -> {
                    startActivity(Intent(this, LaporanTransactionActivity::class.java))
                }
                R.id.menu_logout -> {
                    showLogoutDialog()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun showLogoutDialog() {
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah kamu yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                authViewModel.logout()
                lifecycleScope.launch {
                    userPreference.deleteUserId()
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

}