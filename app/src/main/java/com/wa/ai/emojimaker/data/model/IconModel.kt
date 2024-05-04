package com.wa.ai.emojimaker.data.model

import kotlin.jvm.internal.Intrinsics

/*@Metadata(
    d1 = ["\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001b\b\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0003¢\u0006\u0002\u0010\nJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J;\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u001e\u001a\u00020\u00072\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010 \u001a\u00020\u0003HÖ\u0001J\t\u0010!\u001a\u00020\u0005HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u000cR\u001a\u0010\t\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000c\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0006\u001a\u00020\u0007X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\b\u001a\u00020\u0007X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\u0010\"\u0004\b\u0013\u0010\u0012R\u001a\u0010\u0004\u001a\u00020\u0005X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017¨\u0006\""],
    d2 = ["Lcom/emojimaker/emojistitch/data/model/IconModel;", "", "id", "", "path", "", "isSelect", "", "isShowAds", "idEmoji", "(ILjava/lang/String;ZZI)V", "getId", "()I", "getIdEmoji", "setIdEmoji", "(I)V", "()Z", "setSelect", "(Z)V", "setShowAds", "getPath", "()Ljava/lang/String;", "setPath", "(Ljava/lang/String;)V", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "EmojiMaker1.0.3_04.03.2024_release"],
    k = 1,
    mv = [1, 7, 1],
    xi = 48*/
//) /* compiled from: IconModel.kt */
class IconModel(i2: Int, str: String, z: Boolean, z2: Boolean, i3: Int) {
    val id: Int
    var idEmoji: Int
    var isSelect: Boolean
    var isShowAds: Boolean
    private var path: String
    operator fun component1(): Int {
        return id
    }

    operator fun component2(): String {
        return path
    }

    operator fun component3(): Boolean {
        return isSelect
    }

    operator fun component4(): Boolean {
        return isShowAds
    }

    operator fun component5(): Int {
        return idEmoji
    }

    fun copy(i2: Int, str: String, z: Boolean, z2: Boolean, i3: Int): IconModel {
        Intrinsics.checkNotNullParameter(str, "path")
        return IconModel(i2, str, z, z2, i3)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is IconModel) {
            return false
        }
        return id == other.id && Intrinsics.areEqual(
            path as Any,
            other.path as Any
        ) && isSelect == other.isSelect && isShowAds == other.isShowAds && idEmoji == other.idEmoji
    }

    override fun hashCode(): Int {
        val hashCode = (Integer.hashCode(id) * 31 + path.hashCode()) * 31
        var z = isSelect
        var z2 = true
        if (z) {
            z = true
        }
        val i2 = (hashCode + if (z) 1 else 0) * 31
        val z3 = isShowAds
        if (!z3) {
            z2 = z3
        }
        return (i2 + if (z2) 1 else 0) * 31 + Integer.hashCode(idEmoji)
    }

    override fun toString(): String {
        return "IconModel(id=$id, path=$path, isSelect=$isSelect, isShowAds=$isShowAds, idEmoji=$idEmoji)"
    }

    init {
        Intrinsics.checkNotNullParameter(str, "path")
        id = i2
        path = str
        isSelect = z
        isShowAds = z2
        idEmoji = i3
    }

    fun getPath(): String {
        return path
    }

    fun setPath(str: String) {
        Intrinsics.checkNotNullParameter(str, "<set-?>")
        path = str
    }

    companion object {
        fun  /* synthetic */`copy$default`(
            iconModel: IconModel,
            ii2: Int,
            str1: String,
            zz: Boolean,
            z22: Boolean,
            ii3: Int,
            i4: Int,
            obj: Any?
        ): IconModel {
            var i2 = ii2
            var str = str1
            var z = zz
            var z2 = z22
            var i3 = ii3
            if (i4 and 1 != 0) {
                i2 = iconModel.id
            }
            if (i4 and 2 != 0) {
                str = iconModel.path
            }
            val str2 = str
            if (i4 and 4 != 0) {
                z = iconModel.isSelect
            }
            val z3 = z
            if (i4 and 8 != 0) {
                z2 = iconModel.isShowAds
            }
            val z4 = z2
            if (i4 and 16 != 0) {
                i3 = iconModel.idEmoji
            }
            return iconModel.copy(i2, str2, z3, z4, i3)
        }
    }
}
