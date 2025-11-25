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
import com.example.truequesperu.R
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

        // NUEVOS BOTONES
        binding.btnEnviarAlerta.setOnClickListener {
            abrirFormulario("Alerta General")
        }

        binding.btnProgramarMantenimiento.setOnClickListener {
            abrirFormulario("Programación de Mantenimiento")
        }

        binding.btnVentaRepuestos.setOnClickListener {
            abrirFormulario("Venta de Repuestos")
        }

        return binding.root
    }

    private fun abrirFormulario(tipo: String) {
        val fragment = FragmentFormularioAlerta()
        val bundle = Bundle()
        bundle.putString("tipo", tipo)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.FragmentL1, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun prepararEnvio(tipoSolicitud: String) {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            solicitarPermisos()
            return
        }

        if (auth.currentUser == null) {
            Toast.makeText(requireContext(), "Debe iniciar sesión", Toast.LENGTH_SHORT).show()
            return
        }

        obtenerUbicacionYCrearSolicitud(tipoSolicitud)
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


    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionYCrearSolicitud(tipoSolicitud: String) {

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                Toast.makeText(requireContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val user = auth.currentUser!!
            val alertaId = UUID.randomUUID().toString()

            val alerta = Alert(
                id = alertaId,
                userId = user.uid,
                userName = user.displayName ?: "Cliente",
                userPhone = user.phoneNumber ?: "",
                lat = location.latitude,
                lon = location.longitude,
                descripcion = tipoSolicitud,    // ahora se guarda el tipo
                tipo = tipoSolicitud,          // NUEVO CAMPO
                status = "Pendiente",
                timestamp = System.currentTimeMillis()
            )

            firestore.collection("alerts")
                .document(alertaId)
                .set(alerta)
                .addOnSuccessListener {

                    // AHORA, después de crear registro, abrirás formulario (próximo paso)
                    irAFormulario(alerta)

                    Toast.makeText(requireContext(), "Solicitud iniciada", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al enviar", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun irAFormulario(alerta: Alert) {
        val fragment = FragmentFormularioAlerta()
        val bundle = Bundle()
        bundle.putSerializable("alerta", alerta)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.FragmentL1, fragment)
            .addToBackStack(null)
            .commit()
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