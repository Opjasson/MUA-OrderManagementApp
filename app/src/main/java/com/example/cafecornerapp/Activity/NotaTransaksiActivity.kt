package com.example.cafecornerapp.Activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cafecornerapp.Adapter.CardHistoryAdapter
import com.example.cafecornerapp.Adapter.ListItemNotaAdapter
import com.example.cafecornerapp.Domain.TransaksiWithCartModel
import com.example.cafecornerapp.Helper.HandlePrint
import com.example.cafecornerapp.R
import com.example.cafecornerapp.databinding.ActivityNotaTransaksiBinding
import java.io.File
import java.io.FileOutputStream

class NotaTransaksiActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNotaTransaksiBinding
    private val handlePrint = HandlePrint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotaTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        Get data from adapter history

        val data = intent.getSerializableExtra("object")
                as? TransaksiWithCartModel
        Log.d("DataHISTORU", data.toString())

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
}