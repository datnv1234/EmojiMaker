package com.wa.ai.emojimaker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wa.ai.emojimaker.data.model.CollectionUI

@Dao
interface CollectionDao {

	@Query("SELECT * FROM CollectionUI ORDER BY id ASC")
	fun getAllCollection(): List<CollectionUI>?


	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertCollection(collectionUI: CollectionUI): Long
}