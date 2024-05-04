package com.wa.ai.emojimaker.data.model

import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.jvm.internal.Intrinsics

/*@Metadata(
    d1 = ["\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\b\b\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\u0006¢\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0016\u001a\u00020\bHÆ\u0003J\t\u0010\u0017\u001a\u00020\u0006HÆ\u0003J;\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u0006HÆ\u0001J\u0013\u0010\u0019\u001a\u00020\b2\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001b\u001a\u00020\u0006HÖ\u0001J\t\u0010\u001c\u001a\u00020\u0003HÖ\u0001R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u000cR\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\t\u001a\u00020\u00068\u0006X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000cR\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010¨\u0006\u001d"],
    d2 = ["Lcom/emojimaker/emojistitch/data/model/EmojiModel;", "", "path", "", "folderName", "position", "", "iShowAds", "", "id", "(Ljava/lang/String;Ljava/lang/String;IZI)V", "getFolderName", "()Ljava/lang/String;", "getIShowAds", "()Z", "getId", "()I", "getPath", "getPosition", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "EmojiMaker1.0.3_04.03.2024_release"],
    k = 1,
    mv = [1, 7, 1],
    xi = 48
) */ /* compiled from: EmojiModel.kt */
class EmojiModel(str: String, str2: String, i2: Int, z: Boolean, i3: Int) {
    val folderName: String
    val iShowAds: Boolean
    val id: Int
    val path: String
    val position: Int
    operator fun component1(): String {
        return path
    }

    operator fun component2(): String {
        return folderName
    }

    operator fun component3(): Int {
        return position
    }

    operator fun component4(): Boolean {
        return iShowAds
    }

    operator fun component5(): Int {
        return id
    }

    fun copy(str: String, str2: String, i2: Int, z: Boolean, i3: Int): EmojiModel {
        Intrinsics.checkNotNullParameter(str, "path")
        Intrinsics.checkNotNullParameter(str2, "folderName")
        return EmojiModel(str, str2, i2, z, i3)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is EmojiModel) {
            return false
        }
        return Intrinsics.areEqual(path as Any, other.path as Any) && Intrinsics.areEqual(
            folderName as Any, other.folderName as Any
        ) && position == other.position && iShowAds == other.iShowAds && id == other.id
    }

    override fun hashCode(): Int {
        val hashCode = ((path.hashCode() * 31 + folderName.hashCode()) * 31 + Integer.hashCode(
            position
        )) * 31
        var z = iShowAds
        if (z) {
            z = true
        }
        return (hashCode + if (z) 1 else 0) * 31 + Integer.hashCode(id)
    }

    override fun toString(): String {
        return "EmojiModel(path=$path, folderName=$folderName, position=$position, iShowAds=$iShowAds, id=$id)"
    }

    init {
        Intrinsics.checkNotNullParameter(str, "path")
        Intrinsics.checkNotNullParameter(str2, "folderName")
        path = str
        folderName = str2
        position = i2
        iShowAds = z
        id = i3
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    /* synthetic */ constructor(
        str: String,
        str2: String,
        i2: Int,
        z: Boolean,
        i3: Int,
        i4: Int,
        defaultConstructorMarker: DefaultConstructorMarker?
    ) : this(str, str2, i2, if (i4 and 8 != 0) true else z, if (i4 and 16 != 0) 0 else i3)

    companion object {
        fun  /* synthetic */`copy$default`(
            emojiModel: EmojiModel,
            str: String,
            str2: String,
            i2: Int,
            z: Boolean,
            i3: Int,
            i4: Int,
            obj: Any?
        ): EmojiModel {
            var str = str
            var str2 = str2
            var i2 = i2
            var z = z
            var i3 = i3
            if (i4 and 1 != 0) {
                str = emojiModel.path
            }
            if (i4 and 2 != 0) {
                str2 = emojiModel.folderName
            }
            val str3 = str2
            if (i4 and 4 != 0) {
                i2 = emojiModel.position
            }
            val i5 = i2
            if (i4 and 8 != 0) {
                z = emojiModel.iShowAds
            }
            val z2 = z
            if (i4 and 16 != 0) {
                i3 = emojiModel.id
            }
            return emojiModel.copy(str, str3, i5, z2, i3)
        }
    }
}
