package com.example.digipack

import DigiJson.DigiSearch.QueryList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class searchQueActivity : AppCompatActivity() {

    var userQuestion: String? = null
    var nameInput: EditText? = null
    var submitButton: Button? = null
    private lateinit var newQueryList : QueryList
    private var newquery : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val ui = intent.getBooleanExtra("ui", false)

        // Determines what UI to show to the user
        if(ui){
            setContentView(R.layout.activity_kid_search_que)
        }else{
            setContentView(R.layout.activity_search_que)
        }

        nameInput = findViewById<TextView>(R.id.nameInput) as EditText

        // Grab the textview to show the userQuestion on the specific area
        val userQues = findViewById<TextView>(R.id.user_question)

        // Find the submit button
        submitButton = findViewById<View>(R.id.submitButton) as Button

        submitButton!!.setOnClickListener {
            userQuestion = nameInput!!.text.toString()
            userQues.text = userQuestion
            newQuery(userQuestion!!)
            println("=========================")

        }
    }

    //when the user makes a new query add it to the list
    //if there are still queries that havent been submitted to the server yet
    //add the new query to that list
    private fun newQuery(query: String){
        //set flag to true
        newquery = true

        //if the query list is not initialized
        if( !this::newQueryList.isInitialized ){
            val cacheManager = CacheUtility()
            val qlstr = cacheManager.getStringFromCache(getString(R.string.newQueriesList), this)

            //if there are old queries that havent gotten responses form the server yet load those
            //android studio complains about this if because its a garbage IDE and hates readability
            if( qlstr != ""){
                newQueryList = Json.decodeFromString(qlstr)
            }else{
                //else create new query list
                newQueryList = QueryList(ArrayList<String>())
            }
        }
        //if the submitted query is not in the queries list
        if(!(query in newQueryList.queries)){
            //add the new query to the list
            newQueryList.queries.add(query)
        }
    }

    //when the activity is closed cache the query list if the user has submitted new queries
    override fun onStop(){
        super.onStop()

        if(newquery){
            val cacheManager = CacheUtility()
            //because we attempt to load old queries that dont have reposnes in newQuery(), we dont need to worry
            //about overwriting them here
            cacheManager.cacheString(Json.encodeToString(newQueryList), getString(R.string.newQueriesList),this)
        }
    }
}