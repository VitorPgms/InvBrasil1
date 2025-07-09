package com.vitordepaula.invbrasil.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

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

        val month = listOf(
            "Todos", "01/2025", "02/2025", "03/2025", "04/2025", "05/2025", "06/2025", "07/2025",
            "08/2025", "09/2025", "10/2025", "11/2025", "12/2025"
        )
        val adapterMonth = ArrayAdapter(this, android.R.layout.simple_spinner_item, month)
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMonth.adapter = adapterMonth

        binding.btnRegisterSale.setOnClickListener {
            val intent = Intent(this, ScreenSaleRegister::class.java)
            startActivity(intent)
        }

        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val monthSelected = month[position]
                loadSale(filter = if (monthSelected == "Todos") null else monthSelected)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        loadSale()

    }

    override fun onResume() {
        super.onResume()
        loadSale()
    }

    private fun loadSale(filter: String? = null){
        CoroutineScope(Dispatchers.IO).launch {
            val sales = saleDao.listAll()

            val saleFilter = if (filter == null) {
                sales
            } else {
                sales.filter { sale ->
                    val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val sdfFilter = SimpleDateFormat("MM/yyyy", Locale.getDefault())
                    val data = sdfInput.parse(sale.dataSale)
                    sdfFilter.format(data!!) == filter
                }
            }

            val bundledSale =  saleFilter
                .groupBy { sales ->
                    val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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