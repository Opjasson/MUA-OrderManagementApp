package com.example.cafecornerapp.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cafecornerapp.Adapter.CardHistoryAdapter
import com.example.cafecornerapp.Adapter.CardProductListCartAdapter
import com.example.cafecornerapp.DataStore.UserPreference
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.AuthViewModel
import com.example.cafecornerapp.ViewModel.TransaksiViewModel
import com.example.cafecornerapp.ViewModel.UserViewModel
import com.example.cafecornerapp.databinding.ActivityHistoryTransaksiBinding
import kotlinx.coroutines.launch

class HistoryTransaksiActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHistoryTransaksiBinding
    private val viewModel = TransaksiViewModel()
    private val userViewModel = UserViewModel()
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initSideBar()
        initGetTransaksi()

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

    private fun initGetTransaksi () {
        userViewModel.getUserByUid()

        userViewModel.userLogin.observe(this) { user ->
            viewModel.loadTransaksiWithCart(user!!.documentId.toString())
        }


        viewModel.transaksiUI.observe(this){
            data ->
            Log.d("HISOTR", data.toString())
            binding.historyView.layoutManager= LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
            binding.historyView.adapter= CardHistoryAdapter(data.toMutableList())
            binding.loadHistory.visibility = View.GONE
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