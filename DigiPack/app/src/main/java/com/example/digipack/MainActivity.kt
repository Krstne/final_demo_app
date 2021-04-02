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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get the account the user signed in with
        val account = GoogleSignIn.getLastSignedInAccount(this)

        //if the user already signed in
        if(account != null )
        {
            //extract profile information, ID token
            val googleEmail = account.email
            val googleFirstName = account.givenName
            val googleLastName = account.familyName
            val googleProfilePicURL = account.photoUrl
            val googleIdToken = account.idToken
            val googleId = account.id

            //construct and start intent for Details activity
            val myIntent = Intent(this, DetailsActivity::class.java)
            myIntent.putExtra("google_id", googleId)
            myIntent.putExtra("google_first_name", googleFirstName)
            myIntent.putExtra("google_last_name", googleLastName)
            myIntent.putExtra("google_email", googleEmail)
            myIntent.putExtra("google_profile_pic_url", googleProfilePicURL)
            myIntent.putExtra("google_auth_code", googleIdToken)
            myIntent.putExtra("firstSignIn", false)

            this.startActivity(myIntent)
        }
        //else user isnt signed in

        //initialize google sign in object
        val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestIdToken( getString(R.string.serverClientId) )
                        .requestScopes( Scope (Scopes.DRIVE_FULL),
                                Scope ("https://www.googleapis.com/auth/classroom.courses"),
                                Scope ("https://www.googleapis.com/auth/classroom.coursework.me"),
                                Scope ("https://www.googleapis.com/auth/classroom.announcements"),
                                Scope ("https://www.googleapis.com/auth/classroom.guardianlinks.me.readonly") )
                        .requestServerAuthCode( getString(R.string.serverClientId), true)
                        .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        google_sign_in_button.setOnClickListener {
            signIn()
        }
    }


    //sign in function for the google sign in button
    private fun signIn() {
        val userSignInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
                userSignInIntent, RC_SIGN_IN //Passes result to onActivityResult
        )
    }

    // Checks if the requestCode is the same, if so then continue the sign in process
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //if block handled activityResult for signIn
        if (requestCode == RC_SIGN_IN) {
            val task =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task) //passes task to handleSignInResult
        }
    }

    // The user is signed in successfully and will get the user's basic info
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val userAccount = completedTask.getResult(
                    ApiException::class.java
            )
            // Signed in successfully, extract progile information
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

            val googleIdToken = userAccount?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

            val googleAuthCode = userAccount?.serverAuthCode ?: "" //auth code used for registration with server
            println(googleAuthCode)

            // construct and launch an intent for DetailsActivity
            val myIntent = Intent(this, DetailsActivity::class.java)
            myIntent.putExtra("google_id", googleId)
            myIntent.putExtra("google_first_name", googleFirstName)
            myIntent.putExtra("google_last_name", googleLastName)
            myIntent.putExtra("google_email", googleEmail)
            myIntent.putExtra("google_profile_pic_url", googleProfilePicURL)
            myIntent.putExtra("google_auth_code", googleAuthCode)
            myIntent.putExtra("firstSignIn", true)
            this.startActivity(myIntent)
        } catch (e: ApiException) {
            // Checks if the sign in is unsuccessful, if not then throws an error code
            Log.e(
                    "failed code=", e.statusCode.toString()
            )
        }
    }
}