package com.example.digipack


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.text.Html
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.activity_file_list_view.*
import kotlinx.android.synthetic.main.activity_file_list_view.clouds
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import DigiJson.DigiDrive
import DigiJson.DigiUser
import DigiJson.GUserJson.GUser
import android.graphics.Color
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


const val PICK_PDF_FILE = 2
const val OPEN_PDF_FILE = 3

class FileListViewActivity : AppCompatActivity() {

    var url : String = ""
    lateinit var email : String

    // Call the network detector tool
    private val networkMonitor = networkDetectorTool(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ui = intent.getBooleanExtra("ui", false)

        if(ui){
            setContentView(R.layout.activity_kid_file_page)
        }else{
            setContentView(R.layout.activity_file_list_view)
        }


        // Change title
        supportActionBar?.title = Html.fromHtml("<font color='#01345A'>Files</font>")

        var queue = RequestQueueSingleton.getInstance(this.applicationContext)
        var context: Context = this

        var guser = intent.getSerializableExtra("guser") as GUser
        var df = intent.getSerializableExtra("filelist") as DigiDrive.DF
        var filelist = df.Files

        if (filelist != null) {
            write_to_ui_and_listen(guser, filelist, queue)
        }

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
                val dir = File(Environment.getExternalStorageDirectory().toString() + "/Download/DigiPackDocuments/")
                dir.mkdirs()

                builder.appendEncodedPath( (Uri.fromFile( dir ) ).toString()
                )
                val uri = builder.build()
                val file = DocumentFile.fromSingleUri(context, uri)
                println("openFileButton says uri is : " + uri )

                // Specify openable pdfs for the intent)
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, dir)
            }
            //once they pick a file, call onActivityResult with PICK_PDF_FILE code
            startActivityForResult(intent, PICK_PDF_FILE)
        }


        val uploadFileButton = findViewById<Button>(R.id.uploadFileButton)
        uploadFileButton.setOnClickListener {
            // Construct intent that allows user to pick a file
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                //here I attempt to build a starting URI so the file viewer will open in a specific directory
                //but the uri appears to be coming out malformed idk
                val builder = Uri.Builder()
                val dir = File(Environment.getExternalStorageDirectory().toString() + "/Download/DigiPackDocuments/")
                dir.mkdirs()

                builder.appendEncodedPath( (Uri.fromFile( dir ) ).toString()
                )
                val uri = builder.build()
                val file = DocumentFile.fromSingleUri(context, uri)
                println("openFileButton says uri is : " + uri )

                // Specify openable pdfs for the intent)
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, dir)
            }
            //once they pick a file, call onActivityResult with PICK_PDF_FILE code
            startActivityForResult(intent, OPEN_PDF_FILE)
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
                            else -> {
                            }
                        }
                    }
                    false -> {
                        clouds.setImageResource(R.drawable.networkclouds)
                    }
                }
            }
        }
    }


    // Checks if the requestCode is the same, if so then continue the sign in process
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
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

        if(requestCode == OPEN_PDF_FILE)
        {
            var uri: Uri? = null

            if( resultData != null){
                uri = resultData.data

                //get idTok
                val guser = intent.getSerializableExtra("guser") as GUser
                val idTok = guser.idToken

                //pass everything to the upload utility
                val uu = UploadUtility(this)
                uu.uploadFile(uri!!, null, idTok!!)
            }
        }
    }

    fun write_to_ui_and_listen(guser: GUser, files: ArrayList<DigiDrive.DigiFile>, queue: RequestQueueSingleton)
    {
        try{
            var filenamelist = ArrayList<String>()
            for(i in files){
                when{
                    i.fileName == null ->{
                        filenamelist.add("<no file name found>")
                    }
                    else -> {
                        i.fileName?.let { filenamelist.add(it) }
                    }
                }

            }
            var adapterView = ArrayAdapter(this, android.R.layout.simple_list_item_1, filenamelist)

            json_info.adapter = adapterView


            // Creates an onclick listener when the user clicks on the driveID that would be referenced to driveID
            json_info.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id->
                //position is the index of the list item that corresponds to the button clicked
                //Toast.makeText(applicationContext, "Type Selected is" + files[position], Toast.LENGTH_LONG).show()
                url = getString(R.string.serverUrl).plus("download/${files[position].fileName}")
                //url should not be global in prod
                //should be created dynamically for the task at hand
                getfile(guser, queue, files[position].fileid)
                FileDownloader().getFile(this, url)
            }
        }catch (e: IOException){
            //handle errors eventually
            Log.e(getString(R.string.app_name), "FileListViewActivity write_to_ui error: %s".format(e.toString()))
        }
    }

    fun getfile(guser: GUser, queue: RequestQueueSingleton, fileid: String?): Boolean {


        val reqMethodCode = Request.Method.GET
        val getFileUrl = getString(R.string.serverUrl).plus("sd/${guser.idToken}/${fileid}")
        val juser = DigiUser.Jsuser(guser.firstName, guser.email, guser.userID)
        val request = JSONObject( Json.encodeToString(juser) )

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