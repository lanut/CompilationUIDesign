package ui.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

suspend fun readFileInBackground(filePath: String): String = withContext(Dispatchers.IO) {
    if (filePath.isEmpty()) return@withContext ""
    return@withContext try {
        val file = File(filePath)
        file.readText()
    } catch (e: IOException) {
        ""
    }
}