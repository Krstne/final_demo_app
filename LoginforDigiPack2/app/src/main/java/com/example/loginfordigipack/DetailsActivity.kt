package com.example.loginfordigipack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_details.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONObject.*

class DetailsActivity : AppCompatActivity() {

    // Call the network detector tool
    private val networkMonitor = networkDetectorTool(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val googleId = intent.getStringExtra("google_id")
        val googleFirstName = intent.getStringExtra("google_first_name")
        val googleLastName = intent.getStringExtra("google_last_name")
        val googleEmail = intent.getStringExtra("google_email")
        val googleProfilePicURL = intent.getStringExtra("google_profile_pic_url")
        val googleAccessToken = intent.getStringExtra("google_id_token")

        google_first_name_textview.text = googleFirstName
        //google_email_textview.text = googleEmail

        // Calls the network detector class
        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                clouds.setImageResource(R.drawable.noclouds)
                                internet_connection.text = "Wifi Connection"
                                connectToServer()
                            }
                            ConnectionType.Cellular -> {
                                clouds.setImageResource(R.drawable.noclouds)
                                internet_connection.text = "Cellular Connection"
                                connectToServer()
                            }
                            else -> { }
                        }
                    }
                    false -> {
                        clouds.setImageResource(R.drawable.networkclouds)
                        internet_connection.text = "No Connection"
                        connectToServer()
                    }
                }
            }
        }

        // Automatically calls connection to the server
        connectToServer()
    }

    private fun connectToServer(){
        val googleId = intent.getStringExtra("google_id")
        val googleFirstName = intent.getStringExtra("google_first_name")
        val googleLastName = intent.getStringExtra("google_last_name")
        val googleEmail = intent.getStringExtra("google_email")
        val googleProfilePicURL = intent.getStringExtra("google_profile_pic_url")
        val googleAccessToken = intent.getStringExtra("google_id_token")

        val mptv = findViewById<TextView>(R.id.mptext)
        val authurl = "auth/"
        val getlisturl = "user/$googleEmail"
        val queue = RequestQueueSingleton.getInstance(this.applicationContext)
        val tok = JsauthTok(googleAccessToken, googleEmail)
        val gtok = Gson().toJson(tok)
        val jsobtok = JSONObject(gtok)
        var resp = JSONObject("{Result:noACK}")

        val request = JsonObjectRequest(Request.Method.POST, getString(R.string.serverUrl).plus(authurl), jsobtok,
                { response -> resp = response
                    if( resp.get("Result") == "ACK"){
                        val success = "Authentication Successful"
                        mptv.text = success

                        val user = Jsuser(googleFirstName, googleEmail, googleId)
                        val guser = Gson().toJson(user)
                        val jsuserobj = JSONObject(guser)
                        var filelistResp = JSONObject("{Result:noACK}")

                        val filelistRequest = JsonObjectRequest(Request.Method.GET, getString(R.string.serverUrl).plus(getlisturl), jsuserobj,
                                { flresponse -> filelistResp = flresponse
                                    try{
                                        Log.i(getString(R.string.app_name), "in details act, %s".format(flresponse.toString()))
                                        var flintent = Intent(this, FileListViewActivity::class.java)
                                        flintent.putExtra("fileListJson", flresponse.toString())
                                        flintent.putExtra("gsoData", intent.extras)
                                        val nextbtn = findViewById<Button>(R.id.btn_pick)
                                        nextbtn.setOnClickListener(){
                                            this.startActivity(flintent)
                                        }
                                    }catch(e: JSONException){
                                        Log.e(getString(R.string.app_name), "JSON key error: %s".format(e))
                                    }
                                },
                                { err -> Log.i(getString(R.string.app_name), err.toString())
                                    Toast.makeText(applicationContext, err.toString(), Toast.LENGTH_LONG).show()}
                        )
                        queue.addToRequestQueue(filelistRequest)
                    }
                },
                { error ->
                    mptv.text = error.toString() }
        )
        queue.addToRequestQueue(request)
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }
}

data class Jsuser(
        @SerializedName("userName")
        var userName: String? = null,
        @SerializedName("googleEmail")
        var email: String? = null,
        @SerializedName("googleId")
        var gid: String? = null
)

data class JsauthTok (
        @SerializedName("googleAccessToken")
        var authToken: String? = null,
        @SerializedName("gooogleEmail")
        var email: String? = null
)