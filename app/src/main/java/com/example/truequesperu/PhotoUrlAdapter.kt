package com.example.truequesperu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.truequesperu.databinding.ItemFotoUrlBinding

class PhotoUrlAdapter(private var lista: List<String>) : RecyclerView.Adapter<PhotoUrlAdapter.VH>() {

    inner class VH(val binding: ItemFotoUrlBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemFotoUrlBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val url = lista[position]
        // Usa Glide para cargar la url
        Glide.with(holder.binding.root)
            .load(url)
            .placeholder(R.drawable.ic_image_placeholder) // agrega drawable
            .into(holder.binding.imgFotoUrl)
        // si quieres click -> abrir en full screen, puedes agregar aquí
    }

    override fun getItemCount(): Int = lista.size

    fun updateList(nueva: List<String>) {
        lista = nueva
        notifyDataSetChanged()
    }
}