package com.wa.ai.emojimaker.utils

import timber.log.Timber

class MyDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "(%s:%s)",
            element.fileName,
            element.lineNumber
        )
    }

}