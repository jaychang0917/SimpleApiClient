package com.jaychang.sac.util

import android.content.Context
import android.webkit.MimeTypeMap
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.StringWriter



internal object Utils {

  fun toText(context: Context, file: Int): String {
    val inputStream = context.resources.openRawResource(file)
    val writer = StringWriter()
    val buffer = CharArray(1024)
    inputStream.use { input ->
      val reader = BufferedReader(InputStreamReader(input, "UTF-8"))
      var line: Int = -1
      while ({ line = reader.read(buffer); line }() != -1) {
        writer.write(buffer, 0, line)
      }
    }
    return writer.toString()
  }

  fun getMimeType(file: File): String {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
    if (extension != null) {
      type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    println("getMimeType: $type")
    return type ?: "*/*"
  }
}