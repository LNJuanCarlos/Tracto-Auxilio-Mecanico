package com.example.truequesperu.Fragmentos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.truequesperu.Adapters.AlertAdapter
import com.example.truequesperu.Models.Alert
import com.example.truequesperu.R
import com.example.truequesperu.databinding.FragmentMisAlertasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FragmentMisAlertas : Fragment() {

    private lateinit var binding: FragmentMisAlertasBinding
    private lateinit var adapter: AlertAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance() // Para obtener el UID del cliente

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMisAlertasBinding.inflate(inflater, container, false)

        adapter = AlertAdapter(emptyList()) { alerta ->
            // Click en alerta si quieres mostrar detalle
        }

        binding.recyclerAlertas.adapter = adapter
        binding.recyclerAlertas.layoutManager = LinearLayoutManager(requireContext())

        // Cargar alertas del cliente al iniciar
        cargarAlertasCliente()

        return binding.root
    }

    private fun cargarAlertasCliente() {
        val usuarioId = auth.currentUser?.uid ?: return

        firestore.collection("alerts")
            .whereEqualTo("userId", usuarioId) // <-- aquí filtramos por el cliente
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FragmentMisAlertas", "Error al consultar alertas", error)
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Alert::class.java)
                } ?: emptyList()

                Log.d("FragmentMisAlertas", "Alertas recibidas: ${lista.size}")
                lista.forEach { Log.d("FragmentMisAlertas", it.toString()) }

                adapter.actualizarLista(lista)
            }
    }


    // Opcional: si quieres separar por estado
    fun filtrarPorEstado(estado: String) {
        val usuarioId = auth.currentUser?.uid ?: return

        firestore.collection("alerts")
            .whereEqualTo("userId", usuarioId)      // Cliente actual
            .whereEqualTo("status", estado)         // Estado: "Pendiente", "En camino", "Finalizado"
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Alert::class.java)
                } ?: emptyList()

                adapter.actualizarLista(lista)
            }
    }

    fun verAlertasPendientes() {
        firestore.collection("alerts")
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Alert::class.java)
                } ?: emptyList()

                adapter.actualizarLista(lista)
            }
    }

    fun verAlertasEnCamino() {
        firestore.collection("alerts")
            .whereEqualTo("estado", "en_camino")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Alert::class.java)
                } ?: emptyList()

                adapter.actualizarLista(lista)
            }
    }

    fun verAlertasAtendidas() {
        firestore.collection("alerts")
            .whereEqualTo("estado", "atendida")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Alert::class.java)
                } ?: emptyList()

                adapter.actualizarLista(lista)
            }
    }
}