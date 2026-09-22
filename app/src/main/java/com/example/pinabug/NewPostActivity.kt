//region Packages
package com.example.pinabug
//endregion

//region Imports
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
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
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                if (ActivityCompat.checkSelfPermission( //this was a suggested quick fix from Android studio
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@setOnClickListener
                }
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val post = Post(
                        name = bugNameInput.text.toString(),
                        imageUri = imageUri?.toString(),
                        timestamp = System.currentTimeMillis(),
                        latitude = location?.latitude,
                        longitude = location?.longitude
                    )
                    savePost(post)
                    finish()
                }.addOnFailureListener { e ->
                    AppLogs.log("NewPostActivity", "Failed to get location: ${e.message}")
                    Log.e("NewPostActivity", "Failed to get location: ${e.message}")

                    val post = Post(
                        name = bugNameInput.text.toString(),
                        imageUri = imageUri?.toString(),
                        timestamp = System.currentTimeMillis(),
                        latitude = null,
                        longitude = null
                    )
                    savePost(post)
                    finish()
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

    //region Save Post Function
    private fun savePost(post: Post)
    {
        val file = File(filesDir, "posts.json")
        val postsArray: JSONArray =
            if (file.exists())
            {
                JSONArray(file.readText())
            }
            else
            {
                JSONArray()
            }

        val postJson = JSONObject().apply {
            put("name", post.name)
            put("imageUri", post.imageUri)
            put("timestamp", post.timestamp)
            put("latitude", post.latitude)
            put("longitude", post.longitude)
        }

        postsArray.put(postJson)
        file.writeText(postsArray.toString())

        AppLogs.log("NewPostActivity", "Post saved: ${post.name}")
        Log.d("NewPostActivity", "Post saved: ${post.name}")
    }

    //endregion
}
//endregion
