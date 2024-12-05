package com.example.lab3_caballero_perez_chen_rojas

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class ReproductorFragment : Fragment() {

    private lateinit var videoView: VideoView
    private lateinit var textViewSongName: TextView
    private lateinit var textViewArtist: TextView
    private lateinit var textViewDifficulty: TextView
    private lateinit var textViewGenre: TextView
    private lateinit var textViewLetra: TextView

    private lateinit var database: DatabaseReference
    private var cancionId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_reproductor, container, false)

        // Inicializar los elementos de la UI
        videoView = view.findViewById(R.id.videoView)
        textViewSongName = view.findViewById(R.id.textViewSongName)
        textViewArtist = view.findViewById(R.id.textViewArtist)
        textViewDifficulty = view.findViewById(R.id.textViewDifficulty)
        textViewGenre = view.findViewById(R.id.textViewGenre)
        textViewLetra = view.findViewById(R.id.letra)  // Inicializar el TextView de la letra

        // Obtener el ID de la canción desde SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MusicaPrefs", Context.MODE_PRIVATE)
        cancionId = sharedPreferences.getInt("CANCION_ID", 0)

        // Conectar a Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("musica")

        // Cargar la canción desde Firebase
        cargarCancionDesdeFirebase()

        return view
    }

    private fun cargarCancionDesdeFirebase() {
        // Usar cancionId para buscar la canción específica en Firebase
        database.child(cancionId.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Obtener los datos de la canción
                    val cancion = snapshot.getValue(Cancion::class.java)

                    if (cancion != null) {
                        // Mostrar la información de la canción en los TextView
                        textViewSongName.text = cancion.nombre
                        textViewArtist.text = cancion.artista
                        textViewDifficulty.text = "Dificultad: ${cancion.dificultad}"
                        textViewGenre.text = "Género: ${cancion.genero}"

                        val letra = when (cancionId) {
                            3 -> getString(R.string.lyrics_3)
                            else -> ""  // Si no se encuentra el id, no se muestra nada
                        }
                        textViewLetra.text = letra
                        // Reproducir el video correspondiente
                        val videoUri = when (cancionId) {
                            1 -> R.raw.adele
                            2 -> R.raw.queen
                            3 -> R.raw.ed
                            4 -> R.raw.john
                            5 -> R.raw.weeknd
                            6 -> R.raw.fonsi
                            7 -> R.raw.mariah
                            else -> R.raw.adele // Video predeterminado
                        }

                        val uri = android.net.Uri.parse("android.resource://${requireContext().packageName}/$videoUri")
                        videoView.setVideoURI(uri)

                        // Configurar el MediaController para controlar el VideoView
                        val mediaController = android.widget.MediaController(requireContext())
                        mediaController.setAnchorView(videoView)
                        videoView.setMediaController(mediaController)
                        videoView.requestFocus()
                        videoView.start()
                    }
                } else {
                    Toast.makeText(context, "Canción no encontrada", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al cargar los datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
