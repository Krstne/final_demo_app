package com.example.digipack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    // Initialize the Google Sign in
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val account = GoogleSignIn.getLastSignedInAccount(this)

        if(account != null )
        {
            val googleEmail = account.email
            val googleFirstName = account.givenName
            val googleLastName = account.familyName
            val googleProfilePicURL = account.photoUrl
            val googleIdToken = account.idToken
            val googleId = account.id


            val myIntent = Intent(this, DetailsActivity::class.java)
            myIntent.putExtra("google_id", googleId)
            myIntent.putExtra("google_first_name", googleFirstName)
            myIntent.putExtra("google_last_name", googleLastName)
            myIntent.putExtra("google_email", googleEmail)
            myIntent.putExtra("google_profile_pic_url", googleProfilePicURL)
            myIntent.putExtra("google_id_token", googleIdToken)
            this.startActivity(myIntent)
        }

        val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestIdToken( getString(R.string.serverClientId) )
                        .requestScopes( Scope (Scopes.DRIVE_FULL))
                        .requestServerAuthCode( getString(R.string.serverClientId), true)
                        .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        google_sign_in_button.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val userSignInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
                userSignInIntent, RC_SIGN_IN
        )
    }

    // Checks if the requestCode is the same, if so then continue the sign in process
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // The user is signed in successfully and will get the user's basic info
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val userAccount = completedTask.getResult(
                    ApiException::class.java
            )
            // Signed in successfully
            val googleId = userAccount?.id ?: ""
            Log.i("Google ID",googleId)

            val googleFirstName = userAccount?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = userAccount?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = userAccount?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = userAccount?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            // The ID Token needed for the backend ????
            // This is not showing on the result area.
            val googleIdToken = userAccount?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

            val googleAuthCode = userAccount?.serverAuthCode ?: ""
            println(googleAuthCode)

            // The grabbed items are supposed to be displayed on the DetailsActivity Page
            val myIntent = Intent(this, DetailsActivity::class.java)
            myIntent.putExtra("google_id", googleId)
            myIntent.putExtra("google_first_name", googleFirstName)
            myIntent.putExtra("google_last_name", googleLastName)
            myIntent.putExtra("google_email", googleEmail)
            myIntent.putExtra("google_profile_pic_url", googleProfilePicURL)
            myIntent.putExtra("google_id_token", googleIdToken)
            this.startActivity(myIntent)
        } catch (e: ApiException) {
            // Checks if the sign in is unsuccessful, if not then throws an error code
            Log.e(
                    "failed code=", e.statusCode.toString()
            )
        }
    }
}