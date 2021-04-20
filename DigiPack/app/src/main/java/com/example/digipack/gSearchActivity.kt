package com.example.digipack

import DigiJson.DigiSearch
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class gSearchActivity : AppCompatActivity(){

    private lateinit var theSearchPage : Intent
    private lateinit var theSearchHistoryPage : Intent



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ui = intent.getBooleanExtra("ui", false)

        // Determines what UI to show to the user
        if(ui){
            setContentView(R.layout.activity_kid_gsearch)
        }else{
            setContentView(R.layout.activity_gsearch)
        }


        // Create intent
        theSearchPage = Intent(this, searchQueActivity::class.java)
        theSearchHistoryPage = Intent(this, searchHistoryActivity::class.java)
        val searchlist = intent.getSerializableExtra("resultslist") as DigiSearch.DigiRes
        theSearchHistoryPage.putExtra("resultslist", searchlist)

        theSearchHistoryPage.putExtra("ui", ui)
        theSearchPage.putExtra("ui", ui)

        // Change title
        supportActionBar?.title = Html.fromHtml("<font color='#01345A'>Search</font>")


        var searchPage = findViewById<Button>(R.id.doSearch)
        searchPage?.setOnClickListener{
            when{
                this::theSearchPage.isInitialized ->startActivity(theSearchPage)

                else->{
                    Toast.makeText(this, "Google Search Not HERE", Toast.LENGTH_SHORT).show()
                }
            }
        }


        var searchHSTPage = findViewById<Button>(R.id.searchHistory)
        searchHSTPage?.setOnClickListener{
            when{
                this::theSearchHistoryPage.isInitialized ->startActivity(theSearchHistoryPage)

                else->{
                    Toast.makeText(this, "Google Search Not HERE", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}