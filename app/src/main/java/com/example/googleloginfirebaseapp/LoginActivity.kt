package com.example.googleloginfirebaseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.googleloginfirebaseapp.databinding.ActivityLoginBinding
import com.example.googleloginfirebaseapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
const val REQUEST_CODE_SIGN_IN = 0
class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
        lateinit var auth: FirebaseAuth
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            auth = FirebaseAuth.getInstance()
            binding.btnSignIn.setOnClickListener {
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id)).requestEmail()
                    .build()

                val signInClient = GoogleSignIn.getClient(this, options)
                signInClient.signInIntent.also {
                    startActivityForResult(it, REQUEST_CODE_SIGN_IN)
                }

            }


        }

        private fun updateUI(user: FirebaseUser?) {
            //Navigate to the Main Activity
            if (user == null) {
                Log.w("Login Not", "User is null, not going to navigate")
                return
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish() // close down the log in actvity if user is logged in


        }

        private fun googleAuthForFirebase(account: GoogleSignInAccount) {
            val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithCredential(credentials).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Successfully logged in",
                            Toast.LENGTH_LONG
                        ).show()
                        updateUI(auth.currentUser)
                    }


                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == REQUEST_CODE_SIGN_IN) {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                account?.let {
                    googleAuthForFirebase(it)
                }
            }
        }
    }
