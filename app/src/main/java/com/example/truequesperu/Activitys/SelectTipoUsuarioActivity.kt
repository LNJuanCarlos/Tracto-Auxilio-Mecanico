package com.example.truequesperu.Activitys

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.truequesperu.databinding.ActivitySelectTipoUsuarioBinding


class SelectTipoUsuarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectTipoUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTipoUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEmpresa.setOnClickListener {
            startActivity(Intent(this, RegistroClienteActivity::class.java))
        }

        binding.btnChofer.setOnClickListener {
            startActivity(Intent(this, RegistroChoferActivity::class.java))
        }
    }
}