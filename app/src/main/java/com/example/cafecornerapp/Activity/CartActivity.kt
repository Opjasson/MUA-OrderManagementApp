package com.example.cafecornerapp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.cafecornerapp.Adapter.CardProductAdapter
import com.example.cafecornerapp.Adapter.CardProductListCartAdapter
import com.example.cafecornerapp.DataStore.TransaksiPreference
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.CartViewModel
import com.example.cafecornerapp.ViewModel.ProductViewModel
import com.example.cafecornerapp.ViewModel.TransaksiViewModel
import com.example.cafecornerapp.databinding.ActivityCartBinding
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel = CartViewModel()

    private val viewModelTransaksi = TransaksiViewModel()

    private val viewModelImg = ProductViewModel()
    private lateinit var drawerLayout: DrawerLayout

    private val prefRepo = TransaksiPreference(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initHandleBuy()
        initSideBar()
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
//                R.id.menu_logout -> {
////                    logout()
//                }
            }
            drawerLayout.closeDrawers()
            true
        }
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
            prefRepo.getTransactionId().collect {
                viewModel.getCartByTransaksiId(it.toString())
            }
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

                    lifecycleScope.launch {
                        prefRepo.clearTransactionId()
                    }
                }

                viewModelTransaksi.updateStatus.observe(this) {
                    success ->
                    if (success) {
                        Toast.makeText(this, "Pesanan Sedang Diproses", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
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