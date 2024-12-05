package com.example.lab3_caballero_perez_chen_rojas

data class Cancion(
    val id: Int = 0, // El ID de la canción, se obtiene desde Firebase
    val nombre: String = "",
    val artista: String = "",
    val genero: String = "",
    val dificultad: String = ""
)

