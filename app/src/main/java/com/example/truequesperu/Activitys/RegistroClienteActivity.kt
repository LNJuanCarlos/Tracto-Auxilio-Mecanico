package com.example.truequesperu.Activitys

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.truequesperu.R
import com.example.truequesperu.SunatViewModel


class RegistroClienteActivity : AppCompatActivity() {

    private lateinit var viewModel: SunatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_cliente)

        viewModel = ViewModelProvider(this).get(SunatViewModel::class.java)

        val btnBuscar = findViewById<Button>(R.id.btnConsultarRuc)
        val edtRuc = findViewById<EditText>(R.id.edtRuc)
        val edtNombre = findViewById<EditText>(R.id.edtRazonSocial)
        val edtComercial = findViewById<EditText>(R.id.edtNombreComercial)
        val edtDireccion = findViewById<EditText>(R.id.edtDireccion)

        btnBuscar.setOnClickListener {
            val ruc = edtRuc.text.toString()
            if (ruc.length == 11) {
                viewModel.buscarRuc(ruc)
            } else {
                Toast.makeText(this, "RUC inválido", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launchWhenStarted {

            viewModel.estado.collect { respuesta ->
                respuesta?.let {
                    edtNombre.setText(it.nombre)
                    edtComercial.setText(it.nombre)
                    edtDireccion.setText(it.direccion ?: "")
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collect { err ->
                err?.let {
                    Toast.makeText(this@RegistroClienteActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}