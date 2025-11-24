package com.example.truequesperu.Fragmentos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.truequesperu.Models.Alert
import com.example.truequesperu.R
import com.example.truequesperu.databinding.FragmentDetalleAlertaBinding
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.*


class FragmentDetalleAlerta : Fragment() {

    private lateinit var binding: FragmentDetalleAlertaBinding
    private lateinit var alerta: Alert

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetalleAlertaBinding.inflate(inflater, container, false)

        // Obtener alerta desde el bundle
        alerta = arguments?.getSerializable("alerta") as Alert

        binding.txtUserName.text = alerta.userName
        binding.txtDescripcion.text = alerta.descripcion
        binding.txtFecha.text =
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date(alerta.timestamp))

        cargarMapa()

        binding.btnLlamar.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${alerta.userPhone}")
            startActivity(intent)
        }

        binding.btnVerRuta.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("google.navigation:q=${alerta.lat},${alerta.lon}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        return binding.root
    }

    private fun cargarMapa() {

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->

            val ubicacion = LatLng(alerta.lat, alerta.lon)

            googleMap.addMarker(
                MarkerOptions()
                    .position(ubicacion)
                    .title("Ubicación reportada")
            )

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(ubicacion, 16f)
            )
        }
    }
}