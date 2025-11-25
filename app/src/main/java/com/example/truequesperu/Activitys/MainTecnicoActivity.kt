package com.example.truequesperu.Activitys

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.truequesperu.Adapters.AlertAdapter
import com.example.truequesperu.Fragmentos.FragmentCuenta
import com.example.truequesperu.Fragmentos.FragmentDetalleAlerta
import com.example.truequesperu.Models.Alert
import com.example.truequesperu.R
import com.example.truequesperu.databinding.ActivityMainTecnicoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainTecnicoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainTecnicoBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: AlertAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTecnicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MENU TÉCNICO (usa menu_tecnico.xml)
        binding.BottomNV.menu.clear()
        binding.BottomNV.inflateMenu(R.menu.menu_tecnico)

        // OCULTAR FAB (el técnico no envía alertas)
        binding.FAB.visibility = View.GONE

        // CONFIGURAR ADAPTER Y RECYCLER
        adapter = AlertAdapter(emptyList()) { alerta -> abrirDetalleAlerta(alerta) }
        binding.recyclerAlertas.layoutManager = LinearLayoutManager(this)
        binding.recyclerAlertas.adapter = adapter

        // Mostrar alertas pendientes al iniciar
        verAlertasPendientes()

        // Navegación inferior: cambia la consulta
        binding.BottomNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Item_Inicio -> {
                    mostrarListaAlertas()
                    verAlertasPendientes()
                    true
                }
                R.id.Item_Alertas_En_Camino -> {
                    mostrarListaAlertas()
                    verAlertasEnCamino()
                    true
                }
                R.id.Item_Alertas_Atendidas -> {
                    mostrarListaAlertas()
                    verAlertasAtendidas()
                    true
                }
                R.id.Item_Cuenta -> {
                    abrirFragmentCuenta()
                    true
                }
                else -> false
            }
        }
    }

    private fun verAlertasPendientes() {
        binding.TituloRL.text = "Pendientes"
        firestore.collection("alerts")
            .whereEqualTo("status", "Pendiente")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val lista = snapshot?.documents?.mapNotNull { it.toObject(Alert::class.java) } ?: emptyList()
                adapter.actualizarLista(lista)
            }
    }

    private fun verAlertasEnCamino() {
        binding.TituloRL.text = "En Camino"
        firestore.collection("alerts")
            .whereEqualTo("status", "En camino")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val lista = snapshot?.documents?.mapNotNull { it.toObject(Alert::class.java) } ?: emptyList()
                adapter.actualizarLista(lista)
            }
    }

    private fun verAlertasAtendidas() {
        binding.TituloRL.text = "Atendidas"
        firestore.collection("alerts")
            .whereEqualTo("status", "Finalizado")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val lista = snapshot?.documents?.mapNotNull { it.toObject(Alert::class.java) } ?: emptyList()
                adapter.actualizarLista(lista)
            }
    }

    private fun abrirDetalleAlerta(alerta: Alert) {
        // ocultar lista para mostrar fragment
        binding.recyclerAlertas.visibility = View.GONE

        val fragment = FragmentDetalleAlerta()
        val bundle = Bundle()
        bundle.putSerializable("alerta", alerta)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentL1.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun abrirFragmentCuenta() {
        binding.recyclerAlertas.visibility = View.GONE
        binding.TituloRL.text = "Cuenta"
        val fragment = FragmentCuenta()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentL1.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun mostrarListaAlertas() {
        binding.recyclerAlertas.visibility = View.VISIBLE
        binding.TituloRL.text = "Alertas"
        // remover fragment si existe
        val fragment = supportFragmentManager.findFragmentById(binding.FragmentL1.id)
        fragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
    }

    // manejo back para reactivar recyclerView
    override fun onBackPressed() {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
            if (fm.backStackEntryCount == 1) binding.recyclerAlertas.visibility = View.VISIBLE
        } else super.onBackPressed()
    }
}