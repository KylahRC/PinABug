package com.example.pinabug

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
            user = auth?.currentUser
            Log.d("MainActivity", "FirebaseAuth initialized, user = ${user?.email ?: "null"}")

            map = findViewById(R.id.map)
            map.setTileSource(TileSourceFactory.OpenTopo)
            map.setMultiTouchControls(true)
            Log.d("MainActivity", "Map initialized")

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

            val controller = map.controller
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(-33.9249, 18.4241))
            Log.d("MainActivity", "Map centered on Cape Town")

            val marker = Marker(map)
            marker.position = GeoPoint(-33.9249, 18.4241)
            marker.title = "Bug spotted!"
            map.overlays.add(marker)
            Log.d("MainActivity", "Test marker added")

        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
        }

        Log.d("MainActivity", "onCreate finished")
    }
}
