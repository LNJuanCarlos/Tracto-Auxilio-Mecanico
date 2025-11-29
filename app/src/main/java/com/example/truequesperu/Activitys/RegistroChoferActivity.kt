package com.example.truequesperu.Activitys

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.truequesperu.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroChoferActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etDni: EditText
    private lateinit var etLicencia: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etRucEmpresa: EditText
    private lateinit var btnRegistrarChofer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_chofer)

        initViews()
        initListeners()
    }

    private fun initViews() {
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etDni = findViewById(R.id.etDni)
        etLicencia = findViewById(R.id.etLicencia)
        etTelefono = findViewById(R.id.etTelefono)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        etRucEmpresa = findViewById(R.id.etRucEmpresa)
        btnRegistrarChofer = findViewById(R.id.btnRegistrarChofer)
    }

    private fun initListeners() {
        btnRegistrarChofer.setOnClickListener {
            registrarChofer()
        }
    }

    private fun validarRuc(ruc: String): Boolean {
        if (!ruc.matches(Regex("\\d{11}"))) return false

        val factores = intArrayOf(5,4,3,2,7,6,5,4,3,2)
        var suma = 0

        for (i in 0 until 10) {
            suma += ruc[i].toString().toInt() * factores[i]
        }

        val resto = suma % 11
        val digito = (11 - resto).let { if (it == 10) 0 else if (it == 11) 1 else it }

        return digito == ruc[10].toString().toInt()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun registrarChofer() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val licencia = etLicencia.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()
        val rucEmpresa = etRucEmpresa.text.toString().trim()

        // === VALIDACIONES AVANZADAS ===

        if (nombre.isEmpty() || apellido.isEmpty()) {
            toast("Ingrese nombre y apellido")
            return
        }

        // DNI → 8 dígitos + no repetido + no secuencial
        if (!dni.matches(Regex("\\d{8}"))) {
            toast("DNI inválido, debe tener 8 dígitos")
            return
        }
        if (dni.all { it == dni[0] }) {
            toast("DNI inválido (no puede ser repetido)")
            return
        }
        if (dni == "12345678" || dni == "87654321") {
            toast("DNI inválido (no puede ser secuencial)")
            return
        }

        // LICENCIA: formatos generales del MTC
        val licenciaRegex =
            Regex("^[A-Z]{1,3}\\d{6,8}$") // Ej. A12345678, AI12345678, B1234567
        if (!licencia.matches(licenciaRegex)) {
            toast("Licencia inválida (formato incorrecto)")
            return
        }

        // TELÉFONO: 9 dígitos y empieza en 9
        if (!telefono.matches(Regex("9\\d{8}"))) {
            toast("Teléfono inválido (debe iniciar en 9 y tener 9 dígitos)")
            return
        }

        // EMAIL válido
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        if (!correo.matches(emailRegex)) {
            toast("Correo inválido")
            return
        }

        // CONTRASEÑA: mínimo 6 + letras + números
        val passRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
        if (!contrasena.matches(passRegex)) {
            toast("Contraseña inválida (mínimo 6, incluye letra y número)")
            return
        }

        // RUC: 11 dígitos y dígito verificador válido
        if (!validarRuc(rucEmpresa)) {
            toast("RUC inválido")
            return
        }

        //toast("✔ Chofer validado correctamente (Aún sin backend)")

        registrarChoferFirebase(nombre, apellido, dni, licencia, telefono, correo, contrasena, rucEmpresa)
    }

    private fun registrarChoferFirebase(
        nombre: String,
        apellido: String,
        dni: String,
        licencia: String,
        telefono: String,
        correo: String,
        contrasena: String,
        rucEmpresa: String
    ) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(correo, contrasena)
            .addOnSuccessListener { auth ->

                val uid = auth.user!!.uid

                val data = hashMapOf(
                    "uid" to uid,
                    "tipoUsuario" to "CHOFER",
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "dni" to dni,
                    "licencia" to licencia,
                    "telefono" to telefono,
                    "correo" to correo,
                    "rucEmpresa" to rucEmpresa
                )

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(data)
                    .addOnSuccessListener {
                        toast("Chofer registrado correctamente")
                        finish()
                    }
                    .addOnFailureListener {
                        toast("Error guardando datos")
                    }
            }
            .addOnFailureListener {
                toast("Error creando usuario: ${it.message}")
            }
    }

}