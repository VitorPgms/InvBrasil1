package com.vitordepaula.invbrasil.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vitordepaula.invbrasil.AppDatabase
import com.vitordepaula.invbrasil.R
import com.vitordepaula.invbrasil.dao.ProductDao
import com.vitordepaula.invbrasil.dao.SaleDao
import com.vitordepaula.invbrasil.databinding.ActivityScreenSaleBinding
import com.vitordepaula.invbrasil.model.Product
import com.vitordepaula.invbrasil.model.Sale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenSale : AppCompatActivity() {

    private lateinit var binding: ActivityScreenSaleBinding
    private lateinit var productDao: ProductDao
    private lateinit var saleDao: SaleDao
    private lateinit var product: List<Product>


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

        val db = AppDatabase.getIntance(this)
        productDao = db.productDao()
        saleDao = db.saleDao()

        loadProduct()

        binding.btnRegisterSale.setOnClickListener {
            saveSale()
        }
    }

    private fun loadProduct(){
        CoroutineScope(Dispatchers.IO).launch {
            product = productDao.get()
            val names = product.map { it.nome }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@ScreenSale, android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerProdutos.adapter = adapter
            }
        }
    }

    private fun saveSale() {
        CoroutineScope(Dispatchers.IO).launch {
            val name = binding.editNameClient.text.toString()
            val address = binding.editAdressClient.text.toString()
            val quantitySale = binding.editQuantity.text.toString().toIntOrNull() ?: 0
            val productSelect = product[binding.spinnerProdutos.selectedItemPosition]

            if (name.isNotBlank() && address.isNotBlank() && quantitySale > 0) {
                val newQuantity = (productSelect.quantidade.toIntOrNull() ?: 0) - quantitySale
                val productUpdate = productSelect.copy(quantidade = newQuantity.toString()) //Erro talvez aqui
                productDao.update(productUpdate)

                val sale = Sale(
                    nameClient = name,
                    address = address,
                    productId = productSelect.uid,
                    nameProduct = productSelect.nome,
                    quantitySale = quantitySale,
                    dataSale = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                )

                saleDao.insert(sale)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ScreenSale, "Venda Registrada com Sucesso", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ScreenSale, "Preencha Todos os Campos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}