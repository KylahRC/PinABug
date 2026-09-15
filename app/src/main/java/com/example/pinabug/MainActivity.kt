package com.example.pinabug

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
    var auth: FirebaseAuth? = null
    var button: Button? = null
    var textView: TextView? = null
    var user: FirebaseUser? = null

    private lateinit var map: MapView



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()
        button = findViewById<Button>(R.id.logout)
//        textView = findViewById<TextView>(R.id.user_details)
        user = auth!!.getCurrentUser()
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        if (user == null) {
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        } else {
            textView!!.setText(user!!.getEmail())
        }
        button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(getApplicationContext(), Login::class.java)
                startActivity(intent)
                finish()
            }
        })

        val controller = map.controller
        controller.setZoom(15.0)
        controller.setCenter(GeoPoint(-33.9249, 18.4241)) // Cape Town

        // Add a test marker
        val marker = Marker(map)
        marker.position = GeoPoint(-33.9249, 18.4241)
        marker.title = "Bug spotted!"
        map.overlays.add(marker)
    }
}