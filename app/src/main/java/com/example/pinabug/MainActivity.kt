//region Packages
package com.example.pinabug
//endregion

//region Imports
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
//endregion

//region MainActivity Component class
class MainActivity : ComponentActivity()
{
    //region Fields
    private var auth: FirebaseAuth? = null
    private var logoutBtn: Button? = null
    private var openNewPostBtn: Button? = null
    private var textView: TextView? = null
    private var user: FirebaseUser? = null
    private lateinit var map: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionCode = 100
    //endregion

    //region Main Logic
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")

        try
        {
            //region Layout Setup
            Configuration.getInstance().userAgentValue = packageName
            setContentView(R.layout.activity_main)
            Log.d("MainActivity", "Layout inflated successfully")
            //endregion

            //region Firebase Setup
            auth = FirebaseAuth.getInstance()
            user = auth?.currentUser
            Log.i("MainActivity", "FirebaseAuth initialized, user = ${user?.email ?: "null"}")
            //endregion

            //region Button Setup
            logoutBtn = findViewById(R.id.logout)
            openNewPostBtn = findViewById(R.id.openNewPostBtn)
            //endregion

            //region Map Setup
            try
            {
                map = findViewById(R.id.map)
                map.setTileSource(TileSourceFactory.OpenTopo)
                map.setMultiTouchControls(true)
                Log.d("MainActivity", "Map initialized")
            }
            catch (e: Exception)
            {
                Log.e("MainActivity", "Map initialization failed: ${e.message}", e)
            }
            //endregion

            //region Location Setup
            try
            {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                requestLocationPermission()
            }
            catch (e: Exception)
            {
                Log.e("MainActivity", "Location client setup failed: ${e.message}", e)
            }
            //endregion

            //region User Login Status
            if (user == null)
            {
                Log.w("MainActivity", "No user logged in, redirecting to Login")
                startActivity(Intent(applicationContext, Login::class.java))
                finish()
            }
            else
            {
                textView?.text = user?.email
                Log.i("MainActivity", "User logged in: ${user?.email}")
            }
            //endregion

            //region Button Listeners
            logoutBtn?.setOnClickListener { //WHY does Kotlin REFUSE to let me move this bracket??? Picky language...
                try
                {
                    Log.d("MainActivity", "Logout button clicked")
                    FirebaseAuth.getInstance().signOut()
                    Log.i("MainActivity", "User signed out")
                    startActivity(Intent(applicationContext, Login::class.java))
                    finish()
                }
                catch (e: Exception)
                {
                    Log.e("MainActivity", "Logout failed: ${e.message}", e)
                }
            }

            openNewPostBtn?.setOnClickListener {
                try {
                    Log.d("MainActivity", "New post button clicked")
                    startActivity(Intent(this, NewPostActivity::class.java))
                } catch (e: Exception) {
                    Log.e("MainActivity", "Failed to open NewPostActivity: ${e.message}", e)
                }
            }
            //endregion

        }
        catch (e: Exception)
        {
            Log.e("MainActivity", "Fatal error in onCreate: ${e.message}", e)
        }

        Log.d("MainActivity", "onCreate finished")
    }
    //endregion

    //region Function: Permissions
    private fun requestLocationPermission()
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            {
                Log.w("Location", "Requesting fine location permission")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    locationPermissionCode
                )
            }
            else
            {
                Log.d("Location", "Permission already granted, fetching location")
                getUserLocation()
            }
        }
        catch (e: Exception)
        {
            Log.e("Location", "Permission request failed: ${e.message}", e)
        }
    }
    //endregion

    //region Function: Location
    @SuppressLint("MissingPermission")
    private fun getUserLocation()
    {
        try
        {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    try
                    {
                        if (location != null)
                        {
                            val userPoint = GeoPoint(location.latitude, location.longitude)
                            map.controller.setZoom(15.0)
                            map.controller.setCenter(userPoint)

                            val marker = Marker(map)
                            marker.position = userPoint
                            marker.title = "You are here"
                            map.overlays.add(marker)

                            Log.i("Location", "User location: ${location.latitude}, ${location.longitude}")
                        }
                        else
                        {
                            Log.w("Location", "No location available")
                        }
                    }
                    catch (e: Exception)
                    {
                        Log.e("Location", "Error handling location result: ${e.message}", e)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "Failed to get location: ${e.message}", e)
                }
        }
        catch (e: Exception)
        {
            Log.e("Location", "getUserLocation failed: ${e.message}", e)
        }
    }
    //endregion

    //region Override Function: Deprecated Permission Callback
    @Deprecated("Use Activity Result API instead")
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    )
    {
        try
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == locationPermissionCode && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("Location", "Permission granted, fetching location")
                getUserLocation()
            }
            else
            {
                Log.e("Location", "Permission denied")
            }
        }
        catch (e: Exception)
        {
            Log.e("Location", "Error in onRequestPermissionsResult: ${e.message}", e)
        }
    }
    //endregion
}
//endregion
