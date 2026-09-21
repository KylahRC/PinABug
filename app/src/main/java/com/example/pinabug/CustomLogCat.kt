//region Packages
package com.example.pinabug
//endregion

//region Imports
import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
//endregion

//region AppLogs Object
object AppLogs
{

    //region Fields
    private var logFile: File? = null
    //endregion

    //region Setup Logging
    fun init(context: Context)
    {
        try
        {
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())

            // Use external app-specific storage
            val externalDir = context.getExternalFilesDir("logs")
            if (externalDir != null && !externalDir.exists())
            {
                externalDir.mkdirs()
            }

            logFile = File(externalDir, "debug_log_$timestamp.txt")
            logFile?.writeText("=== App Run Started at $timestamp ===\n")
        }
        catch (e: Exception)
        {
            Log.e("AppLogs", "Failed to init log file: ${e.message}", e)
        }
    }
    //endregion

    //region Do the Logging
    fun log(tag: String, message: String)
    {
        try
        {
            val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val entry = "$timestamp [$tag] $message\n"
            logFile?.appendText(entry)
        }
        catch (e: Exception)
        {
            Log.e("AppLogs", "Failed to write log: ${e.message}", e)
        }
    }
    //endregion
}
//endregion
