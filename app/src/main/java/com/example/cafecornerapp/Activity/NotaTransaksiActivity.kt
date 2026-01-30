package com.example.cafecornerapp.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.drawToBitmap
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cafecornerapp.Adapter.CardHistoryAdapter
import com.example.cafecornerapp.Adapter.ListItemNotaAdapter
import com.example.cafecornerapp.DataStore.UserPreference
import com.example.cafecornerapp.Domain.TransaksiWithCartModel
import com.example.cafecornerapp.Helper.HandlePrint
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.AuthViewModel
import com.example.cafecornerapp.ViewModel.UserViewModel
import com.example.cafecornerapp.databinding.ActivityNotaTransaksiBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NotaTransaksiActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNotaTransaksiBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userPreference: UserPreference

    private val userViewModel = UserViewModel()

    private val handlePrint = HandlePrint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotaTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

//        Get data from adapter history

        val data = intent.getSerializableExtra("object")
                as? TransaksiWithCartModel

        binding.notaPelanggan.text = data!!.cartItems[0].username.toString()
        binding.tvTanggalNota.text = data!!.transaksi.createdAt.toString()
        binding.tvTotalHarga.text = data!!.transaksi.totalHarga.toString()

        binding.rvNotaItem.layoutManager= LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        binding.rvNotaItem.adapter= ListItemNotaAdapter(data!!.cartItems.toMutableList())


        binding.cetakNotaBtn.setOnClickListener {

            val bitmap = binding.layoutNota.drawToBitmap()

            val pdfFile = createTempPdf(this, bitmap)

            previewPdf(this, pdfFile)

            Toast.makeText(
                this,
                "PDF tersimpan: ${pdfFile.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
        }

        initSideBar()
    }

    fun previewPdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        context.startActivity(intent)
    }

    fun createTempPdf(
        context: Context,
        bitmap: Bitmap
    ): File {
        val pdfDocument = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(
            bitmap.width,
            bitmap.height,
            1
        ).create()

        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "preview_nota.pdf")

        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        return file
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