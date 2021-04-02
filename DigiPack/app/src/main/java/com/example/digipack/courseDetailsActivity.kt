package com.example.digipack

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class courseDetailsActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_course_details)

        // Change title
        supportActionBar?.title = Html.fromHtml("<font color='#01345A'>Course Name</font>")

    }
}