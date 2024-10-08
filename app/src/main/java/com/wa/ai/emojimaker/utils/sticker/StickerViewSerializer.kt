package com.wa.ai.emojimaker.utils.sticker

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import org.msgpack.core.MessagePack
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker
import java.io.*
import java.security.MessageDigest

fun MessagePacker.packMatrix(matrix: Matrix) {
    val temp = FloatArray(9)
    matrix.getValues(temp)
    temp.forEach { this.packFloat(it) }
}

fun MessageUnpacker.unpackMatrix(): Matrix {
    val temp = FloatArray(9)
    (0..8).forEach { temp[it] = this.unpackFloat() }
    val matrix = Matrix()
    matrix.setValues(temp)
    return matrix
}

fun MessagePacker.packRect(rect: Rect) {
    this.packInt(rect.left)
    this.packInt(rect.top)
    this.packInt(rect.right)
    this.packInt(rect.bottom)
}

fun MessageUnpacker.unpackRect(): Rect {
    val left = this.unpackInt()
    val top = this.unpackInt()
    val right = this.unpackInt()
    val bottom = this.unpackInt()
    return Rect(left, top, right, bottom)
}

fun MessagePacker.packRectF(rectF: RectF) {
    this.packFloat(rectF.left)
    this.packFloat(rectF.top)
    this.packFloat(rectF.right)
    this.packFloat(rectF.bottom)
}

fun MessageUnpacker.unpackRectF(): RectF {
    val left = this.unpackFloat()
    val top = this.unpackFloat()
    val right = this.unpackFloat()
    val bottom = this.unpackFloat()
    return RectF(left, top, right, bottom)
}

class StickerViewSerializer {
    data class Entry(
        val bounds: Rect,
        val matrix: Matrix,
        val cropBounds: RectF,
        val flipHorizontal: Boolean,
        val flipVertical: Boolean
    )

    data class StickerMetadata(val bitmap: ByteArray, val instances: MutableList<Entry>)
    data class Board(val version: Int, val date: Long, val canvasMatrix: Matrix, val stickers: Map<String, StickerMetadata>)

    private fun serializeViewModel(viewModel: StickerViewModel): Board {
        val dedup: MutableMap<String, StickerMetadata> = mutableMapOf()

        viewModel.stickers.value!!.forEach {
            val drawableSticker = it as DrawableSticker
            val entry = Entry(
                drawableSticker.getRealBounds(),
                drawableSticker.matrix,
                drawableSticker.croppedBounds,
                drawableSticker.isFlippedHorizontally,
                drawableSticker.isFlippedVertically
            )
            dedup.getOrPut(
                it.sha256!!
            ) { StickerMetadata(drawableSticker.bitmapCache, mutableListOf()) }.instances.add(entry)
        }

        return Board(SERIAL_VERSION, System.currentTimeMillis(), viewModel.canvasMatrix.value!!.getMatrix(), dedup)
    }

    private fun deserializeViewModel(
        viewModel: StickerViewModel,
        board: Board,
        resources: Resources
    ) {
        viewModel.removeAllStickers()
        board.stickers.flatMapTo(viewModel.stickers.value!!) { (sha, metadata) ->
            metadata.instances.mapNotNull { entry ->
                BitmapFactory.decodeByteArray(metadata.bitmap, 0, metadata.bitmap.size)?.let { bitmap ->
                    val drawable = BitmapDrawable(resources, bitmap)
                    val sticker = DrawableSticker(drawable, metadata.bitmap, sha)
                    sticker.realBounds = entry.bounds
                    sticker.croppedBounds = entry.cropBounds
                    sticker.setMatrix(entry.matrix)
                    sticker.isFlippedHorizontally = entry.flipHorizontal
                    sticker.isFlippedVertically = entry.flipVertical
                    sticker
                }
            }
        }

        viewModel.canvasMatrix.value!!.setMatrix(board.canvasMatrix)
        viewModel.canvasMatrix.value!!.notifyChange()
        viewModel.updateCanvasMatrix()
    }

    fun serialize(viewModel: StickerViewModel, file: File) {
        val board = serializeViewModel(viewModel)

        // Why isn't there a maintained binary serialization library for Android? MoshiPack is
        // several Kotlin versions out of date, and Moshi is way too slow because it's JSON.
        MessagePack.newDefaultPacker(BufferedOutputStream(FileOutputStream(file)))
            .use { p ->
                p.packInt(board.version)
                p.packLong(board.date)
                p.packMatrix(board.canvasMatrix)
                p.packArrayHeader(board.stickers.size)
                board.stickers.forEach { (sha, sticker) ->
                    p.packString(sha)
                    p.packBinaryHeader(sticker.bitmap.size)
                    p.addPayload(sticker.bitmap)
                    p.packArrayHeader(sticker.instances.size)
                    sticker.instances.forEach {
                        p.packRect(it.bounds)
                        p.packMatrix(it.matrix)
                        p.packRectF(it.cropBounds)
                        p.packBoolean(it.flipHorizontal)
                        p.packBoolean(it.flipVertical)
                    }
                }
            }
    }

    fun deserialize(viewModel: StickerViewModel, inputStream: InputStream, resources: Resources) {
        val unpacked = MessagePack.newDefaultUnpacker(inputStream)
            .use { u ->
                val version = u.unpackInt()
                val date = u.unpackLong()
                val canvasMatrix = u.unpackMatrix()
                val stickerCount = u.unpackArrayHeader()
                val stickers: HashMap<String, StickerMetadata> = HashMap()
                (0 until stickerCount).forEach{
                    val sha = u.unpackString()
                    val bitmapSize = u.unpackBinaryHeader()
                    val bitmap = u.readPayload(bitmapSize)
                    val instanceCount = u.unpackArrayHeader()
                    val instances: ArrayList<Entry> = ArrayList()
                    (0 until instanceCount).mapTo(instances) {
                        val bounds = u.unpackRect()
                        val matrix = u.unpackMatrix()
                        val cropBounds = u.unpackRectF()
                        val flipHorizontal = u.unpackBoolean()
                        val flipVertical = u.unpackBoolean()
                        Entry(bounds, matrix, cropBounds, flipHorizontal, flipVertical)
                    }
                    stickers[sha] = StickerMetadata(bitmap, instances)
                }
                Board(version, date, canvasMatrix, stickers)
            }

        deserializeViewModel(viewModel, unpacked, resources)
    }

    companion object {
        const val SERIAL_VERSION = 1

        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                val bitmapDrawable = drawable
                if (bitmapDrawable.bitmap != null) {
                    return bitmapDrawable.bitmap
                }
            }
            var bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                ) // Single color bitmap will be created of 1x1 pixel
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fun sha256(bytes: ByteArray): String {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("", { str, it -> str + "%02x".format(it) })
        }
    }
}