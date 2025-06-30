package com.vitordepaula.invbrasil.view

import android.content.Intent
import android.os.Bundle

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vitordepaula.invbrasil.AppDatabase
import com.vitordepaula.invbrasil.R
import com.vitordepaula.invbrasil.adapter.BundledSaleAdapter
import com.vitordepaula.invbrasil.dao.SaleDao
import com.vitordepaula.invbrasil.databinding.ActivityScreenSaleBinding
import com.vitordepaula.invbrasil.model.BundledSale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale


class ScreenSale : AppCompatActivity() {

    private lateinit var binding: ActivityScreenSaleBinding
    private lateinit var saleDao: SaleDao
    private lateinit var adapter: BundledSaleAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenSaleBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        saleDao = AppDatabase.getIntance(this).saleDao()

        binding.btnRegisterSale.setOnClickListener {
            val intent = Intent(this, ScreenSaleRegister::class.java)
            startActivity(intent)
        }

        loadSale()

    }

    override fun onResume() {
        super.onResume()
        loadSale()
    }

    private fun loadSale(){
        CoroutineScope(Dispatchers.IO).launch {
            val sales = saleDao.listAll()

            val bundledSale =  sales
                .groupBy { sales ->
                    val sdfInput = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val sdfMonthYear = SimpleDateFormat("MM/yyyy", Locale.getDefault())

                    val data = sdfInput.parse(sales.dataSale)
                    sdfMonthYear.format(data!!)
                }
                .map { (monthYear, salesMouth) ->
                    BundledSale(monthYear, salesMouth)
                }

            withContext(Dispatchers.Main) {
                adapter = BundledSaleAdapter(this@ScreenSale, bundledSale)
                binding.recyclerViewVendas.adapter = adapter
                binding.recyclerViewVendas.layoutManager = LinearLayoutManager(this@ScreenSale)
            }
        }
    }
}