package com.example.pinabug

//region Imports
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

//endregion

class SettingsActivity : AppCompatActivity()
{

    private lateinit var themeSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        try
        {
            setContentView(R.layout.activity_settings)
            themeSwitch = findViewById(R.id.themeSwitch)

            // Load saved preference
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
        }
        catch (e: Exception)
        {
            AppLogs.log("SettingsActivity", "Fatal error in onCreate: ${e.message}")
            Log.e("SettingsActivity", "Fatal error in onCreate: ${e.message}")
        }
    }
}
