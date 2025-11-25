package com.example.truequesperu.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.truequesperu.Models.Alert
import com.example.truequesperu.R

import java.text.SimpleDateFormat
import java.util.*

class AlertAdapter(
    private var lista: List<Alert>,
    private val listener: (Alert) -> Unit
) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitulo: TextView = itemView.findViewById(R.id.txtTitulo)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtTipo: TextView = itemView.findViewById(R.id.txtTipo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alerta, parent, false)
        return AlertViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alerta = lista[position]

        holder.txtTitulo.text = alerta.descripcion
        holder.txtFecha.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(alerta.timestamp))

        holder.txtEstado.text = alerta.status
        holder.txtTipo.text = alerta.tipo

        val color = when (alerta.status) {
            "Pendiente" -> Color.parseColor("#FFA726")
            "En camino" -> Color.parseColor("#29B6F6")
            "En atención" -> Color.parseColor("#FB8C00")
            "Finalizado" -> Color.parseColor("#66BB6A")
            else -> Color.GRAY
        }

        holder.txtEstado.setTextColor(color)

        holder.itemView.setOnClickListener { listener(alerta) }
    }

    // metodo clave
    fun actualizarLista(nuevaLista: List<Alert>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}