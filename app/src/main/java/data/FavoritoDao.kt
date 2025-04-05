package com.example.projandroid2.data
import androidx.room.*
import com.example.projandroid2.model.Favorito

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(favorito: Favorito)

    @Query("SELECT * FROM favoritos")
    suspend fun listar(): List<Favorito>

    @Delete
    suspend fun remover(favorito: Favorito)

    @Query("SELECT * FROM favoritos WHERE nome_oficial = :nome")
    suspend fun buscarPorNome(nome: String): Favorito?
}
