package com.example.digipack

import DigiJson.GUserJson
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.documentfile.provider.DocumentFile
import kotlinx.android.synthetic.main.activity_popup.*
import java.io.File

private val SUBMIT_FILE = 4

class popUp : AppCompatActivity(){
    private var popupTitle = ""
    private var popupText = ""
    private var popupdueDate = ""
    private var popupButton = ""
    private var darkStatusBar = false
    private var courseworkId = ""
    private var courseId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0,0)
        setContentView(R.layout.activity_popup)
        var context: Context = this

        // Get the data
        val bundle = intent.extras
        popupTitle = bundle?.getString("popuptitle", "Title") ?: ""
        popupText = bundle?.getString("popuptext", "Text") ?: ""
        popupdueDate = bundle?.getString("popupduedate", "Text") ?: ""
        popupButton = bundle?.getString("popupbtn", "Button") ?: ""
        darkStatusBar = bundle?.getBoolean("darkstatusbar", false) ?: false
        courseworkId = bundle?.getString("courseworkId", courseworkId) ?: ""
        courseId = bundle?.getString("courseId", courseId) ?: ""

        // Set the data
        popup_window_title.text = popupTitle
        popup_window_text.text = popupText
        due_date.text = popupdueDate
        popup_window_button.text = popupButton


        // Set the Status bar appearance for different API levels
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // If you want dark status bar, set darkStatusBar to true
                if (darkStatusBar) {
                    this.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                this.window.statusBarColor = Color.TRANSPARENT
                setWindowFlag(this, false)
            }
        }


        // Fade animation for the background of Popup Window
        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#3E8BBE"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()


        // Fade animation for the Popup Window
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()


        // Close the Popup Window when you press the button
        popup_window_button.setOnClickListener {
            onBackPressed()
        }

        //set up the submit button
        var submitButton = findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener {
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
            startActivityForResult(intent, SUBMIT_FILE)
        }

    }

    private fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
    }

    override fun onBackPressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        popup_window_view_with_border.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)


        if(requestCode == SUBMIT_FILE)
        {
            var uri: Uri? = null

            if( resultData != null){
                uri = resultData.data

                //get idTok
                val guser = intent.getSerializableExtra("guser") as GUserJson.GUser
                val idTok = guser.idToken

                //pass everything to the upload utility
                val uu = UploadUtility(this)
                uu.submitFile(uri!!, null, idTok!!, courseId, courseworkId)
            }
        }
    }
}