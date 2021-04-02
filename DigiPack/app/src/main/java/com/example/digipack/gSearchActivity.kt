package com.example.digipack

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class gSearchActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gsearch)

        // Change title color
        supportActionBar?.title = Html.fromHtml("<font color='#01345A'>Search</font>")

        // Get the action button
        val actionBr = supportActionBar
        if(actionBr != null){
            actionBr.setDisplayHomeAsUpEnabled(true)
        }
    }

    // When the back button func
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(this, DetailsActivity::class.java)
        this.startActivity(myIntent)
        return super.onOptionsItemSelected(item)
    }
}