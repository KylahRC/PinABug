//region Packages
package com.example.pinabug
//endregion

//region Imports
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
//endregion

//region NewPostActivity Class
class NewPostActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        findViewById<Button>(R.id.backBtn).setOnClickListener {
            finish() // closes this activity and returns to MainActivity
        }

    }
}
//endregion