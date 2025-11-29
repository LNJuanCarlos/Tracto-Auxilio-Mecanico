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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegistroClienteActivity : AppCompatActivity() {

    private lateinit var viewModel: SunatViewModel
    private lateinit var edtCorreo: EditText
    private lateinit var edtTelefono: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_cliente)

        viewModel = ViewModelProvider(this).get(SunatViewModel::class.java)

        val btnBuscar = findViewById<Button>(R.id.btnConsultarRuc)
        val edtRuc = findViewById<EditText>(R.id.edtRuc)
        val edtNombre = findViewById<EditText>(R.id.edtRazonSocial)
        val edtComercial = findViewById<EditText>(R.id.edtNombreComercial)
        val edtDireccion = findViewById<EditText>(R.id.edtDireccion)
        edtCorreo = findViewById(R.id.edtCorreo)
        edtTelefono = findViewById(R.id.edtTelefono)
        edtPassword = findViewById(R.id.edtPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            registrarCliente()
        }

        btnBuscar.setOnClickListener {
            val ruc = edtRuc.text.toString()

            if (ruc.length != 11) {
                Toast.makeText(this, "RUC inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.buscarRuc(ruc)
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

    private fun registrarCliente() {

        val ruc = findViewById<EditText>(R.id.edtRuc).text.toString()
        val razon = findViewById<EditText>(R.id.edtRazonSocial).text.toString()
        val comercial = findViewById<EditText>(R.id.edtNombreComercial).text.toString()
        val direccion = findViewById<EditText>(R.id.edtDireccion).text.toString()

        val correo = edtCorreo.text.toString().trim()
        val telefono = edtTelefono.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (correo.isEmpty() || telefono.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear usuario en FirebaseAuth
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, password)
            .addOnSuccessListener { auth ->
                val uid = auth.user!!.uid

                // Ahora guardamos datos en Firestore
                val data = hashMapOf(
                    "uid" to uid,
                    "tipoUsuario" to "CLIENTE",
                    "ruc" to ruc,
                    "razonSocial" to razon,
                    "nombreComercial" to comercial,
                    "direccion" to direccion,
                    "correo" to correo,
                    "telefono" to telefono
                )

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cliente registrado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error guardando datos", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al crear usuario: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}