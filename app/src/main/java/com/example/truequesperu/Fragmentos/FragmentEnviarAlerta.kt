package com.example.truequesperu.Fragmentos

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.truequesperu.Models.Alert
import com.example.truequesperu.databinding.FragmentEnviarAlertaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID


class FragmentEnviarAlerta : Fragment() {

    private lateinit var binding: FragmentEnviarAlertaBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val LOCATION_REQUEST_CODE = 2001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnviarAlertaBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.btnEnviarAlerta.setOnClickListener {
            validarYEnviar()
        }

        return binding.root
    }

    private fun solicitarPermisos() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }

    private fun validarYEnviar() {
        val descripcion = binding.txtDescripcion.text.toString().trim()

        if (descripcion.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese una descripción", Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            solicitarPermisos()
            return
        }

        // Si no hay usuario logueado, se cancela la acción
        if (auth.currentUser == null) {
            Toast.makeText(requireContext(), "Debe iniciar sesión para enviar alertas", Toast.LENGTH_SHORT).show()
            return
        }

        obtenerUbicacionYEnviar(descripcion)
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionYEnviar(descripcion: String) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                Toast.makeText(requireContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val currentUser = auth.currentUser!!
            val alertaId = UUID.randomUUID().toString()

            val alerta = Alert(
                id = alertaId,
                userId = currentUser.uid,
                userName = currentUser.displayName ?: "Cliente",
                userPhone = currentUser.phoneNumber ?: "",
                lat = location.latitude,
                lon = location.longitude,
                descripcion = descripcion,
                status = "Pendiente",  // Estado inicial
                timestamp = System.currentTimeMillis()
            )

            firestore.collection("alerts")
                .document(alertaId)
                .set(alerta)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Alerta enviada!", Toast.LENGTH_LONG).show()
                    binding.txtDescripcion.setText("")
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al enviar alerta", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}