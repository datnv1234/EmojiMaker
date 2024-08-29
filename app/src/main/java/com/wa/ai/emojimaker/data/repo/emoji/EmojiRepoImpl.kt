package com.wa.ai.emojimaker.data.repo.emoji

import android.app.Activity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wa.ai.emojimaker.data.entities.EmojiEntities
import com.wa.ai.emojimaker.data.entities.toEmojiUI
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.data.model.EmojiUI
import com.wa.ai.emojimaker.functions.RequestNetwork
import com.wa.ai.emojimaker.functions.RequestNetworkController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class EmojiRepoImpl @Inject constructor() : EmojiRepo {

    override suspend fun getAllEmoji(activity: Activity): Flow<List<EmojiUI>> {
        return flow {
            kotlin.runCatching {
                emit(getListEmoji(activity))
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private suspend fun getListEmoji(activity: Activity): List<EmojiUI> {
        return kotlin.runCatching {
            val cachedResponse = SharedPreferenceHelper.getString("supportedEmojisList")
            if (!cachedResponse.isNullOrEmpty()) {
                val emojiEntitiesList: ArrayList<EmojiEntities> = Gson().fromJson(
                    cachedResponse,
                    object : TypeToken<ArrayList<EmojiEntities>>() {}.type
                )
                return emojiEntitiesList.map { it.toEmojiUI() }
            }

            suspendCancellableCoroutine { continuation ->
                val requestSupportedEmojis = RequestNetwork(activity)
                val requestSupportedEmojisListener = object : RequestNetwork.RequestListener {
                    override fun onResponse(
                        tag: String?,
                        response: String?,
                        responseHeaders: HashMap<String?, Any?>?,
                    ) {
                        kotlin.runCatching {
                            SharedPreferenceHelper.storeString("supportedEmojisList", response)
                            val emojiEntitiesList: ArrayList<EmojiEntities> = Gson().fromJson(
                                response,
                                object : TypeToken<ArrayList<EmojiEntities>>() {}.type
                            )
                            continuation.resume(emojiEntitiesList.map { it.toEmojiUI() })
                        }.onFailure {
                            it.printStackTrace()
                            continuation.resumeWithException(it)
                        }
                    }

                    override fun onErrorResponse(tag: String?, message: String?) {
                         kotlin.runCatching {
                             continuation.resumeWithException(Exception(message))
                         }.onFailure {
                             it.printStackTrace()
                         }
                    }
                }

                requestSupportedEmojis.startRequestNetwork(
                    RequestNetworkController.GET,
                    "https://ilyassesalama.github.io/EmojiMixer/emojis/supported_emojis.json",
                    "",
                    requestSupportedEmojisListener
                )
            }
        }.getOrElse {
            it.printStackTrace()
            emptyList()
        }
    }
}

