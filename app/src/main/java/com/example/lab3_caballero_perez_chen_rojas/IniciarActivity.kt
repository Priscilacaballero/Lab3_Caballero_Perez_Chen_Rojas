package com.example.lab3_caballero_perez_chen_rojas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.example.lab3_caballero_perez_chen_rojas.databinding.ActivityIniciarBinding
import com.google.firebase.database.*

class IniciarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIniciarBinding
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout utilizando ViewBinding
        binding = ActivityIniciarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Referencia a la base de datos de Firebase
        database = FirebaseDatabase.getInstance().getReference("usuarios")

        // Acción al hacer clic en el botón "Iniciar sesión"
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                verificarCredenciales(email, password)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción al hacer clic en el botón "Iniciar con Google"
        binding.buttonGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }

        // Acción al hacer clic en el enlace "¿No tienes cuenta? Regístrate"
        binding.textViewRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                handleSignInResult(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error en inicio de sesión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount) {
        val email = account.email
        val idToken = account.idToken
        Toast.makeText(this, "Inicio de sesión exitoso: $email", Toast.LENGTH_SHORT).show()

        // Redirigir a la siguiente actividad
        val intent = Intent(this, AdentroActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun verificarCredenciales(email: String, password: String) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var encontrado = false
                var userId: String? = null

                for (usuarioSnapshot in snapshot.children) {
                    val dbEmail = usuarioSnapshot.child("email").getValue(String::class.java)
                    val dbPassword = usuarioSnapshot.child("password").getValue(String::class.java)

                    if (dbEmail == email && dbPassword == password) {
                        encontrado = true
                        userId = usuarioSnapshot.key // Obtén el ID único del usuario
                        break
                    }
                }

                if (encontrado) {
                    guardarIdUsuarioEnSharedPreferences(userId) // Guardar el ID en SharedPreferences
                    Toast.makeText(this@IniciarActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                    // Redirigir a la siguiente actividad
                    val intent = Intent(this@IniciarActivity, AdentroActivity::class.java)
                    startActivity(intent)
                    finish() // Finaliza la actividad actual
                } else {
                    Toast.makeText(this@IniciarActivity, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@IniciarActivity, "Error al verificar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarIdUsuarioEnSharedPreferences(userId: String?) {
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_ID", userId) // Guardar el ID del usuario
        editor.apply()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}