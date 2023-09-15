package com.example.prm_projekt_2_s22599.data

import androidx.room.*
import com.example.prm_projekt_2_s22599.data.model.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM product;")
    fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM product WHERE id = :id;")
    fun getProduct(id: Long): ProductEntity

    @Query("SELECT * FROM product ORDER BY name ASC;")
    fun getAllSortedByName(): List<ProductEntity>

    @Query("SELECT * FROM product ORDER BY id ASC;")
    fun getAllSortedByTime(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProduct(newProduct: ProductEntity)

    @Update
    fun updateProduct(newProduct: ProductEntity)

    @Query("DELETE FROM product WHERE id = :id;")
    fun remove(id : Long)
}