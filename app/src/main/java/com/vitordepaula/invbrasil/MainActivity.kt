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
import com.vitordepaula.invbrasil.view.RegisterProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productDao: ProductDao
    private lateinit var productAdapter: ProductAdapter
    private val _listProduct = MutableLiveData<MutableList<Product>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            getProduct()

            withContext(Dispatchers.Main){
                _listProduct.observe(this@MainActivity){ listProduct ->
                    val recyclerViewProduct = binding.recyclerViewProduct
                    recyclerViewProduct.layoutManager = LinearLayoutManager(this@MainActivity)
                    recyclerViewProduct.setHasFixedSize(true)
                    productAdapter = ProductAdapter(this@MainActivity, listProduct)
                    recyclerViewProduct.adapter = productAdapter
                    productAdapter.notifyDataSetChanged()

                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btCadastrar.setOnClickListener {
            val navigateRegistrationScreen = Intent(this, RegisterProduct::class.java)
            startActivity(navigateRegistrationScreen)
        }

    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.IO).launch {
            getProduct()

            withContext(Dispatchers.Main){
                _listProduct.observe(this@MainActivity) { listProduct ->
                    val recyclerViewProduct = binding.recyclerViewProduct
                    recyclerViewProduct.layoutManager = LinearLayoutManager(this@MainActivity)
                    recyclerViewProduct.setHasFixedSize(true)
                    productAdapter = ProductAdapter(this@MainActivity, listProduct)
                    recyclerViewProduct.adapter = productAdapter
                    productAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun getProduct(){
        productDao = AppDatabase.getIntance(this).productDao()
        val listProduct: MutableList<Product> = productDao.get()
        _listProduct.postValue(listProduct)
    }
}