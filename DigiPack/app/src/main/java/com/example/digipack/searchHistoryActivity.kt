package com.example.digipack

import DigiJson.DigiSearch
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search_history.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

class searchHistoryActivity : AppCompatActivity(){



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ui = intent.getBooleanExtra("ui", false)

        // Determines what UI to show to the user
        if(ui){
            setContentView(R.layout.activity_kid_search_history)
        }else{
            setContentView(R.layout.activity_search_history)
        }

        // Change title
        supportActionBar?.title = Html.fromHtml("<font color='#01345A'>Search History</font>")

        //unpack expected intent variables
        var res : DigiSearch.DigiRes = intent.getSerializableExtra("resultslist") as DigiSearch.DigiRes
        populateHistory(res.resultslist)
    }

    private fun populateHistory(searchlist: ArrayList<DigiSearch.Results>?){
        //if the search list is null, let the user know that they dont have any results to show
        if( searchlist == null){
            viewtitle.text = getString(R.string.nosearchhistory)
        }else{
            viewtitle.text = "Search Results"
            //else populate the search history
            try{
                //create string array of queries
                var queries = ArrayList<String>()
                for( i in searchlist ){
                    i.query?.let { queries.add(it) }
                    print(i.results)
                }

                //here we create the list of queries and their results below them.
                var searchResult = ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1, queries)
                searchHistoryList.adapter = searchResult

                searchHistoryList.onItemClickListener = AdapterView.OnItemClickListener{parent, view, position, id ->
                    var names = searchlist[position]
                    println("=====================================")


                }


            }catch(e: IOException) {
                //handle errors eventually
                Log.e(getString(R.string.app_name), "searchHistoryActivity populateHistory error: %s".format(e.toString()))
            }
        }
    }

    private fun removeQuery(position: Int, searchlist: ArrayList<DigiSearch.Results>?){
        if(searchlist == null){
            Toast.makeText(this, "Nothing to Remove", Toast.LENGTH_SHORT).show()
        }else{
            //remove from local array
            val removed = searchlist.removeAt(position)
            Toast.makeText(this, "Removed ${removed.query} results", Toast.LENGTH_SHORT).show()
            //cache updated array
            val tocache = Json.encodeToString(DigiSearch.DigiRes(searchlist))
            CacheUtility().cacheString(tocache, getString(R.string.queryResultsList), this)
        }
    }

}
