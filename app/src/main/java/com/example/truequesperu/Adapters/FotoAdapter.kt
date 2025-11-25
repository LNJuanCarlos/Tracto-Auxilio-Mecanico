package com.example.truequesperu.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.truequesperu.databinding.ItemFotoBinding

class FotoAdapter(
    private val fotos: MutableList<Uri>,
    private val onEliminarClick: (Uri) -> Unit
) : RecyclerView.Adapter<FotoAdapter.FotoViewHolder>() {

    inner class FotoViewHolder(val binding: ItemFotoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoViewHolder {
        val binding = ItemFotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FotoViewHolder, position: Int) {
        val uri = fotos[position]

        holder.binding.imgFoto.setImageURI(uri)

        holder.binding.btnEliminar.setOnClickListener {
            onEliminarClick(uri)
        }
    }

    override fun getItemCount(): Int = fotos.size

    fun agregarFoto(uri: Uri) {
        fotos.add(uri)
        notifyItemInserted(fotos.size - 1)
    }

    fun eliminarFoto(uri: Uri) {
        val index = fotos.indexOf(uri)
        if (index != -1) {
            fotos.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun obtenerLista(): List<Uri> = fotos
}