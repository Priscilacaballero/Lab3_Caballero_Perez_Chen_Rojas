package com.example.lab3_caballero_perez_chen_rojas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configura el diseño XML como la vista de esta actividad
        setContentView(R.layout.activity_registro)
        database = FirebaseDatabase.getInstance()
        // Asocia los componentes del diseño con sus respectivos IDs
        val editTextNombre: EditText = findViewById(R.id.editTextNombre)
        val editTextApellido: EditText = findViewById(R.id.editTextApellido)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        val button2: Button = findViewById(R.id.button2)

        button2.setOnClickListener {
            val intent = Intent(this, IniciarActivity::class.java)
            startActivity(intent)
        }
        buttonRegister.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val apellido = editTextApellido.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val icono = "Ninguna" // Valor predeterminado o seleccionable

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Crear un objeto Usuario
                val usuario = Usuario(nombre, apellido, email, password, icono)

                // Obtener la referencia a la base de datos
                val usuariosRef = database.getReference("usuarios")

                // Guardar el usuario bajo el nodo 'usuarios'
                val userId = usuariosRef.push().key // Genera un ID único para el usuario
                if (userId != null) {
                    usuariosRef.child(userId).setValue(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                            // Redirigir al usuario a la pantalla de inicio
                            val intent = Intent(this, IniciarActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al registrar el usuario. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

    }
}
