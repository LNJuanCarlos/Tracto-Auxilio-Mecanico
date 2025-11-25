package com.example.truequesperu.Fragmentos

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.truequesperu.Adapters.FotoAdapter
import com.example.truequesperu.Models.Alert
import com.example.truequesperu.Models.Vehiculo
import com.example.truequesperu.databinding.FragmentFormularioAlertaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class FragmentFormularioAlerta : Fragment() {

    private val listaFotosUri = mutableListOf<Uri>()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var fotoAdapter: FotoAdapter
    private val CAMERA_REQUEST_CODE  = 3001
    private lateinit var binding: FragmentFormularioAlertaBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var tipoSolicitud: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormularioAlertaBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fotoAdapter = FotoAdapter(listaFotosUri) { uriAEliminar ->
            fotoAdapter.eliminarFoto(uriAEliminar)
        }

        binding.recyclerFotos.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerFotos.adapter = fotoAdapter

        // Recibir tipo enviado desde el fragment anterior
        tipoSolicitud = arguments?.getString("tipo") ?: "Alerta"
        binding.txtTipo.text = tipoSolicitud

        binding.btnConfirmar.setOnClickListener {
            validarFormulario()
        }

        binding.btnTomarFoto.setOnClickListener {
            abrirCamara()
        }

        return binding.root
    }

    private val launcherCamara =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {

                val uri = guardarFotoTemporal(bitmap)
                listaFotosUri.add(uri)

                fotoAdapter.notifyDataSetChanged()
            }
        }

    private fun solicitarPermisoCamara() {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    private fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            solicitarPermisoCamara()
            return
        }

        launcherCamara.launch(null)
    }

    private fun guardarFotoTemporal(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "${UUID.randomUUID()}.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        fos.flush()
        fos.close()
        return file.toUri()
    }

    private fun validarFormulario() {
        val placa = binding.edtPlaca.text.toString().trim()
        val modelo = binding.edtModelo.text.toString().trim()
        val anio = binding.edtAnio.text.toString().trim()
        val vin = binding.edtVIN.text.toString().trim()
        val descripcion = binding.edtDescripcion.text.toString().trim()

        if (placa.isEmpty() || modelo.isEmpty() || anio.isEmpty() || vin.isEmpty()) {
            Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        obtenerUbicacionYEnviar(placa, modelo, anio, vin, descripcion)
    }
    private fun subirFotosAFirebase(
        alertaId: String,
        callback: (List<String>) -> Unit
    ) {
        if (listaFotosUri.isEmpty()) {
            callback(emptyList())
            return
        }

        val urls = mutableListOf<String>()
        var fotosSubidas = 0

        listaFotosUri.forEach { uri ->
            val ref = storage.reference
                .child("alertas/$alertaId/${UUID.randomUUID()}.jpg")

            ref.putFile(uri)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { url ->
                    urls.add(url.toString())
                    fotosSubidas++

                    if (fotosSubidas == listaFotosUri.size) {
                        callback(urls)
                    }
                }
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionYEnviar(
        placa: String,
        modelo: String,
        anio: String,
        vin: String,
        descripcion: String
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                Toast.makeText(
                    requireContext(),
                    "No se pudo obtener la ubicación",
                    Toast.LENGTH_SHORT
                ).show()
                return@addOnSuccessListener
            }

            val user = auth.currentUser!!
            val id = UUID.randomUUID().toString()

            val vehiculo = Vehiculo(
                placa = placa,
                modelo = modelo,
                anio = anio,
                vin = vin
            )

            subirFotosAFirebase(id) { urls ->

                val alerta = Alert(
                    id = id,
                    userId = user.uid,
                    userName = user.displayName ?: "Cliente",
                    userPhone = user.phoneNumber ?: "",
                    tipo = tipoSolicitud,
                    vehiculo = vehiculo,
                    lat = location.latitude,
                    lon = location.longitude,
                    descripcion = descripcion,
                    fotos = urls,                     // <--- AQUI VAN LAS URLS
                    status = "Pendiente",
                    timestamp = System.currentTimeMillis()
                )

                firestore.collection("alerts")
                    .document(id)
                    .set(alerta)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Solicitud enviada", Toast.LENGTH_LONG)
                            .show()
                    }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                abrirCamara()
            } else {
                Toast.makeText(requireContext(), "Se necesita permiso de cámara", Toast.LENGTH_LONG).show()
            }
        }
    }
}