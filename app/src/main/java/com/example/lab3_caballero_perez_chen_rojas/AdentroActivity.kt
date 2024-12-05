package com.example.lab3_caballero_perez_chen_rojas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdentroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adentro) // Vincula el diseño XML con esta actividad
        // Carga el fragmento inicial
        loadFragment(CancionesFragment())

        // Configura el BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val fragment: Fragment = when (menuItem.itemId) {
                R.id.menu_canciones -> CancionesFragment()
                R.id.menu_reproductor -> ReproductorFragment()
                R.id.menu_usuario -> UsuarioFragment()
                R.id.menu_favoritas -> FavoritasFragment()
                else -> CancionesFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    // Método para cargar fragmentos en el contenedor
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
