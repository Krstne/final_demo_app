package com.example.digipack

import DigiJson.GUserJson
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val guser = intent.getSerializableExtra("guser") as GUserJson.GUser
        val idTok = guser.idToken

        val cu = CacheUtility()

        if( cu.askForPermissions(this) )
        {
            Toast.makeText(this, "askForPermissions if entered.", Toast.LENGTH_SHORT).show()
            //write file list response to external app storage
            //get path to external directory
            val path = getExternalFilesDir(null)

            //create digipack directory in case does not exist
            val digiPackDirectory = File(path, "DigiPack")
            digiPackDirectory.mkdir()

            //create file, write content to file
            val file = File(digiPackDirectory, "hello.txt")


            FileOutputStream(file).use {
                it.write("Hello, World!".toByteArray())
            }
            val uu = UploadUtility(this)
            uu.uploadFile(file, "hello.txt", idTok?:"null")
        }
    }
}