package com.example.cafecornerapp.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cafecornerapp.Adapter.CardProductAdapter
import com.example.cafecornerapp.R
import com.example.cafecornerapp.ViewModel.CartViewModel
import com.example.cafecornerapp.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel = CartViewModel()

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

        binding.loadCart.visibility= View.VISIBLE
        viewModel..observe(this) {
                list ->
            binding.MPproductView.layoutManager= GridLayoutManager(this, 2)
            binding.MPproductView.adapter= CardProductAdapter(list.toMutableList())
            binding.MPLoadProduct.visibility= View.GONE
        }

        viewModel.loadAllItems()

    }
}