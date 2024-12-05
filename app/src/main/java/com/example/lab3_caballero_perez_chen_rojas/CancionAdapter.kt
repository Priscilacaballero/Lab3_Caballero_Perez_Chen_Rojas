package com.example.lab3_caballero_perez_chen_rojas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3_caballero_perez_chen_rojas.R

class CancionAdapter(
    private val canciones: List<Cancion>,
    private val context: Context // Pasar el contexto al adaptador para usar SharedPreferences
) : RecyclerView.Adapter<CancionAdapter.CancionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cancion, parent, false)
        return CancionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CancionViewHolder, position: Int) {
        val cancion = canciones[position]

        // Asignar el nombre de la canción y el artista
        holder.nombreCancion.text = cancion.nombre
        holder.artistaCancion.text = cancion.artista
        holder.generoDificultadCancion.text = "${cancion.genero} | ${cancion.dificultad}"

        // Asignar la imagen según el ID de la canción
        val imagenId = when (cancion.id) {
            1 -> R.drawable.adele
            2 -> R.drawable.queen
            3 -> R.drawable.ed
            4 -> R.drawable.john
            5 -> R.drawable.weeknd
            6 -> R.drawable.fonsi
            7 -> R.drawable.mariah
            else -> R.drawable.nulluser // Imagen por defecto en caso de que no haya ID válido
        }
        holder.imageCancion.setImageResource(imagenId)

        // Configurar el listener de clic
        holder.itemView.setOnClickListener {
            // Guardar el id de la canción seleccionada en SharedPreferences
            guardarIdCancionEnSharedPreferences(cancion.id)

            // Mostrar un Toast con el ID de la canción
            Toast.makeText(context, "ID de la canción seleccionada: ${cancion.id}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return canciones.size
    }

    // Método para guardar el ID de la canción seleccionada en SharedPreferences
    private fun guardarIdCancionEnSharedPreferences(cancionId: Int) {
        val sharedPreferences = context.getSharedPreferences("MusicaPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("CANCION_ID", cancionId) // Guardar el ID de la canción
        editor.apply()
    }

    // ViewHolder que representa cada item en el RecyclerView
    inner class CancionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCancion: ImageView = itemView.findViewById(R.id.imageCancion)
        val nombreCancion: TextView = itemView.findViewById(R.id.nombreCancion)
        val artistaCancion: TextView = itemView.findViewById(R.id.artistaCancion)
        val generoDificultadCancion: TextView = itemView.findViewById(R.id.generoDificultadCancion)
    }
}
