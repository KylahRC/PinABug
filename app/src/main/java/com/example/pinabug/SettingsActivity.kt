//https://github.com/osmdroid/osmdroid/wiki
//https://codesignal.com/learn/courses/handling-json-files-with-kotlin-2/lessons/introduction-to-json-handling-with-kotlin
//https://codesignal.com/learn/courses/handling-json-files-with-kotlin-2/lessons/introduction-to-json-handling-in-kotlin-1
//https://developer.android.com/develop/ui/views/components/menus?hl=en
//https://developer.android.com/develop/ui/views/theming/themes

//region Packages
package com.example.pinabug
//endregion

//region Imports
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

//endregion

//region Settings Activity
class SettingsActivity : AppCompatActivity()
{
    private lateinit var themeSwitch: Switch
    private lateinit var clearPostsBtn: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        try
        {
            setContentView(R.layout.activity_settings)
            themeSwitch = findViewById(R.id.themeSwitch)
            clearPostsBtn = findViewById(R.id.clearPostsBtn)

            //region Theme Toggle
            val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val isBrownTheme = prefs.getBoolean("BrownTheme", false)
            themeSwitch.isChecked = isBrownTheme

            themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                try
                {
                    val editor = prefs.edit()
                    editor.putBoolean("BrownTheme", isChecked)
                    editor.apply()

                    AppLogs.log("SettingsActivity", "Theme toggled: Brown=$isChecked")
                    Log.d("SettingsActivity", "Theme toggled: Brown=$isChecked")

                    // Restart app to apply theme
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                catch (e: Exception)
                {
                    AppLogs.log("SettingsActivity", "Error toggling theme: ${e.message}")
                    Log.e("SettingsActivity", "Error toggling theme: ${e.message}")
                }
            }
            //endregion

            //region Clear posts
            clearPostsBtn.setOnClickListener {
                try
                {
                    val file = File(filesDir, "posts.json")
                    if (file.exists())
                    {
                        file.delete()
                        AppLogs.log("SettingsActivity", "Post history cleared")
                        Log.d("SettingsActivity", "Post history cleared")
                        Toast.makeText(this, "Post history cleared", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        AppLogs.log("SettingsActivity", "No post history file found")
                        Log.d("SettingsActivity", "No post history file found")
                        Toast.makeText(this, "No post history found", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception)
                {
                    AppLogs.log("SettingsActivity", "Error clearing posts: ${e.message}")
                    Log.e("SettingsActivity", "Error clearing posts: ${e.message}")
                    Toast.makeText(this, "Failed to clear posts", Toast.LENGTH_SHORT).show()
                }
            }
            //endregion
        }
        catch (e: Exception)
        {
            AppLogs.log("SettingsActivity", "Fatal error in onCreate: ${e.message}")
            Log.e("SettingsActivity", "Fatal error in onCreate: ${e.message}")
        }
    }
}
//endregion