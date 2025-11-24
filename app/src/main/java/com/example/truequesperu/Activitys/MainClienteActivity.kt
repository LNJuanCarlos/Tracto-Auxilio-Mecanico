package com.example.truequesperu.Activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.truequesperu.Fragmentos.FragmentChats
import com.example.truequesperu.Fragmentos.FragmentCuenta
import com.example.truequesperu.Fragmentos.FragmentEnviarAlerta
import com.example.truequesperu.Fragmentos.FragmentInicio
import com.example.truequesperu.Fragmentos.FragmentMisAlertas
import com.example.truequesperu.R
import com.example.truequesperu.databinding.ActivityMainClienteBinding

class MainClienteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.BottomNV.inflateMenu(R.menu.menu_cliente)

        verFragmentInicio()

        binding.FAB.setOnClickListener {
            abrirPantallaEnviarAlerta()
        }

        binding.BottomNV.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.Item_Inicio -> { verFragmentInicio(); true }
                R.id.Item_Chats -> { verFragmentChats(); true }
                R.id.Item_Mis_Alertas -> { verFragmentMisAlertas(); true }
                R.id.Item_Cuenta -> { verFragmentCuenta(); true }
                else -> false
            }
        }
    }
    private fun abrirPantallaEnviarAlerta() {
        binding.TituloRL.text = "Enviar alerta"
        val fragment = FragmentEnviarAlerta()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentL1.id, fragment, "FragmentEnviarAlerta")
            .addToBackStack(null)
            .commit()
    }

    private fun verFragmentInicio(){
        binding.TituloRL.text = "Inicio"
        val fragment = FragmentInicio()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentL1.id, fragment, "FragmentInicio")
        fragmentTransaction.commit()

    }

    private fun verFragmentChats(){
        binding.TituloRL.text = "Chats"
        val fragment = FragmentChats()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentL1.id, fragment, "FragmentChats")
        fragmentTransaction.commit()

    }

    private fun verFragmentMisAlertas(){
        binding.TituloRL.text = "MisAnuncios"
        val fragment = FragmentMisAlertas()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentL1.id, fragment, "FragmentMisAlertas")
        fragmentTransaction.commit()

    }

    private fun verFragmentCuenta(){
        binding.TituloRL.text = "Cuenta"
        val fragment = FragmentCuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentL1.id, fragment, "FragmentCuenta")
        fragmentTransaction.commit()

    }
}