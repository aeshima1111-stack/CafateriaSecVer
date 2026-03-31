package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteria.databinding.ActivityAdminPageBinding

class AdminPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAdminPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.cardadd.setOnClickListener {
            startActivity(Intent(this, AddDataActivity::class.java))
        }
        binding.cardview.setOnClickListener {
            startActivity(Intent(this, AdminViewActivity::class.java))
        }
        binding.cardhistory.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
        binding.cardsale.setOnClickListener {
            Toast.makeText(this, "button clicked", Toast.LENGTH_SHORT).show()
        }
        binding.cardinventory.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

    }
}