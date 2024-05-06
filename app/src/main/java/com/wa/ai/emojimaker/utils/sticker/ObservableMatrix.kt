package com.wa.ai.emojimaker.utils.sticker

import android.graphics.Matrix
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.wa.ai.emojimaker.BR

class ObservableMatrix : BaseObservable() {
    private val _matrix = Matrix()

    @Bindable
    fun getMatrix(): Matrix {
        return _matrix
    }

    fun setMatrix(matrix: Matrix) {
        _matrix.set(matrix)
        notifyPropertyChanged(1)
    }

    fun setValues(floats: FloatArray) {
        _matrix.setValues(floats)
        notifyPropertyChanged(1)
    }

    fun invert(a: Matrix) {
        _matrix.invert(a)
    }
}