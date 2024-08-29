package com.wa.ai.emojimaker.data.repo.collection

import com.wa.ai.emojimaker.data.model.CollectionUI
import com.wa.ai.emojimaker.database.dao.CollectionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CollectionRepoImpl @Inject constructor(
    private val collectionDao: CollectionDao,
) : CollectionRepo {
    override suspend fun getAllCollection(): Flow<List<CollectionUI>> {
        return flow {
            emit(collectionDao.getAllCollection() ?: mutableListOf())
        }
    }

    override suspend fun insertCollection(collectionUI: CollectionUI): Flow<Long> {
        return flow {
            emit( collectionDao.insertCollection(collectionUI) )
        }
    }
}