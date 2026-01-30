package com.example.cafecornerapp.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.text.TextPaint
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cafecornerapp.DataStore.UserPreference
import com.example.cafecornerapp.Domain.LaporanModel
import com.example.cafecornerapp.Helper.ConvertDateTime
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.AuthViewModel
import com.example.cafecornerapp.ViewModel.TransaksiViewModel
import com.example.cafecornerapp.ViewModel.UserViewModel
import com.example.cafecornerapp.databinding.ActivityLaporanTransactionBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.compareTo

class LaporanTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaporanTransactionBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userPreference: UserPreference

    private val userViewModel = UserViewModel()
    private val localIDR = ConvertDateTime()
    private val viewModel = TransaksiViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLaporanTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val etDateRange = binding.etDateRange

        etDateRange.setOnClickListener {
            showDateRangePicker(etDateRange)
        }

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

    private fun showDateRangePicker(editText: TextInputEditText) {

        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Pilih Rentang Tanggal")
            .build()

        picker.show(supportFragmentManager, "DATE_RANGE_PICKER")

        picker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

            val start = formatter.format(Date(startDate))
            val end = formatter.format(Date(endDate))

            editText.setText("$start  -  $end")

            val table = binding.tableHarga

//            viewModelRekap.loadData(start, end)

            viewModel.loadTransaksiLaporan(start, end)
//            viewModel.transaksiLaporan.observe(this){
//                data ->
//                Log.d("DATALAPORAN", data.toString())
//            }
            viewModel.transaksiLaporan.observe(this){
                    list ->

                // 1️⃣ HAPUS SEMUA ROW DATA (kecuali header)
                val childCount = table.childCount
                if (childCount > 1) {
                    table.removeViews(1, childCount - 1)
                }
                Log.d("LISTBARANGMASUK", list.toString())

                binding.cetakBtn.setOnClickListener {
                    generatePdf(this, list)
                }

                var totalKeseluruhan : Long = 0
                var nomor : Long = 1
                list.forEachIndexed { index2, prod ->

                    prod.cartItems.forEachIndexed {
                        index, item ->
                        val row = TableRow(this)
                        row.setBackgroundColor(Color.WHITE)

                        val subtotal = item.jumlah * item.harga
                        totalKeseluruhan += subtotal

                        row.addView(createCell(nomor.toString(), Gravity.CENTER))
                        row.addView(createCell(item.createdAt.toString(), Gravity.CENTER))
                        row.addView(createCell(item.nama.toString(), Gravity.CENTER))
                        row.addView(createCell(item.jumlah.toString(), Gravity.START))
                        row.addView(createCell(item.harga.toString(), Gravity.START))
                        row.addView(createCell(
                            subtotal.toString(), Gravity.END)
                        )
                        nomor++
                        table.addView(row)
                    }
                }
                binding.totalKeseluruhanTxt.text = "Total Akhir : " + localIDR.formatRupiah(totalKeseluruhan).toString()

            }


        }
    }

    private fun createCell(text: String, gravity: Int): TextView {
        return TextView(this).apply {
            this.text = text
            this.gravity = gravity
            setPadding(8, 8, 8, 8)
            setBackgroundResource(android.R.drawable.editbox_background)
        }
    }

    fun generatePdf(context: Context, data: List<LaporanModel>) {

        val pdfDocument = PdfDocument()

        val textPaint = TextPaint().apply {
            textSize = 12f
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        // Ukuran A4 (pt)
        val pageWidth = 595
        val pageHeight = 842

        // Layout table
        val startX = 30f
        val startY = 100f
        val rowHeight = 28f
        val bottomMargin = 800f

        // Kolom X
        val colNo = startX
        val colNama = startX + 40
        val colAwal = startX + 180
        val colMasuk = startX + 270
        val colKeluar = startX + 360
        val colAkhir = startX + 450

        fun drawTextSafe(
            canvas: Canvas,
            text: String,
            x: Float,
            y: Float,
            maxWidth: Float
        ) {
            val safeText = TextUtils.ellipsize(
                text,
                textPaint,
                maxWidth,
                TextUtils.TruncateAt.END
            ).toString()

            canvas.drawText(safeText, x, y, textPaint)
        }

        fun drawHeader(canvas: Canvas, y: Float) {
            drawTextSafe(canvas, "No", colNo, y, 30f)
            drawTextSafe(canvas, "Tanggal", colNama, y, 120f)
            drawTextSafe(canvas, "Nama Menu", colAwal, y, 70f)
            drawTextSafe(canvas, "Jumlah", colMasuk, y, 70f)
            drawTextSafe(canvas, "Harga", colKeluar, y, 70f)
            drawTextSafe(canvas, "Total", colAkhir, y, 70f)

            canvas.drawLine(20f, y + 5f, pageWidth - 20f, y + 5f, linePaint)
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Judul
        canvas.drawText("Laporan Data Penjualan", 40f, 50f, titlePaint)

        var y = startY
        drawHeader(canvas, y)
        y += rowHeight


            var totalKeseluruhan : Long = 0
            var nomor : Long = 1

        data.forEach { laporan ->

            laporan.cartItems.forEach { item ->

                // pindah halaman
                if (y > bottomMargin) {
                    pdfDocument.finishPage(page)

                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas

                    canvas.drawText("Laporan Data Penjualan", 40f, 50f, titlePaint)

                    y = startY
                    drawHeader(canvas, y)
                    y += rowHeight
                }

                val subtotal = item.jumlah * item.harga
                totalKeseluruhan += subtotal

                drawTextSafe(canvas, nomor.toString(), colNo, y, 30f)
                drawTextSafe(canvas, item.createdAt ?: "-", colNama, y, 120f)
                drawTextSafe(canvas, item.nama, colAwal, y, 70f)
                drawTextSafe(canvas, item.jumlah.toString(), colMasuk, y, 70f)
                drawTextSafe(canvas, localIDR.formatRupiah(item.harga), colKeluar, y, 70f)
                drawTextSafe(canvas, localIDR.formatRupiah(subtotal), colAkhir, y, 70f)

                y += rowHeight
                nomor++
            }
        }
//            binding.totalKeseluruhanTxt.text =
        y += 10f
        canvas.drawLine(20f, y, pageWidth - 20f, y, linePaint)
        y += 25f

        canvas.drawText(
            "TOTAL KESELURUHAN : ${localIDR.formatRupiah(totalKeseluruhan).toString()}",
            colAkhir - 200,
            y,
            titlePaint
        )

        pdfDocument.finishPage(page)

        val file = File(context.getExternalFilesDir(null), "laporan_penjualan.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        Toast.makeText(context, "PDF berhasil dibuat", Toast.LENGTH_SHORT).show()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    }
}