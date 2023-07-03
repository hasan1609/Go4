package com.go4sumbergedang.go4.room

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.go4sumbergedang.go4.model.ProductEntity

interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("SELECT * FROM carts")
    suspend fun getAllCart(): List<ProductEntity>
}