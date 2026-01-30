package com.example.cafecornerapp.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.cafecornerapp.Adapter.CardProductAdapter
import com.example.cafecornerapp.Adapter.CardProductListCartAdapter
import com.example.cafecornerapp.DataStore.TransaksiPreference
import com.example.cafecornerapp.DataStore.UserPreference
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.AuthViewModel
import com.example.cafecornerapp.ViewModel.CartViewModel
import com.example.cafecornerapp.ViewModel.ProductViewModel
import com.example.cafecornerapp.ViewModel.TransaksiViewModel
import com.example.cafecornerapp.ViewModel.UserViewModel
import com.example.cafecornerapp.databinding.ActivityCartBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel = CartViewModel()

    private val viewModelTransaksi = TransaksiViewModel()
    private val userViewModel = UserViewModel()

    private val viewModelImg = ProductViewModel()
    private lateinit var drawerLayout: DrawerLayout

    private val prefRepo = TransaksiPreference(this)

    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initHandleBuy()
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

    private fun initHandleBuy () {
        binding.picTf.visibility = View.GONE

        var imgUrl : String = ""

        val pickImage =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    Glide.with(applicationContext).load(uri).into(binding.picTf)
                    viewModelImg.upload(this, uri)
                }
            }

        viewModelImg.imageUrl.observe(this){
            imgUrl = it.toString()
        }

        binding.btnUpload.setOnClickListener {
            binding.picTf.visibility = View.VISIBLE
            pickImage.launch("image/*")
        }

//        get Cart by transaksi
        lifecycleScope.launch {
           val transId = prefRepo.getTransactionId().first()
                viewModel.getCartByTransaksiId(transId.toString())
        }

        binding.loadCart.visibility= View.VISIBLE
        viewModel.cartResult.observe(this) {
                list ->
            viewModel.loadCartCustom(list)

            viewModel.transaksiUI.observe(this) {
                    data ->
                Log.d("DATACARTACTI", data.toString())
                var totalHarga = data.sumOf {
                    item ->
                    item.harga * item.jumlah
                }

                binding.btnBuy.setOnClickListener {
                    viewModelTransaksi.updateTransaksi(
                        list[0].transaksiId,
                        totalHarga.toLong(),
                        binding.etNote.text.toString(),
                        imgUrl
                    )

                    viewModelTransaksi.updateStatus.observe(this) {
                            success ->
                        if (success) {
                            Toast.makeText(this, "Pesanan Sedang Diproses", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                    lifecycleScope.launch {
                        prefRepo.clearTransactionId()
                    }


                }




                binding.tvTotal.text = "Rp $totalHarga"

                binding.rvCart.layoutManager= LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false)
                binding.rvCart.adapter= CardProductListCartAdapter(
                    onKurangClick = {
                            cart ->
                        if (cart.jumlah > 1) {
                            viewModel.minusQtyCart(cart.cartId)
                        }

                    },
                    onPlusClick = {
                            cart ->
                            viewModel.addQtyCart(cart.cartId)
                    },
                    data.toMutableList(),
                    )
//                ---------------
                CardProductListCartAdapter(
                    onKurangClick = {
                            cart ->
                        if (cart.jumlah > 1) {
                            viewModel.minusQtyCart(cart.cartId)
                        }
                    },
                    onPlusClick = {
                            cart ->
                        viewModel.addQtyCart(cart.cartId)
                    },
                    data.toMutableList()).submitList(data)
                binding.loadCart.visibility= View.GONE
            }
        }
    }
}