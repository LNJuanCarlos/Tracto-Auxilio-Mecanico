package com.example.truequesperu.Opciones_Login

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.truequesperu.Activitys.MainClienteActivity
import com.example.truequesperu.Activitys.MainTecnicoActivity
import com.example.truequesperu.Activitys.SelectTipoUsuarioActivity
import com.example.truequesperu.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login_email : AppCompatActivity() {

        private lateinit var binding: ActivityLoginEmailBinding
        private lateinit var firebaseAuth : FirebaseAuth
        private lateinit var progressDialog : ProgressDialog
        private lateinit var sharedPref : SharedPreferences

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityLoginEmailBinding.inflate(layoutInflater)
            enableEdgeToEdge()
            setContentView(binding.root)

            firebaseAuth = FirebaseAuth.getInstance()
            sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)

            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Espere por favor")
            progressDialog.setCanceledOnTouchOutside(false)

            cargarDatosGuardados()

            binding.BtnIngresar.setOnClickListener {
                validarInfo()
            }

            binding.TxtRegistrarme.setOnClickListener {
                startActivity(Intent(this, SelectTipoUsuarioActivity::class.java))
            }

        }

    private fun cargarDatosGuardados() {
        val savedEmail = sharedPref.getString("username", "")
        val savedPassword = sharedPref.getString("password", "")

        binding.EtEmail.setText(savedEmail)
        binding.EtPassword.setText(savedPassword)

        if (!savedEmail.isNullOrEmpty()) {
            binding.cbRecordar.isChecked = true
        }
    }

        private var email = ""
        private var password = ""



        private fun validarInfo() {

            email = binding.EtEmail.text.toString().trim()
            password = binding.EtPassword.text.toString().trim()
            binding.cbRecordar.isChecked

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.EtEmail.error = "Email invalido"
                binding.EtEmail.requestFocus()
            }
            else if (email.isEmpty()){
                binding.EtEmail.error = "Ingrese Email"
                binding.EtEmail.requestFocus()
            }
            else if (password.isEmpty()){
                binding.EtPassword.error = "Ingrese Password"
                binding.EtPassword.requestFocus()
            }
            else{
                loginUsuario()
            }
        }

    private fun loginUsuario() {
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        if (binding.cbRecordar.isChecked) {
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString("username", email)
            editor.putString("password", password)
            editor.apply()
        }

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user!!.uid

                // AHORA LEEMOS EL TIPO DE USUARIO
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { snapshot ->

                        progressDialog.dismiss()

                        if (snapshot.exists()) {
                            val tipo = snapshot.getString("tipoUsuario") ?: ""

                            // Guardar rol en SharedPreferences
                            sharedPref.edit()
                                .putString("tipoUsuario", tipo)
                                .apply()

                            // Redirigir según rol
                            if (tipo == "CLIENTE") {
                                startActivity(Intent(this, MainClienteActivity::class.java))
                            } else {
                                startActivity(Intent(this, MainTecnicoActivity::class.java))
                            }

                            finishAffinity()
                            Toast.makeText(this, "Bienvenido(a)", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this, "No se encontró el usuario en Firestore", Toast.LENGTH_SHORT).show()
                        }

                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(this, "Error obteniendo datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "No se pudo iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    }