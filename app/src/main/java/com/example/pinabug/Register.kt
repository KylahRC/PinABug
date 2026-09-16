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

//region Register Class
class Register : AppCompatActivity()
{

    //region Fields
    private var editTextEmail: TextInputEditText? = null
    private var editTextPassword: TextInputEditText? = null
    private var buttonReg: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null
    private var textView: TextView? = null
    //endregion

    //region Start Logic
    public override fun onStart()
    {
        super.onStart()
        Log.d("Register", "onStart called")

        try
        {
            val currentUser = mAuth?.currentUser
            if (currentUser != null)
            {
                Log.i("Register", "User already signed in: ${currentUser.email}")
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
            else
            {
                Log.d("Register", "No user signed in at start")
            }
        }
        catch (e: Exception)
        {
            Log.e("Register", "Error in onStart: ${e.message}", e)
        }
    }
    //endregion

    //region Main Logic
    @Suppress("ObjectLiteralToLambda")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.d("Register", "onCreate started")

        try
        {
            setContentView(R.layout.activity_register)
            Log.d("Register", "Layout set successfully")

            //region Firebase Setup
            mAuth = FirebaseAuth.getInstance()
            Log.d("Register", "FirebaseAuth initialized")
            //endregion

            //region UI References
            editTextEmail = findViewById(R.id.email)
            editTextPassword = findViewById(R.id.password)
            buttonReg = findViewById(R.id.btn_register)
            progressBar = findViewById(R.id.progressBar)
            textView = findViewById(R.id.loginNow)
            //endregion

            //region Login Navigation
            textView?.setOnClickListener {
                try
                {
                    Log.d("Register", "Login link clicked")
                    startActivity(Intent(applicationContext, Login::class.java))
                    finish()
                }
                catch (e: Exception)
                {
                    Log.e("Register", "Failed to open Login: ${e.message}", e)
                }
            }
            //endregion

            //region Register Button
            buttonReg?.setOnClickListener {
                try
                {
                    Log.d("Register", "Register button clicked")
                    progressBar?.visibility = View.VISIBLE

                    //region Values
                    val email = editTextEmail?.text?.toString()?.trim()
                    val password = editTextPassword?.text?.toString()?.trim()
                    //endregion

                    //region Empty Email
                    if (TextUtils.isEmpty(email))
                    {
                        Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                        Log.w("Register", "Email field empty")
                        progressBar?.visibility = View.GONE
                        return@setOnClickListener
                    }
                    //endregion

                    //region Empty Password
                    if (TextUtils.isEmpty(password))
                    {
                        Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                        Log.w("Register", "Password field empty")
                        progressBar?.visibility = View.GONE
                        return@setOnClickListener
                    }
                    //endregion

                    //region Firebase Account Creation
                    mAuth?.createUserWithEmailAndPassword(email!!, password!!)
                        ?.addOnCompleteListener(object : OnCompleteListener<AuthResult?>
                        {
                            override fun onComplete(task: Task<AuthResult?>)
                            {
                                progressBar?.visibility = View.GONE
                                try
                                {
                                    if (task.isSuccessful)
                                    {
                                        Log.i("Register", "Account created for $email")
                                        Toast.makeText(
                                            applicationContext,
                                            "Account Created.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(applicationContext, MainActivity::class.java))
                                        finish()
                                    }
                                    else
                                    {
                                        Log.w("Register", "Account creation failed: ${task.exception?.message}", task.exception)
                                        Toast.makeText(
                                            this@Register,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                catch (e: Exception)
                                {
                                    Log.e("Register", "Error in onComplete: ${e.message}", e)
                                }
                            }
                        })
                    //endregion

                }
                catch (e: Exception)
                {
                    Log.e("Register", "Register button handler failed: ${e.message}", e)
                    progressBar?.visibility = View.GONE
                }
            }
            //endregion

        }
        catch (e: Exception)
        {
            Log.e("Register", "Fatal error in onCreate: ${e.message}", e)
        }

        Log.d("Register", "onCreate finished")
    }
    //endregion
}

//endregion