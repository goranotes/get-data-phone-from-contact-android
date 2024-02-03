package com.example.getdataphonefromcontact

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.getdataphonefromcontact.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivContactPhone.setOnClickListener {
            Toast.makeText(this, "masuk",Toast.LENGTH_SHORT).show()
        }
    }
}