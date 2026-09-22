//region Packages
package com.example.pinabug
//endregion

//region Imports
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
//endregion

//region Login Class
class Login : AppCompatActivity()
{

    //region Fields
    private var editTextEmail: TextInputEditText? = null
    private var editTextPassword: TextInputEditText? = null
    private var buttonLogin: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null
    private var textView: TextView? = null
    //endregion

    //region Start Logic
    public override fun onStart()
    {
        super.onStart()
        AppLogs.log("Login", "onStart called")
        Log.d("Login", "onStart called")

        try
        {
            val currentUser = mAuth?.currentUser
            if (currentUser != null)
            {
                AppLogs.log("Login", "User already signed in: ${currentUser.email}")
                Log.d("Login", "User already signed in: ${currentUser.email}")
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
            else
            {
                AppLogs.log("Login", "No user signed in at start")
                Log.d("Login", "No user signed in at start")
            }
        }
        catch (e: Exception)
        {
            AppLogs.log("Login", "Error in onStart: ${e.message}")
            Log.e("Login", "Error in onStart: ${e.message}")
        }
    }
    //endregion

    //region Main Logic
    @Suppress("ObjectLiteralToLambda")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        AppLogs.log("Login", "onCreate started")
        Log.d("Login", "onCreate started")

        try
        {
            setContentView(R.layout.activity_login)
            AppLogs.log("Login", "Layout set successfully")
            Log.d("Login", "Layout set successfully")

            //region Firebase Setup
            mAuth = FirebaseAuth.getInstance()
            AppLogs.log("Login", "FirebaseAuth initialized")
            Log.d("Login", "FirebaseAuth initialized")
            //endregion

            //region UI References
            editTextEmail = findViewById(R.id.email)
            editTextPassword = findViewById(R.id.password)
            buttonLogin = findViewById(R.id.btn_login)
            progressBar = findViewById(R.id.progressBar)
            textView = findViewById(R.id.registerNow)
            //endregion

            //region Register Navigation
            textView?.setOnClickListener {
                try
                {
                    AppLogs.log("Login", "Register link clicked")
                    Log.d("Login", "Register link clicked")
                    startActivity(Intent(applicationContext, Register::class.java))
                    finish()
                }
                catch (e: Exception)
                {
                    AppLogs.log("Login", "Failed to open Register: ${e.message}")
                    Log.e("Login", "Failed to open Register: ${e.message}")
                }
            }
            //endregion

            //region Login Button
            buttonLogin?.setOnClickListener {
                try
                {
                    AppLogs.log("Login", "Login button clicked")
                    Log.d("Login", "Login button clicked")
                    progressBar?.visibility = View.VISIBLE

                    val email = editTextEmail?.text?.toString()?.trim()
                    val password = editTextPassword?.text?.toString()?.trim()

                    //region Check for email
                    if (TextUtils.isEmpty(email))
                    {
                        Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                        AppLogs.log("Login", "Email field empty")
                        Log.d("Login", "Email field empty")
                        progressBar?.visibility = View.GONE
                        return@setOnClickListener
                    }
                    //endregion

                    //region Check for password
                    if (TextUtils.isEmpty(password))
                    {
                        Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                        AppLogs.log("Login", "Password field empty")
                        Log.d("Login", "Password field empty")
                        progressBar?.visibility = View.GONE
                        return@setOnClickListener
                    }
                    //endregion

                    //region Firebase Sign-In
                    mAuth?.signInWithEmailAndPassword(email!!, password!!)
                        ?.addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                            override fun onComplete(task: Task<AuthResult?>)
                            {
                                progressBar?.visibility = View.GONE
                                try
                                {
                                    if (task.isSuccessful)
                                    {
                                        AppLogs.log("Login", "Login successful for $email")
                                        Log.d("Login", "Login successful for $email")
                                        Toast.makeText(
                                            applicationContext,
                                            "Login Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(applicationContext, MainActivity::class.java))
                                        finish()
                                    }
                                    else
                                    {
                                        AppLogs.log("Login", "Authentication failed: ${task.exception?.message}")
                                        Log.e("Login", "Authentication failed: ${task.exception?.message}")
                                        Toast.makeText(
                                            this@Login,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                catch (e: Exception)
                                {
                                    AppLogs.log("Login", "Error in onComplete: ${e.message}")
                                    Log.e("Login", "Error in onComplete: ${e.message}")
                                }
                            }
                        })
                    //endregion

                }
                catch (e: Exception)
                {
                    AppLogs.log("Login", "Login button handler failed: ${e.message}")
                    Log.e("Login", "Login button handler failed: ${e.message}")
                    progressBar?.visibility = View.GONE
                }
            }
            //endregion

        }
        catch (e: Exception)
        {
            AppLogs.log("Login", "Fatal error in onCreate: ${e.message}")
            Log.e("Login", "Fatal error in onCreate: ${e.message}")
        }

        AppLogs.log("Login", "onCreate finished")
        Log.d("Login", "onCreate finished")
    }
    //endregion
}
//endregion
