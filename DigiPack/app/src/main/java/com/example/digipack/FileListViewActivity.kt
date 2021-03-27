package com.example.digipack

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_file_list_view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_file_list_view.*
import kotlinx.android.synthetic.main.activity_main.*

import java.io.File

import java.io.InputStream
import android.provider.DocumentsContract
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.activity_file_list_view.clouds


const val PICK_PDF_FILE = 2

class FileListViewActivity : AppCompatActivity() {

    var url : String = ""
    lateinit var email : String
    // Call the network detector tool
    private val networkMonitor = networkDetectorTool(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list_view)

        var filenames = ArrayList<String>()
        var fileids = ArrayList<String>()
        var queue = RequestQueueSingleton.getInstance(this.applicationContext)
        var context: Context = this

        var googleFirstName = intent.getBundleExtra("gsoData")?.getString("google_first_name")
        var googleId = intent.getBundleExtra("gsoData")?.getString("google_id")
        email = intent.getBundleExtra("gsoData")?.getString("google_email").toString()

        read_json(filenames, fileids)
        write_to_ui_and_listen(filenames, fileids, queue)

        //on refresh:
        //refreshList(queue, email, googleFirstName, googleId)
        //read_json(filenames, fileids)

        // Looking for the file button connection
        val openFileButton = findViewById<Button>(R.id.openFileButton)

        // Set file button onclicklistener
        openFileButton.setOnClickListener {
            // Construct intent that allows user to pick a file
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                //here I attempt to build a starting URI so the file viewer will open in a specific directory
                //but the uri appears to be coming out malformed idk
                val builder = Uri.Builder()
                builder.appendEncodedPath(
                    (Uri.fromFile(
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                        )
                    )).toString()
                )
                val uri = builder.build()
                val file = DocumentFile.fromSingleUri(context, uri)
                println(uri)

                // Specify openable pdfs for the intent
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, file?.uri)
            }
            //once they pick a file, call onActivityResult with PICK_PDF_FILE code
            startActivityForResult(intent, PICK_PDF_FILE)
        }

        // Calls the network detector class
        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                clouds.setImageResource(R.drawable.sun_connection)
                            }
                            ConnectionType.Cellular -> {
                                clouds.setImageResource(R.drawable.sun_connection)
                            }
                            else -> { }
                        }
                    }
                    false -> {
                        clouds.setImageResource(R.drawable.networkclouds)
                    }
                }
            }
        }

        // Checks if the requestCode is the same, if so then continue the sign in process
        fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
            super.onActivityResult(requestCode, resultCode, resultData)

            //after the user picks a pdf file
            if (requestCode == PICK_PDF_FILE){
                var uri: Uri? = null

                //if they successfully picked a file
                if (resultData != null) {
                    //call an intent to open the file up; asks user to select an application with which to view pdf
                    uri = resultData.data
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "application/pdf")
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(intent)
                }
            }
        }
    }

    // Read the json file and the display it on the activity layout
    fun read_json(filenames: ArrayList<String>, fileids: ArrayList<String>){

        try {
            //goat.jpeg
            //lion.png
            //Caitlin Abuel Resume.pdf

            // Read the text file

            var json : String? = intent.getStringExtra("fileListJson")
            var gsodata = intent.getBundleExtra("gsoData")
            //email = gsodata?.getString("google_email")

            // Prints the Json File
            //json_info.text = json
            var jsonobj = JSONObject(json)
            Log.i(getString(R.string.app_name), "filelistview jsonobj: %s".format(jsonobj.toString()))

            // Creates an JSON array which will contain data from our Json file
            var jsonArray = JSONArray(jsonobj.getString("Files"))
            Log.i(getString(R.string.app_name), "filelistview jsonArray: %s".format(jsonArray.toString()))
            //Log.i(getString(R.string.app_name), "Json Array: %s".format(internalJsonobj.toString()))

            //var jsonArray = JSONArray(internalJsonobj)
            Log.i(getString(R.string.app_name), "just before the fore loop")
            // Loop through the json array

            for (i in 0..(jsonArray.length() - 1)){
                // Create a json object to access each value in the file
                var jsonObj =  jsonArray.getJSONObject(i)

                // Array to store the jsonObject
                filenames.add(jsonObj.getString("fileName"))
                fileids.add(jsonObj.getString("fileid"))
            }
        }catch (e : IOException){
            //handle errors eventually
            Log.e(getString(R.string.app_name), "error: %s".format(e.toString()))
        }

    }

    fun write_to_ui_and_listen(fileNames: ArrayList<String>, fileids: ArrayList<String>, queue: RequestQueueSingleton)
    {
        try{
            var adapterView = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileNames)

            json_info.adapter = adapterView

            // Creates an onclick listener when the user clicks on the driveID that would be referenced to driveID
            json_info.onItemClickListener = AdapterView.OnItemClickListener{
                    parent, view, position, id->
                //position is the index of the list item that corresponds to the button clicked
                Toast.makeText(applicationContext, "Type Selected is" + fileNames[position],Toast.LENGTH_LONG).show()
                url = getString(R.string.serverUrl).plus("download/${fileNames[position]}")
                //url should not be global in prod
                //should be created dynamically for the task at hand
                getfile(queue, email, "mynamejef", fileids[position], "alsoyef")
                FileDownloader().getFile(this, url)
            }
        }catch(e: IOException){
            //handle errors eventually
            Log.e(getString(R.string.app_name), "FileListViewActivity write_to_ui error: %s".format(e.toString()))
        }
    }

    fun getfile(queue: RequestQueueSingleton, googleEmail: String?, googleFirstName: String?, fileid: String?, googleId: String?): Boolean {
        val reqMethodCode = Request.Method.GET
        val getFileUrl = getString(R.string.serverUrl).plus("sd/${googleEmail}/${fileid}")
        val request = JSONObject(Gson().toJson(Jsuser(googleFirstName, googleEmail, googleId)))

        var flag : Boolean = false

        val req = JsonObjectRequest(reqMethodCode, getFileUrl, request,
            { resp ->
                //do something response
                flag = true
            },
            { err ->
                //so something err
                flag = false
            }
        )
        queue.addToRequestQueue(req)
        return flag
    }

    // Refresh the page
    fun refreshList(queue: RequestQueueSingleton, googleEmail: String?, googleFirstName: String?, googleId: String? ){
        val reqMethodCode = Request.Method.GET
        val getFileUrl = getString(R.string.serverUrl).plus("user/").plus(googleEmail)

        val request = JSONObject(Gson().toJson(Jsuser(googleFirstName, googleEmail, googleId)))
        val req = JsonObjectRequest(reqMethodCode, getFileUrl, request,
            { resp -> intent.removeExtra("fileListJson")
                intent.putExtra("fileListJson", resp.toString())
                //do something with a positive response
            },
            { err -> println("fdas")
                Log.e(getString(R.string.app_name), "FileListViewActivity refeshList error: %s".format(err.toString()))
                //do something with an error
            }
        )
        queue.addToRequestQueue(req)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        FileDownloader().onRequestPermissionsResult(requestCode, permissions, grantResults)
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
