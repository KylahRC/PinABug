//region Packages
package com.example.pinabug
//endregion

//region Imports
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
//endregion

//region NewPostActivity Class
class NewPostActivity : AppCompatActivity()
{

    //region Fields
    private lateinit var previewImage: ImageView
    private lateinit var bugNameInput: EditText
    private lateinit var postBtn: Button
    private lateinit var cancelBtn: Button
    private var imageUri: Uri? = null
    //endregion

    //region Main Logic
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        try
        {
            AppLogs.log("NewPostActivity", "onCreate started")
            Log.d("NewPostActivity", "onCreate started")

            setContentView(R.layout.activity_new_post)

            //region UI Setup
            previewImage = findViewById(R.id.previewImage)
            bugNameInput = findViewById(R.id.bugNameInput)
            postBtn = findViewById(R.id.postBtn)
            cancelBtn = findViewById(R.id.cancelBtn)
            AppLogs.log("NewPostActivity", "UI setup complete")
            Log.d("NewPostActivity", "UI setup complete")
            //endregion

            //region Button Listeners

            // region Image picker
            previewImage.setOnClickListener {
                try
                {
                    AppLogs.log("NewPostActivity", "Image area clicked")
                    Log.d("NewPostActivity", "Image area clicked")
                    val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                    startActivityForResult(intent, 100)
                }
                catch (e: Exception)
                {
                    AppLogs.log("NewPostActivity", "Error opening image picker: ${e.message}")
                    Log.e("NewPostActivity", "Error opening image picker: ${e.message}")
                }
            }
            //endregion

            //region Submit Post
            postBtn.setOnClickListener {
                try
                {
                    AppLogs.log("NewPostActivity", "Post button clicked with bug name: ${bugNameInput.text}")
                    Log.d("NewPostActivity", "Post button clicked with bug name: ${bugNameInput.text}")
                    finish()
                }
                catch (e: Exception)
                {
                    AppLogs.log("NewPostActivity", "Error handling Post button: ${e.message}")
                    Log.e("NewPostActivity", "Error handling Post button: ${e.message}")
                }
            }
            //endregion

            //region Cancel new post
            cancelBtn.setOnClickListener {
                try
                {
                    AppLogs.log("NewPostActivity", "Cancel button clicked")
                    Log.d("NewPostActivity", "Cancel button clicked")
                    finish()
                }
                catch (e: Exception)
                {
                    AppLogs.log("NewPostActivity", "Error handling Cancel button: ${e.message}")
                    Log.e("NewPostActivity", "Error handling Cancel button: ${e.message}")
                }
            }
            //endregion

            //endregion

            AppLogs.log("NewPostActivity", "onCreate finished")
            Log.d("NewPostActivity", "onCreate finished")
        }
        catch (e: Exception)
        {
            AppLogs.log("NewPostActivity", "Fatal error in onCreate: ${e.message}")
            Log.e("NewPostActivity", "Fatal error in onCreate: ${e.message}")
        }
    }
    //endregion

    //region Activity Result Image Selected
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        try
        {
            if (requestCode == 100 && resultCode == RESULT_OK)
            {
                imageUri = data?.data
                previewImage.setImageURI(imageUri)
                AppLogs.log("NewPostActivity", "Image selected: $imageUri")
                Log.d("NewPostActivity", "Image selected: $imageUri")
            }
            else
            {
                AppLogs.log("NewPostActivity", "Image selection cancelled or failed")
                Log.d("NewPostActivity", "Image selection cancelled or failed")
            }
        }
        catch (e: Exception)
        {
            AppLogs.log("NewPostActivity", "Error handling image result: ${e.message}")
            Log.e("NewPostActivity", "Error handling image result: ${e.message}")
        }
    }
    //endregion
}
//endregion
