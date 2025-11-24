package com.example.truequesperu.Activitys

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.truequesperu.Adapters.AlertAdapter
import com.example.truequesperu.Fragmentos.FragmentCuenta
import com.example.truequesperu.Fragmentos.FragmentDetalleAlerta
import com.example.truequesperu.Models.Alert
import com.example.truequesperu.R
import com.example.truequesperu.databinding.ActivityMainTecnicoBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainTecnicoActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainTecnicoBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: AlertAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainTecnicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MENU TÉCNICO
        binding.BottomNV.menu.clear()
        binding.BottomNV.inflateMenu(R.menu.menu_tecnico)

        // OCULTAR FAB
        binding.FAB.visibility = View.GONE

        // CONFIGURAR ADAPTER
        adapter = AlertAdapter(emptyList()) { alerta ->
            abrirDetalleAlerta(alerta)
        }

        /*binding.recyclerAlertas.layoutManager = LinearLayoutManager(this)
        binding.recyclerAlertas.adapter = adapter*/

        // CARGAR ALERTAS INICIALMENTE
        verAlertasPendientes()

        binding.BottomNV.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.Item_Inicio -> { verAlertasPendientes(); true }
                R.id.Item_Alertas_En_Camino -> { verAlertasEnCamino(); true }
                R.id.Item_Alertas_Atendidas -> { verAlertasAtendidas(); true }
                R.id.Item_Cuenta -> { verFragmentCuenta(); true }
                else -> false
            }
        }
    }

    private fun verAlertasPendientes() {
        binding.TituloRL.text = "Pendientes"

        firestore.collection("alerts")
            .whereEqualTo("status", "Pendiente")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val lista = snapshot?.documents?.mapNotNull { it.toObject(Alert::class.java) }
                    ?: emptyList()

                adapter.actualizarLista(lista)
            }
    }

    private fun verAlertasEnCamino() {
        binding.TituloRL.text = "En Camino"

        firestore.collection("alerts")
            .whereEqualTo("status", "En camino")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val lista = snapshot?.documents?.mapNotNull { it.toObject(Alert::class.java) }
                    ?: emptyList()

                adapter.actualizarLista(lista)
            }
    }

    private fun verAlertasAtendidas() {
        binding.TituloRL.text = "Atendidas"

        firestore.collection("alerts")
            .whereEqualTo("status", "Finalizado")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val lista = snapshot?.documents?.mapNotNull { it.toObject(Alert::class.java) }
                    ?: emptyList()

                adapter.actualizarLista(lista)
            }
    }

    private fun abrirDetalleAlerta(alerta: Alert) {
        val fragment = FragmentDetalleAlerta()
        val bundle = Bundle()
        bundle.putSerializable("alerta", alerta)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentL1.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun verFragmentCuenta(){
        binding.TituloRL.text = "Cuenta"
        val fragment = FragmentCuenta()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentL1.id, fragment, "FragmentCuenta")
            .commit()
    }
}