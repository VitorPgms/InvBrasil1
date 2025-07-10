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
import com.vitordepaula.invbrasil.dao.CategoryDao
import com.vitordepaula.invbrasil.databinding.ActivityScreenEditCategoryBinding
import com.vitordepaula.invbrasil.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScreenEditCategory : AppCompatActivity() {

    private lateinit var binding: ActivityScreenEditCategoryBinding
    private lateinit var categoryDao: CategoryDao
    private lateinit var category: List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenEditCategoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryDao = AppDatabase.getIntance(this).categoryDao()

        loadCategory()

        binding.btnUpdateCategory.setOnClickListener {
            updateCategory()
        }
    }

    private fun loadCategory(){
        CoroutineScope(Dispatchers.IO).launch {
            category = categoryDao.getAll()
            val names = category.map { it.nome }

            withContext(Dispatchers.Main){
                val adapter = ArrayAdapter(this@ScreenEditCategory, android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerEditCategory.adapter = adapter
            }
        }
    }

    private fun updateCategory() {
        val newDescription = binding.editTextCategory.text.toString()
        val positionSelected = binding.spinnerEditCategory.selectedItemPosition
        val category = category.getOrNull(positionSelected)

        if (category != null && newDescription.isNotBlank()) {
            val categoryUpdate = category.copy(nome = newDescription)

            CoroutineScope(Dispatchers.IO).launch {
                categoryDao.update(categoryUpdate)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ScreenEditCategory,
                        "Categoria Atualizada",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

}