package com.vitordepaula.invbrasil.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vitordepaula.invbrasil.AppDatabase
import com.vitordepaula.invbrasil.MainActivity
import com.vitordepaula.invbrasil.R
import com.vitordepaula.invbrasil.databinding.ActivityScreenDashboardBinding
import com.vitordepaula.invbrasil.repository.ProductRepository
import com.vitordepaula.invbrasil.util.StockCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScreenDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityScreenDashboardBinding
    private lateinit var repository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityScreenDashboardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dao = AppDatabase.getIntance(this).productDao()
        repository = ProductRepository(dao)


        binding.btnListProduct.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnCategory.setOnClickListener{
            val intent = Intent(this, ScreenSale::class.java)
            startActivity(intent)
        }

        loadTotais()
    }

    override fun onResume() {
        super.onResume()
        loadTotais()
    }

    private fun loadTotais() {
        CoroutineScope(Dispatchers.IO).launch {
            val product = repository.getAll()
            val (totalQuantity, totalAmount) = StockCalculator.calculateTotal(product)

            val lowStock = repository.getLowStock()
            val totalLowStock = lowStock.size

            withContext(Dispatchers.Main) {
                binding.txtProduct.text = "$totalQuantity"
                binding.txtTotalAmount.text = "R$%.2f".format(totalAmount)
                binding.txtLowStock.text = "$totalLowStock"
            }
        }
    }
}