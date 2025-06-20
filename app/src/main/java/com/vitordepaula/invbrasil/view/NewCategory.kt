package com.vitordepaula.invbrasil.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vitordepaula.invbrasil.AppDatabase
import com.vitordepaula.invbrasil.R
import com.vitordepaula.invbrasil.dao.CategoryDao
import com.vitordepaula.invbrasil.databinding.ActivityNewCategoryBinding
import com.vitordepaula.invbrasil.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewCategory : AppCompatActivity() {

    private lateinit var binding: ActivityNewCategoryBinding
    private lateinit var categoryDao: CategoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCategoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryDao = AppDatabase.getIntance(this).categoryDao()


        binding.btSaveCategory.setOnClickListener {
            val nome = binding.editNameCategory.text.toString()
            if (nome.isNotEmpty()){
                CoroutineScope(Dispatchers.IO).launch {
                   categoryDao.insert(Category(nome = nome))
                    runOnUiThread{
                        Toast.makeText(this@NewCategory, "Categoria Salva!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Digite um nome!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}