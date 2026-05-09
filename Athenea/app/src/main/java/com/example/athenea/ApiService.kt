package com.example.athenea

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("usuarios")
    fun getUsuarios(): Call<List<User>>

    @POST("usuarios")
    fun registrarUsuario(@Body usuario: User): Call<User>

    @GET("recursos")
    fun getRecursos(): Call<List<Recurso>>

    @POST("recursos")
    fun addRecurso(@Body recurso: Recurso): Call<Recurso>

    @PUT("recursos/{id}")
    fun updateRecurso(@Path("id") id: String, @Body recurso: Recurso): Call<Recurso>

    @DELETE("recursos/{id}")
    fun deleteRecurso(@Path("id") id: String): Call<Void>
}