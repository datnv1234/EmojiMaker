package com.wa.ai.emojimaker.data.model

import kotlin.jvm.internal.Intrinsics

/*@Metadata(
    d1 = ["\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b#\b\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u000c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\u0006\u0010\u000c\u001a\u00020\r¢\u0006\u0002\u0010\u000eJ\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\t\u0010&\u001a\u00020\u0003HÆ\u0003J\t\u0010'\u001a\u00020\u0007HÆ\u0003J\t\u0010(\u001a\u00020\u0007HÆ\u0003J\u000f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u000b0\nHÆ\u0003J\t\u0010*\u001a\u00020\rHÆ\u0003JU\u0010+\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\b\b\u0002\u0010\u000c\u001a\u00020\rHÆ\u0001J\u0013\u0010,\u001a\u00020\r2\b\u0010-\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010.\u001a\u00020\u0003HÖ\u0001J\t\u0010/\u001a\u00020\u0007HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u000c\u001a\u00020\rX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000c\u0010\u0013\"\u0004\b\u0014\u0010\u0015R \u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\u0006\u001a\u00020\u0007X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u001a\u0010\b\u001a\u00020\u0007X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001b\"\u0004\b\u001f\u0010\u001dR\u001a\u0010\u0005\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u0010\"\u0004\b!\u0010\u0012R\u001a\u0010\u0004\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u0010\"\u0004\b#\u0010\u0012¨\u00060"],
    d2 = ["Lcom/emojimaker/emojistitch/data/model/OptionsModel;", "", "id", "", "photoOptionsUnSelect", "photoOptionsSelect", "nameEvent", "", "nameOptions", "listIcon", "", "Lcom/emojimaker/emojistitch/data/model/IconModel;", "isSelectPage", "", "(IIILjava/lang/String;Ljava/lang/String;Ljava/util/List;Z)V", "getId", "()I", "setId", "(I)V", "()Z", "setSelectPage", "(Z)V", "getListIcon", "()Ljava/util/List;", "setListIcon", "(Ljava/util/List;)V", "getNameEvent", "()Ljava/lang/String;", "setNameEvent", "(Ljava/lang/String;)V", "getNameOptions", "setNameOptions", "getPhotoOptionsSelect", "setPhotoOptionsSelect", "getPhotoOptionsUnSelect", "setPhotoOptionsUnSelect", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "toString", "EmojiMaker1.0.3_04.03.2024_release"],
    k = 1,
    mv = [1, 7, 1],
    xi = 48
) *//* compiled from: OptionsModel.kt */
class OptionsModel(
    i2: Int,
    i3: Int,
    i4: Int,
    str: String,
    str2: String,
    list: List<IconModel>,
    z: Boolean
) {
    var id: Int
    var isSelectPage: Boolean
    private var listIcon: List<IconModel>
    private var nameEvent: String
    private var nameOptions: String
    var photoOptionsSelect: Int
    var photoOptionsUnSelect: Int
    operator fun component1(): Int {
        return id
    }

    operator fun component2(): Int {
        return photoOptionsUnSelect
    }

    operator fun component3(): Int {
        return photoOptionsSelect
    }

    operator fun component4(): String {
        return nameEvent
    }

    operator fun component5(): String {
        return nameOptions
    }

    operator fun component6(): List<IconModel> {
        return listIcon
    }

    operator fun component7(): Boolean {
        return isSelectPage
    }

    fun copy(
        i2: Int,
        i3: Int,
        i4: Int,
        str: String,
        str2: String,
        list: List<IconModel>,
        z: Boolean
    ): OptionsModel {
        Intrinsics.checkNotNullParameter(str, "nameEvent")
        Intrinsics.checkNotNullParameter(str2, "nameOptions")
        Intrinsics.checkNotNullParameter(list, "listIcon")
        return OptionsModel(i2, i3, i4, str, str2, list, z)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is OptionsModel) {
            return false
        }
        return id == other.id && photoOptionsUnSelect == other.photoOptionsUnSelect && photoOptionsSelect == other.photoOptionsSelect && Intrinsics.areEqual(
            nameEvent as Any, other.nameEvent as Any
        ) && Intrinsics.areEqual(
            nameOptions as Any, other.nameOptions as Any
        ) && Intrinsics.areEqual(
            listIcon as Any, other.listIcon as Any
        ) && isSelectPage == other.isSelectPage
    }

    override fun hashCode(): Int {
        val hashCode = (((((Integer.hashCode(id) * 31 + Integer.hashCode(
            photoOptionsUnSelect
        )) * 31 + Integer.hashCode(photoOptionsSelect)) * 31 + nameEvent.hashCode()) * 31 + nameOptions.hashCode()) * 31 + listIcon.hashCode()) * 31
        var z = isSelectPage
        if (z) {
            z = true
        }
        return hashCode + if (z) 1 else 0
    }

    override fun toString(): String {
        return "OptionsModel(id=$id, photoOptionsUnSelect=$photoOptionsUnSelect, photoOptionsSelect=$photoOptionsSelect, nameEvent=$nameEvent, nameOptions=$nameOptions, listIcon=$listIcon, isSelectPage=$isSelectPage)"
    }

    init {
        Intrinsics.checkNotNullParameter(str, "nameEvent")
        Intrinsics.checkNotNullParameter(str2, "nameOptions")
        Intrinsics.checkNotNullParameter(list, "listIcon")
        id = i2
        photoOptionsUnSelect = i3
        photoOptionsSelect = i4
        nameEvent = str
        nameOptions = str2
        listIcon = list
        isSelectPage = z
    }

    fun getNameEvent(): String {
        return nameEvent
    }

    fun setNameEvent(str: String) {
        Intrinsics.checkNotNullParameter(str, "<set-?>")
        nameEvent = str
    }

    fun getNameOptions(): String {
        return nameOptions
    }

    fun setNameOptions(str: String) {
        Intrinsics.checkNotNullParameter(str, "<set-?>")
        nameOptions = str
    }

    fun getListIcon(): List<IconModel> {
        return listIcon
    }

    fun setListIcon(list: List<IconModel>) {
        Intrinsics.checkNotNullParameter(list, "<set-?>")
        listIcon = list
    }

    companion object {
        fun  /* synthetic */`copy$default`(
            optionsModel: OptionsModel,
            ii2: Int,
            ii3: Int,
            ii4: Int,
            str1: String,
            str11: String,
            list1: List<IconModel>,
            z1: Boolean,
            i5: Int
        ): OptionsModel {
            var i2 = ii2
            var i3 = ii3
            var i4 = ii4
            var str = str1
            var str2 = str11
            var list = list1
            var z = z1
            if (i5 and 1 != 0) {
                i2 = optionsModel.id
            }
            if (i5 and 2 != 0) {
                i3 = optionsModel.photoOptionsUnSelect
            }
            val i6 = i3
            if (i5 and 4 != 0) {
                i4 = optionsModel.photoOptionsSelect
            }
            val i7 = i4
            if (i5 and 8 != 0) {
                str = optionsModel.nameEvent
            }
            val str3 = str
            if (i5 and 16 != 0) {
                str2 = optionsModel.nameOptions
            }
            val str4 = str2
            if (i5 and 32 != 0) {
                list = optionsModel.listIcon
            }
            val list2 = list
            if (i5 and 64 != 0) {
                z = optionsModel.isSelectPage
            }
            return optionsModel.copy(i2, i6, i7, str3, str4, list2, z)
        }
    }
}
