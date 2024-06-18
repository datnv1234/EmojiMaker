package com.wa.ai.emojimaker.utils.extention

fun String.removeFirstLastChar(): String =  this.substring(1, this.length - 1)
fun String.getLastBitFromUrl(): String = replaceFirst(".*/([^/?]+).*".toRegex(), "$1")