//region Packages
package com.example.pinabug
//endregion

//region Imports
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
        AppLogs.log("Register", "onStart called")

        //region Check Status
        try
        {
            val currentUser = mAuth?.currentUser

            if (currentUser != null)
            {
                AppLogs.log("Register", "User already signed in: ${currentUser.email}")
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
            else
            {
                AppLogs.log("Register", "No user signed in at start")
            }

        }
        catch (e: Exception)
        {
            AppLogs.log("Register", "Error in onStart: ${e.message}")
        }

    //endregion
    }
    //endregion

    //region Main Logic
    @Suppress("ObjectLiteralToLambda")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        AppLogs.log("Register", "onCreate started")

        try
        {
            setContentView(R.layout.activity_register)
            AppLogs.log("Register", "Layout set successfully")

            //region Firebase Setup
            mAuth = FirebaseAuth.getInstance()
            AppLogs.log("Register", "FirebaseAuth initialized")
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
                    AppLogs.log("Register", "Login link clicked")
                    startActivity(Intent(applicationContext, Login::class.java))
                    finish()
                }
                catch (e: Exception)
                {
                    AppLogs.log("Register", "Failed to open Login: ${e.message}")
                }
            }
            //endregion

            //region Register Button
            buttonReg?.setOnClickListener {
                try
                {
                    AppLogs.log("Register", "Register button clicked")
                    progressBar?.visibility = View.VISIBLE

                    val email = editTextEmail?.text?.toString()?.trim()
                    val password = editTextPassword?.text?.toString()?.trim()

                    //region Check Email feild
                    if (TextUtils.isEmpty(email))
                    {
                        Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                        AppLogs.log("Register", "Email field empty")
                        progressBar?.visibility = View.GONE
                        return@setOnClickListener
                    }
                    //endregion

                    //region Check Password feild
                    if (TextUtils.isEmpty(password))
                    {
                        Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                        AppLogs.log("Register", "Password field empty")
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
                                        AppLogs.log("Register", "Account created for $email")
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
                                        AppLogs.log("Register", "Account creation failed: ${task.exception?.message}")
                                        Toast.makeText(
                                            this@Register,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                catch (e: Exception)
                                {
                                    AppLogs.log("Register", "Error in onComplete: ${e.message}")
                                }
                            }
                        })
                    //endregion

                }
                catch (e: Exception)
                {
                    AppLogs.log("Register", "Register button handler failed: ${e.message}")
                    progressBar?.visibility = View.GONE
                }
            }
            //endregion

        }
        catch (e: Exception)
        {
            AppLogs.log("Register", "Fatal error in onCreate: ${e.message}")
        }

        AppLogs.log("Register", "onCreate finished")
    }
    //endregion
}
//endregion
