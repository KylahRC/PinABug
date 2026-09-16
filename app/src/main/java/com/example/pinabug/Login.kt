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

    //region Main Logic
    public override fun onStart()
    {
        super.onStart()
        Log.d("Login", "onStart called")

        try
        {
            val currentUser = mAuth?.currentUser
            if (currentUser != null)
            {
                Log.i("Login", "User already signed in: ${currentUser.email}")
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
            else
            {
                Log.d("Login", "No user signed in at start")
            }
        }
        catch (e: Exception)
        {
            Log.e("Login", "Error in onStart: ${e.message}", e)
        }
    }

    @Suppress("ObjectLiteralToLambda")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.d("Login", "onCreate started")

        try
        {
            setContentView(R.layout.activity_login)
            Log.d("Login", "Layout set successfully")

            //region Firebase Setup
            mAuth = FirebaseAuth.getInstance()
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
                    Log.d("Login", "Register link clicked")
                    startActivity(Intent(applicationContext, Register::class.java))
                    finish()
                }
                catch (e: Exception)
                {
                    Log.e("Login", "Failed to open Register: ${e.message}", e)
                }
            }
            //endregion

            //region Login Button
            buttonLogin?.setOnClickListener {
                try
                {
                    Log.d("Login", "Login button clicked")
                    progressBar?.visibility = View.VISIBLE

                    val email = editTextEmail?.text?.toString()?.trim()
                    val password = editTextPassword?.text?.toString()?.trim()

                    if (TextUtils.isEmpty(email))
                    {
                        Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                        Log.w("Login", "Email field empty")
                        progressBar?.visibility = View.GONE
                        return@setOnClickListener
                    }

                    if (TextUtils.isEmpty(password))
                    {
                        Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                        Log.w("Login", "Password field empty")
                        progressBar?.visibility = View.GONE
                        return@setOnClickListener
                    }

                    //region Firebase Sign-In
                    mAuth?.signInWithEmailAndPassword(email!!, password!!)
                        ?.addOnCompleteListener(object : OnCompleteListener<AuthResult?>
                        {
                            override fun onComplete(task: Task<AuthResult?>)
                            {
                                progressBar?.visibility = View.GONE
                                try
                                {
                                    if (task.isSuccessful)
                                    {
                                        Log.i("Login", "Login successful for $email")
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
                                        Log.e("Login", "Authentication failed: ${task.exception?.message}", task.exception)
                                        Toast.makeText(
                                            this@Login,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                catch (e: Exception)
                                {
                                    Log.e("Login", "Error in onComplete: ${e.message}", e)
                                }
                            }
                        })
                    //endregion

                }
                catch (e: Exception)
                {
                    Log.e("Login", "Login button handler failed: ${e.message}", e)
                    progressBar?.visibility = View.GONE
                }
            }
            //endregion

        }
        catch (e: Exception)
        {
            Log.e("Login", "Fatal error in onCreate: ${e.message}", e)
        }

        Log.d("Login", "onCreate finished")
    }
    //endregion
}

//endregion