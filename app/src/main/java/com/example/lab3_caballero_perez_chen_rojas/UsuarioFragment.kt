package com.example.lab3_caballero_perez_chen_rojas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class UsuarioFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var profileImage: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var useremailTextView: TextView
    private lateinit var iconSpinner: Spinner
    private lateinit var logoutButton: View  // Botón de cierre de sesión
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_usuario, container, false)

        // Inicializar vistas
        profileImage = view.findViewById(R.id.profileImage)
        usernameTextView = view.findViewById(R.id.username)
        useremailTextView = view.findViewById(R.id.useremail)
        iconSpinner = view.findViewById(R.id.iconoSpinner)
        logoutButton = view.findViewById(R.id.logoutButton) // Inicializar el botón

        // Recuperar el ID del usuario desde SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("USER_ID", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "Error: No se encontró el usuario.", Toast.LENGTH_SHORT).show()
            return view
        }

        // Referencia a Firebase
        database = FirebaseDatabase.getInstance().getReference("usuarios")

        // Cargar datos del usuario
        cargarDatosUsuario()

        // Configuración del Spinner
        val iconos = listOf("Escoger icono de perfil", "Pinguino", "Rata", "Tortuga")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, iconos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        iconSpinner.adapter = adapter

        // Cambiar la imagen de perfil según la selección en el Spinner
        iconSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedIcon = iconos[position]
                when (selectedIcon) {
                    "Pinguino" -> profileImage.setImageResource(R.drawable.pinguino)
                    "Rata" -> profileImage.setImageResource(R.drawable.rata)
                    "Tortuga" -> profileImage.setImageResource(R.drawable.tortuga)
                    else -> profileImage.setImageResource(R.drawable.nulluser)
                }
                // Guardar el icono seleccionado en Firebase
                if (position != 0) {
                    guardarIcono(selectedIcon)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Configurar el botón de cerrar sesión
        logoutButton.setOnClickListener {
            cerrarSesion()
        }

        return view
    }

    private fun cargarDatosUsuario() {
        userId?.let { id ->
            database.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("nombre").getValue(String::class.java) ?: "Sin nombre"
                    val email = snapshot.child("email").getValue(String::class.java) ?: "Sin email"
                    val icono = snapshot.child("icono").getValue(String::class.java) ?: "nulluser"

                    // Actualizar vistas
                    usernameTextView.text = username
                    useremailTextView.text = email

                    // Actualizar imagen según el icono
                    when (icono) {
                        "Pinguino" -> profileImage.setImageResource(R.drawable.pinguino)
                        "Rata" -> profileImage.setImageResource(R.drawable.rata)
                        "Tortuga" -> profileImage.setImageResource(R.drawable.tortuga)
                        else -> profileImage.setImageResource(R.drawable.nulluser)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar datos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun guardarIcono(icono: String) {
        userId?.let { id ->
            // Actualizar el icono en la base de datos de Firebase
            database.child(id).child("icono").setValue(icono)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Icono actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar el icono", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun cerrarSesion() {
        // Borrar el USER_ID de SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("USER_ID")
        editor.apply()

        // Redirigir a la actividad de inicio de sesión
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Opcional, para cerrar la actividad actual
    }
}
