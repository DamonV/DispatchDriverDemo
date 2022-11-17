package com.driver.reliantdispatch.data

import android.content.Context
import com.driver.reliantdispatch.App
import java.io.*
import javax.inject.Inject
import android.graphics.Bitmap
import com.driver.reliantdispatch.domain.boundaries.FilesGateway
import java.util.*
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.driver.reliantdispatch.domain.LOG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody


class FilesGatewayImpl: FilesGateway {
    @Inject
    lateinit var context: Context

    init {
        App.component.inject(this)
    }

    override suspend fun saveSignature(bitmap: Bitmap): String? = withContext(Dispatchers.IO){
        var outputStream: OutputStream? = null
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 98, bytes)
            val outputFile = File(context.getExternalFilesDir(null), "${Calendar.getInstance().timeInMillis}.jpg")
            outputStream = FileOutputStream(outputFile)
            outputStream.write(bytes.toByteArray())
            outputStream.flush()
            return@withContext outputFile.absolutePath
        } catch (e: IOException) {
            return@withContext null
        } finally {
            outputStream?.close()
        }
    }

    override fun getSignature(filename: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        options.inMutable = true
        return BitmapFactory.decodeFile(filename, options)
    }

    override fun deleteSignature(filename: String){
        val file = File(filename)
        if (file.exists()) file.delete()
    }

    override fun getFileAsString(filename: String): String? {
        FileInputStream(File(filename)).use {
            val reader = BufferedReader(InputStreamReader(it))
            val sb = StringBuilder()
            var line: String?
            var firstLine = true
            while (true) {
                line = reader.readLine()
                if (line == null) break
                if (firstLine) {
                    sb.append(line)
                    firstLine = false
                } else {
                    sb.append("\n").append(line)
                }
            }
            reader.close()
            return sb.toString()
        }
    }

    override fun getFileAsBytes(filename: String) = File(filename).readBytes()

    override fun writeBodyToDisk(body: ResponseBody, filename: String, extension: String): Uri? {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val buffer = ByteArray(4096)
            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0

            inputStream = body.byteStream()
            val outputFile = File(context.getExternalFilesDir(null), "$filename.$extension")
            outputStream = FileOutputStream(outputFile)

            while (true) {
                val read = inputStream.read(buffer)
                if (read == -1) {
                    break
                }
                outputStream.write(buffer, 0, read)
                fileSizeDownloaded += read.toLong()
                Log.d(LOG_TAG, "file download: $fileSizeDownloaded of $fileSize")
            }
            outputStream.flush()
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outputFile)

        } catch (e: IOException) {
            return null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }
}