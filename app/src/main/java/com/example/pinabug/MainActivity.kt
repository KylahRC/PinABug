//https://github.com/osmdroid/osmdroid/wiki
//https://codesignal.com/learn/courses/handling-json-files-with-kotlin-2/lessons/introduction-to-json-handling-with-kotlin
//https://codesignal.com/learn/courses/handling-json-files-with-kotlin-2/lessons/introduction-to-json-handling-in-kotlin-1
//https://developer.android.com/develop/ui/views/components/menus?hl=en
//https://developer.android.com/develop/ui/views/theming/themes


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
import android.widget.Toast
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Date
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle

//endregion

//region MainActivity Component class
class MainActivity : ComponentActivity()
{
    //region Fields
    private var auth: FirebaseAuth? = null
    private var logoutBtn: Button? = null
    private var openNewPostBtn: Button? = null
    private var textView: TextView? = null

    private var postData: TextView? = null
    private var user: FirebaseUser? = null
    private lateinit var map: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionCode = 100
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    //endregion

    //region Main Logic
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        //region Custom Logging start
        AppLogs.init(this)
        AppLogs.log("MainActivity", "App started")
        Log.d("MainActivity", "App started")
        AppLogs.log("MainActivity", "onCreate started")
        Log.d("MainActivity", "onCreate started")
        //endregion

        //region Setups and Calls
        try
        {
            //region Layout Setup
            Configuration.getInstance().userAgentValue = packageName
            val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val isBrownTheme = prefs.getBoolean("BrownTheme", false)

            if (isBrownTheme)
            {
                setTheme(R.style.Theme_PinABug_Brown)
            }
            else
            {
                setTheme(R.style.Theme_PinABug)
            }

            setContentView(R.layout.activity_main)
            loadPostsIntoTextView()
            AppLogs.log("MainActivity", "Layout inflated successfully")
            Log.d("MainActivity", "Layout inflated successfully")
            //endregion

            //region Firebase Setup
            auth = FirebaseAuth.getInstance()
            user = auth?.currentUser
            AppLogs.log("MainActivity", "FirebaseAuth initialized, user = ${user?.email ?: "null"}")
            Log.d("MainActivity", "FirebaseAuth initialized, user = ${user?.email ?: "null"}")
            //endregion

            //region Button Setup
            logoutBtn = findViewById(R.id.logout)
            openNewPostBtn = findViewById(R.id.openNewPostBtn)
            //endregion

            //region Load Posts
            postData = findViewById(R.id.postData)
            AppLogs.log("MainActivity", "Post data loaded")
            Log.d("MainActivity", "post data loaded")
            //endregion

            //region Menu
            try
            {
                drawerLayout = findViewById(R.id.drawerLayout)
                navigationView = findViewById(R.id.navigationView)
                val toolbar = findViewById<Toolbar>(R.id.toolbar)

                val toggle = ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
                )
                drawerLayout.addDrawerListener(toggle)
                toggle.syncState()

                AppLogs.log("MainActivity", "Hamburger menu initialized")
                Log.d("MainActivity", "Hamburger menu initialized")

                navigationView.setNavigationItemSelectedListener { item ->
                    try
                    {
                        when (item.itemId)
                        {
                            R.id.nav_new_post ->
                            {
                                AppLogs.log("MainActivity", "Menu: New Post clicked")
                                Log.d("MainActivity", "Menu: New Post clicked")
                                startActivity(Intent(this, NewPostActivity::class.java))
                            }
                            R.id.nav_logout ->
                            {
                                AppLogs.log("MainActivity", "Menu: Logout clicked")
                                Log.d("MainActivity", "Menu: Logout clicked")
                                FirebaseAuth.getInstance().signOut()
                                AppLogs.log("MainActivity", "User signed out via menu")
                                Log.d("MainActivity", "User signed out via menu")
                                startActivity(Intent(this, Login::class.java))
                                finish()
                            }
                            R.id.nav_settings ->
                            {
                                AppLogs.log("MainActivity", "Menu: Settings clicked")
                                Log.d("MainActivity", "Menu: Settings clicked")

                                startActivity(Intent(this, SettingsActivity::class.java))
                            }
                        }
                        drawerLayout.closeDrawers()
                        true
                    }
                    catch (e: Exception)
                    {
                        AppLogs.log("MainActivity", "Error handling menu item: ${e.message}")
                        Log.e("MainActivity", "Error handling menu item: ${e.message}")
                        false
                    }
                }
            }
            catch (e: Exception)
            {
                AppLogs.log("MainActivity", "Hamburger menu setup failed: ${e.message}")
                Log.e("MainActivity", "Hamburger menu setup failed: ${e.message}")
            }
            //endregion

            //region Map Setup
            try
            {
                map = findViewById(R.id.map)
                map.setTileSource(TileSourceFactory.OpenTopo)
                map.setMultiTouchControls(true)
                AppLogs.log("MainActivity", "Map initialized")
                Log.d("MainActivity", "Map initialized")
            }
            catch (e: Exception)
            {
                AppLogs.log("MainActivity", "Map initialization failed: ${e.message}")
                Log.e("MainActivity", "Map initialization failed: ${e.message}")
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
                AppLogs.log("MainActivity", "Location client setup failed: ${e.message}")
                Log.e("MainActivity", "Location client setup failed: ${e.message}")
            }
            //endregion

            //region User Login Status
            if (user == null)
            {
                AppLogs.log("MainActivity", "No user logged in, redirecting to Login")
                Log.d("MainActivity", "No user logged in, redirecting to Login")
                startActivity(Intent(applicationContext, Login::class.java))
                finish()
            }
            else
            {
                textView?.text = user?.email
                AppLogs.log("MainActivity", "User logged in: ${user?.email}")
                Log.d("MainActivity", "User logged in: ${user?.email}")
            }
            //endregion

            //region Button Listeners
            logoutBtn?.setOnClickListener {
                try
                {
                    AppLogs.log("MainActivity", "Logout button clicked")
                    Log.d("MainActivity", "Logout button clicked")
                    FirebaseAuth.getInstance().signOut()
                    AppLogs.log("MainActivity", "User signed out")
                    Log.d("MainActivity", "User signed out")
                    startActivity(Intent(applicationContext, Login::class.java))
                    finish()
                }
                catch (e: Exception)
                {
                    AppLogs.log("MainActivity", "Logout failed: ${e.message}")
                    Log.e("MainActivity", "Logout failed: ${e.message}")
                }
            }

            openNewPostBtn?.setOnClickListener{
                try
                {
                    AppLogs.log("MainActivity", "New post button clicked")
                    Log.d("MainActivity", "New post button clicked")
                    startActivity(Intent(this, NewPostActivity::class.java))
                }
                catch (e: Exception)
                {
                    AppLogs.log("MainActivity", "Failed to open NewPostActivity: ${e.message}")
                    Log.e("MainActivity", "Failed to open NewPostActivity: ${e.message}")
                }
            }
            //endregion

            loadPinsFromPosts()

        }
        catch (e: Exception)
        {
            AppLogs.log("MainActivity", "Fatal error in onCreate: ${e.message}")
            Log.e("MainActivity", "Fatal error in onCreate: ${e.message}")
        }
        //endregion

        AppLogs.log("MainActivity", "onCreate finished")
        Log.d("MainActivity", "onCreate finished")
    }
    //endregion

    //region Function: Permissions
    private fun requestLocationPermission()
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            {
                AppLogs.log("Location", "Requesting fine location permission")
                Log.d("Location", "Requesting fine location permission")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    locationPermissionCode
                )
            }
            else
            {
                AppLogs.log("Location", "Permission already granted, fetching location")
                Log.d("Location", "Permission already granted, fetching location")
                getUserLocation()
            }
        }
        catch (e: Exception)
        {
            AppLogs.log("Location", "Permission request failed: ${e.message}")
            Log.e("Location", "Permission request failed: ${e.message}")
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

                            AppLogs.log("Location", "User location: ${location.latitude}, ${location.longitude}")
                            Log.d("Location", "User location: ${location.latitude}, ${location.longitude}")
                        }
                        else
                        {
                            AppLogs.log("Location", "No location available")
                            Log.d("Location", "No location available")
                        }
                    }
                    catch (e: Exception)
                    {
                        AppLogs.log("Location", "Error handling location result: ${e.message}")
                        Log.e("Location", "Error handling location result: ${e.message}")
                    }
                }
                .addOnFailureListener { e ->
                    AppLogs.log("Location", "Failed to get location: ${e.message}")
                    Log.e("Location", "Failed to get location: ${e.message}")
                }
        }
        catch (e: Exception)
        {
            AppLogs.log("Location", "getUserLocation failed: ${e.message}")
            Log.e("Location", "getUserLocation failed: ${e.message}")
        }
    }
    //endregion

    //region Override Function: Get Location
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
                AppLogs.log("Location", "Permission granted, fetching location")
                Log.d("Location", "Permission granted, fetching location")
                getUserLocation()
            }
            else
            {
                AppLogs.log("Location", "Permission denied")
                Log.d("Location", "Permission denied")
            }
        }
        catch (e: Exception)
        {
            AppLogs.log("Location", "Error in onRequestPermissionsResult: ${e.message}")
            Log.e("Location", "Error in onRequestPermissionsResult: ${e.message}")
        }
    }
    //endregion

    //region Load post data from JSON
    private fun loadPostsIntoTextView()
    {
        val file = File(filesDir, "posts.json")
        if (!file.exists())
        {
            postData?.text = "No posts yet."
            return
        }

        val postsArray = JSONArray(file.readText())
        val builder = StringBuilder()

        for (i in 0 until postsArray.length())
        {
            val obj: JSONObject = postsArray.getJSONObject(i)
            builder.append("Name: ${obj.optString("name")}\n")
            builder.append("Time: ${Date(obj.optLong("timestamp"))}\n")
            builder.append("Location: ${obj.optDouble("latitude", Double.NaN)}, ${obj.optDouble("longitude", Double.NaN)}\n")
            builder.append("Image: ${obj.optString("imageUri")}\n\n")
        }

        postData?.text = builder.toString()
    }
    //endregion

    //region Update post history
    override fun onResume()
    {
        super.onResume()
        loadPostsIntoTextView()
        loadPinsFromPosts()
    }
    //endregion

    //region Pins
    private fun loadPinsFromPosts()
    {
        try
        {
            val file = File(filesDir, "posts.json")
            if (!file.exists())
            {
                AppLogs.log("MainActivity", "No posts.json found, no pins to load")
                Log.d("MainActivity", "No posts.json found, no pins to load")
                return
            }

            val postsArray = JSONArray(file.readText())
            for (i in 0 until postsArray.length())
            {
                val obj = postsArray.getJSONObject(i)
                val lat = obj.optDouble("latitude", Double.NaN)
                val lon = obj.optDouble("longitude", Double.NaN)

                if (!lat.isNaN() && !lon.isNaN())
                {
                    val point = GeoPoint(lat, lon)
                    val marker = Marker(map)
                    marker.position = point
                    marker.title = obj.optString("name", "Unknown")
                    marker.snippet = obj.optString("imageUri", "")
                    
                    marker.snippet = "Image: ${obj.optString("imageUri", "None")}\nTime: ${Date(obj.optLong("timestamp"))}"
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    // Default behavior: tapping shows title + snippet in a popup
                    marker.setOnMarkerClickListener { m, _ ->
                        try
                        {
                            m.showInfoWindow()
                            AppLogs.log("MainActivity", "Pin clicked: ${m.title}")
                            Log.d("MainActivity", "Pin clicked: ${m.title}")
                            true
                        }
                        catch (e: Exception)
                        {
                            AppLogs.log("MainActivity", "Error showing pin info: ${e.message}")
                            Log.e("MainActivity", "Error showing pin info: ${e.message}")
                            false
                        }
                    }
                    map.overlays.add(marker)
                    AppLogs.log("MainActivity", "Pin added: ${marker.title} at $lat,$lon")
                    Log.d("MainActivity", "Pin added: ${marker.title} at $lat,$lon")
                }
            }
            map.invalidate()

        }
        catch (e: Exception)
        {
            AppLogs.log("MainActivity", "Error loading pins: ${e.message}")
            Log.e("MainActivity", "Error loading pins: ${e.message}")
        }
    }
    //endregion

}
//endregion
