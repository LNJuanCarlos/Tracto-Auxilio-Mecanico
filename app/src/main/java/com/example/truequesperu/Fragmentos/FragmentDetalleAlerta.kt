package com.example.truequesperu.Fragmentos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.truequesperu.Adapters.FotoAdapter
import com.example.truequesperu.Models.Alert
import com.example.truequesperu.PhotoUrlAdapter
import com.example.truequesperu.R
import com.example.truequesperu.databinding.FragmentDetalleAlertaBinding
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class FragmentDetalleAlerta : Fragment() {

    private lateinit var binding: FragmentDetalleAlertaBinding
    private var alerta: Alert? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var photoAdapter: PhotoUrlAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alerta = arguments?.getSerializable("alerta") as? Alert
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetalleAlertaBinding.inflate(inflater, container, false)

        // Inicializar adapter de fotos horizontal
        photoAdapter = PhotoUrlAdapter(emptyList())
        binding.recyclerFotos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerFotos.adapter = photoAdapter

        alerta?.let { a ->
            // Datos básicos
            binding.txtUserName.text = a.userName
            binding.txtDescripcion.text = a.descripcion
            binding.txtFecha.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(a.timestamp))
            binding.txtEstado.text = a.status

            // Datos del vehículo (pueden ser null/default)
            val veh = a.vehiculo
            binding.txtPlaca.text = "Placa: ${veh?.placa ?: "-"}"
            binding.txtModelo.text = "Modelo: ${veh?.modelo ?: "-"}"
            binding.txtAnio.text = "Año: ${veh?.anio ?: "-"}"
            binding.txtVin.text = "VIN: ${veh?.vin ?: "-"}"

            // Fotos (urls)
            if (!a.fotos.isNullOrEmpty()) {
                binding.recyclerFotos.visibility = View.VISIBLE
                photoAdapter.updateList(a.fotos)
            } else {
                binding.recyclerFotos.visibility = View.GONE
            }

            // Mapa: usar el SupportMapFragment que definiste en el XML
            val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as? SupportMapFragment
            mapFragment?.getMapAsync { googleMap ->
                val pos = LatLng(a.lat, a.lon)
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(pos).title("Ubicación"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
            }

            // Llamar
            binding.btnLlamar.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${a.userPhone}"))
                startActivity(intent)
            }

            // Ver ruta en Google Maps
            binding.btnVerRuta.setOnClickListener {
                val gmmIntentUri = Uri.parse("google.navigation:q=${a.lat},${a.lon}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }

            // Acciones de técnico: actualizar estado
            binding.btnEnCamino.setOnClickListener { actualizarEstado(a, "En camino") }
            binding.btnEnAtencion.setOnClickListener { actualizarEstado(a, "En atención") }
            binding.btnFinalizar.setOnClickListener { actualizarEstado(a, "Finalizado") }

            // Si quieres deshabilitar botones según estado actual:
            actualizarBotonesSegunEstado(a.status)
        }

        return binding.root
    }

    private fun actualizarBotonesSegunEstado(status: String) {
        // Ejemplo simple: si ya está Finalizado, ocultar botones
        when (status) {
            "Pendiente" -> {
                binding.btnEnCamino.isEnabled = true
                binding.btnEnAtencion.isEnabled = false
                binding.btnFinalizar.isEnabled = false
            }
            "En camino" -> {
                binding.btnEnCamino.isEnabled = false
                binding.btnEnAtencion.isEnabled = true
                binding.btnFinalizar.isEnabled = false
            }
            "En atención" -> {
                binding.btnEnCamino.isEnabled = false
                binding.btnEnAtencion.isEnabled = false
                binding.btnFinalizar.isEnabled = true
            }
            "Finalizado" -> {
                binding.btnEnCamino.isEnabled = false
                binding.btnEnAtencion.isEnabled = false
                binding.btnFinalizar.isEnabled = false
            }
            else -> {
                binding.btnEnCamino.isEnabled = true
                binding.btnEnAtencion.isEnabled = true
                binding.btnFinalizar.isEnabled = true
            }
        }
    }

    private fun actualizarEstado(a: Alert, nuevoEstado: String) {
        val tecnico = auth.currentUser
        val update = mapOf(
            "status" to nuevoEstado,
            "tecnicoId" to (tecnico?.uid ?: "tecnico_demo"),
            "tecnicoName" to (tecnico?.displayName ?: "Técnico")
        )

        db.collection("alerts").document(a.id)
            .update(update)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Estado actualizado a: $nuevoEstado", Toast.LENGTH_SHORT).show()
                // actualizar UI localmente
                binding.txtEstado.text = nuevoEstado
                actualizarBotonesSegunEstado(nuevoEstado)
                // opcional: cerrar detalle y volver al listado
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error actualizando estado: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}