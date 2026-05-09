package com.example.athenea

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // Para el Login: Traemos la lista y buscamos si el usuario existe
    @GET("usuarios")
    fun getUsuarios(): Call<List<User>>

    // Para el Registro: Guardamos el nuevo usuario en la nube
    @POST("usuarios")
    fun registrarUsuario(@Body usuario: User): Call<User>

    // Para los Recursos
    @GET("recursos")
    fun getRecursos(): Call<List<Recurso>>

    @POST("recursos")
    fun addRecurso(@Body recurso: Recurso): Call<Recurso>
}