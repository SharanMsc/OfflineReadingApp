package com.dreamworks.offlinereading.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dreamworks.offlinereading.databinding.ActivityDownloadBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadScreen : AppCompatActivity() {
    private lateinit var binding: ActivityDownloadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.downloadButton.setOnClickListener {
            val url = binding.urlEditText.text.toString()
//            downloadContent(url)

            // Handle the loaded thumbnail bitmap

//            downloadAndSaveWebPage(url)
            binding.statusTextView.text = ""
        }
//        getFiles()

    }

    private fun getFiles(){
        val downloadsDirectory: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (downloadsDirectory != null) {
            val files: Array<File>? = downloadsDirectory.listFiles()
            if (files != null) {

                for (file in files) {
                    // Do something with each file (e.g., display file name)

                    Log.d("http:e", file.name)
                }
            }
        }
    }






    @SuppressLint("SetTextI18n")
    private fun downloadAndSaveWebPage(url: String) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val htmlContent = downloadWebPage(url)
                saveWebPage(htmlContent)
                withContext(Dispatchers.Main) {
                    Log.d("MainActivity", "Web page saved successfully")
                    binding.statusTextView.text = "Download Complete"
                    // Show a toast or update UI to indicate successful saving
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.statusTextView.text = "Download Failed: ${e.message}"
                    Log.e("MainActivity", "Error saving web page: ${e.message}")
                    // Show a toast or update UI to indicate error
                }
            }
        }
    }

    private suspend fun downloadWebPage(url: String): String {
        return try {
            val urlConnection = withContext(Dispatchers.IO) {
                URL(url).openConnection()
            }
            val inputStream = withContext(Dispatchers.IO) {
                urlConnection.getInputStream()
            }
            val buffer = StringBuffer()
            val bufferedReader = inputStream.bufferedReader()
            var inputLine: String?
            while (withContext(Dispatchers.IO) {
                    bufferedReader.readLine()
                }.also { inputLine = it } != null) {
                buffer.append(inputLine)
            }
            withContext(Dispatchers.IO) {
                inputStream.close()
            }
            withContext(Dispatchers.IO) {
                bufferedReader.close()
            }
            buffer.toString()
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun saveWebPage(htmlContent: String) {
        try {
            val fileName = "saved_web_page.html"
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+"/html")
            val file = File(storageDir, fileName)
            val fileOutputStream =
                withContext(Dispatchers.IO) {
                    FileOutputStream(file)
                }


            withContext(Dispatchers.IO) {
                fileOutputStream.write(htmlContent.toByteArray())
            }
            withContext(Dispatchers.IO) {
                fileOutputStream.close()
            }
        } catch (e: Exception) {
            throw e
        }
    }




    private fun downloadContent(url: String) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val result = downloadFile(url)
                withContext(Dispatchers.Main) {
                    if (result) {

                        binding.statusTextView.text = "Download Complete"
                    } else {
                        binding.statusTextView.text = "Download Failed"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.statusTextView.text = "Download Failed"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun downloadFile(url: String): Boolean {
        return try {
            val urlConnection = withContext(Dispatchers.IO) {
                URL(url).openConnection()
            }
            val inputStream = withContext(Dispatchers.IO) {
                urlConnection.getInputStream()
            }

            val totalSize = urlConnection.contentLength
            var downloadedSize = 0
            var totalBytesRead = 0
            val buffer = ByteArray(1024)
            var bytesRead: Int

            val fileName = "downloaded_file"+""+System.currentTimeMillis()+"."+getFileExtension(url)
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+"/"+getFileExtension(url))
            val file = File(storageDir, fileName)
            val outputStream = withContext(Dispatchers.IO) {
                FileOutputStream(file)
            }

            while (withContext(Dispatchers.IO) {
                    inputStream.read(buffer)
                }.also { bytesRead = it } != -1) {
                totalBytesRead += bytesRead
                downloadedSize += bytesRead
                val progress = (totalBytesRead * 100 / totalSize)
                withContext(Dispatchers.Main) {
                    Log.e("http:e","Downloading... $progress%")
                    binding.progressBar.progress = progress
                    binding.statusTextView.text = "Downloading... $progress%"
                }
                withContext(Dispatchers.IO) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }

            withContext(Dispatchers.IO) {
                inputStream.close()
            }
            withContext(Dispatchers.IO) {
                outputStream.close()
            }
            true
        } catch (e: Exception) {
            Log.e("http:e",e.message.toString())
            false
        }
    }
    private fun getFileExtension(url: String): String {
        val lastIndexOfDot = url.lastIndexOf('.')
        return if (lastIndexOfDot != -1) {
            url.substring(lastIndexOfDot + 1)
        } else {
            "jpg"
        }
    }







}

