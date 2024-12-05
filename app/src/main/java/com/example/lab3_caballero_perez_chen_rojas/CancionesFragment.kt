package com.example.lab3_caballero_perez_chen_rojas

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CancionesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CancionAdapter
    private lateinit var database: DatabaseReference
    private val cancionesList = mutableListOf<Cancion>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_canciones, container, false)

        // Configuración del RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewCanciones)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CancionAdapter(cancionesList, requireContext()) // Pasar el contexto
        recyclerView.adapter = adapter

        // Referencia a la base de datos de Firebase
        database = FirebaseDatabase.getInstance().getReference("musica")

        // Cargar canciones de Firebase
        cargarCanciones()

        return view
    }

    private fun cargarCanciones() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cancionesList.clear() // Limpiar la lista antes de agregar nuevos datos

                for (cancionSnapshot in snapshot.children) {
                    val cancion = cancionSnapshot.getValue(Cancion::class.java)
                    if (cancion != null) {
                        cancionesList.add(cancion)
                    }
                }

                adapter.notifyDataSetChanged() // Notificar al adapter para que actualice la lista
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar canciones: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Obtener el ID de la canción desde SharedPreferences
    private fun obtenerIdCancionDesdeSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("MusicaPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("CANCION_ID", -1) // Retorna -1 si no se ha guardado el ID
    }
}
