package com.wa.ai.emojimaker.evenbus

import com.wa.ai.emojimaker.data.model.PackageModel
import java.io.File

data class CreatePackageEvent(val mPackage: PackageModel, val file: File)