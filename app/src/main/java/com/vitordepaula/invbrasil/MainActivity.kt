package com.vitordepaula.invbrasil

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.vitordepaula.invbrasil.adapter.ProductAdapter
import com.vitordepaula.invbrasil.dao.ProductDao
import com.vitordepaula.invbrasil.databinding.ActivityMainBinding
import com.vitordepaula.invbrasil.model.Product
import com.vitordepaula.invbrasil.repository.ProductRepository
import com.vitordepaula.invbrasil.util.StockCalculator
import com.vitordepaula.invbrasil.view.RegisterProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.times

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var repository: ProductRepository
    private val _listProduct = MutableLiveData<MutableList<Product>>()
    private var showLowStock = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val dao = AppDatabase.getIntance(this).productDao()
        repository = ProductRepository(dao)

        _listProduct.observe(this@MainActivity) { listProduct ->
            val recyclerViewProduct = binding.recyclerViewProduct
            recyclerViewProduct.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerViewProduct.setHasFixedSize(true)
            productAdapter = ProductAdapter(this@MainActivity, listProduct)
            recyclerViewProduct.adapter = productAdapter
            calculateTotal(listProduct)
        }

        // Primeira carga dos produtos
        CoroutineScope(Dispatchers.IO).launch {
            loadAllProducts()
        }

        binding.btnRegister.setOnClickListener {
            val navigateRegistrationScreen = Intent(this, RegisterProduct::class.java)
            startActivity(navigateRegistrationScreen)
        }

        binding.btEstoqueBaixo.setOnClickListener {
            showLowStock = !showLowStock
            CoroutineScope(Dispatchers.IO).launch {
                if (showLowStock) {
                    filterLowStock()
                    withContext(Dispatchers.Main) {
                        binding.btEstoqueBaixo.text = "Mostrar todos os produtos"
                    }
                } else {
                    loadAllProducts()
                    withContext(Dispatchers.Main) {
                        binding.btEstoqueBaixo.text = "Mostrar apenas com estoque baixo"
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        // Atualiza a lista ao voltar do cadastro
        CoroutineScope(Dispatchers.IO).launch {
            if (showLowStock) {
                filterLowStock()
            } else {
                loadAllProducts()
            }
        }
    }

    private suspend fun loadAllProducts() {
        val listProduct = repository.getAll()
        _listProduct.postValue(listProduct)
    }

    private suspend fun filterLowStock() {
        val productFilter = repository.getLowStock()
        _listProduct.postValue(productFilter)
    }

    private fun calculateTotal(list: List<Product>){
        val (totalQuantity, totalAmount) = StockCalculator.calculateTotal(list)

        runOnUiThread{
            //binding.txtTotalQuantity.text = "Total de itens: $totalQuantity"
            //binding.txtTotalAmount.text = "Valor total : R$ %.2f".format(totalAmount)
        }
    }
}