package com.example.pinabug

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

class MainActivity : ComponentActivity() {
    private var auth: FirebaseAuth? = null
    private var button: Button? = null
    private var textView: TextView? = null
    private var user: FirebaseUser? = null

    private lateinit var map: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionCode = 100


    var openNewPostBtn: Button? = null



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")

        try {
            Configuration.getInstance().userAgentValue = packageName
            setContentView(R.layout.activity_main)
            Log.d("MainActivity", "Layout set successfully")

            auth = FirebaseAuth.getInstance()
            button = findViewById(R.id.logout)
//            textView = findViewById(R.id.user_details)

            openNewPostBtn = findViewById(R.id.openNewPostBtn)
            user = auth?.currentUser
            Log.d("MainActivity", "FirebaseAuth initialized, user = ${user?.email ?: "null"}")

            map = findViewById(R.id.map)
            map.setTileSource(TileSourceFactory.OpenTopo)
            map.setMultiTouchControls(true)
            Log.d("MainActivity", "Map initialized")

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            requestLocationPermission()

            if (user == null) {
                Log.w("MainActivity", "No user logged in, redirecting to Login")
                val intent = Intent(applicationContext, Login::class.java)
                startActivity(intent)
                finish()
            } else {
                textView?.text = user?.email
                Log.i("MainActivity", "User logged in: ${user?.email}")
            }

            button?.setOnClickListener {
                Log.d("MainActivity", "Logout button clicked")
                FirebaseAuth.getInstance().signOut()
                Log.i("MainActivity", "User signed out")
                val intent = Intent(applicationContext, Login::class.java)
                startActivity(intent)
                finish()
            }

            openNewPostBtn?.setOnClickListener {
                Log.d("MainActivity", "New post button clicked")
                val intent = Intent(this, NewPostActivity::class.java)
                startActivity(intent)
            }


        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
        }

        Log.d("MainActivity", "onCreate finished")
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        } else {
            getUserLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val userPoint = GeoPoint(location.latitude, location.longitude)
                    map.controller.setZoom(15.0)
                    map.controller.setCenter(userPoint)

                    val marker = Marker(map)
                    marker.position = userPoint
                    marker.title = "You are here"
                    map.overlays.add(marker)

                    Log.i("Location", "User location: ${location.latitude}, ${location.longitude}")
                } else {
                    Log.w("Location", "No location available")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Location", "Failed to get location: ${e.message}", e)
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,   // ✅ must be Array<String>
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionCode && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation()
        } else {
            Log.e("Location", "Permission denied")
        }
    }
}
