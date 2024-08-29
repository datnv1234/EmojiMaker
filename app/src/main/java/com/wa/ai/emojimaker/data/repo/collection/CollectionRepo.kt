package com.wa.ai.emojimaker.data.repo.collection

import com.wa.ai.emojimaker.data.model.CollectionUI
import kotlinx.coroutines.flow.Flow

interface CollectionRepo {
    suspend fun getAllCollection(): Flow<List<CollectionUI>>
    suspend fun insertCollection(collectionUI: CollectionUI): Flow<Long>
}