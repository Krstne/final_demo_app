package com.example.digipack

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


const val REQUEST_CODE = 1;

/**
 * Class of utility functions which aid in storing and retrieving data from external app storage
 * for the purpose of caching app data.
 */
class CacheUtility {




    /**
     * Function Name: cacheString
     * Algorithm: Takes a given string of data to be cached and a given filename under which to store said string.
     * Stores the file in external app storage in the DigiPack directory. Also retrieves write permissions
     * from user if necessary.
     *
     * Arguments:
     *     fileData - String data to be written to file
     *     fileName - String name of file to be written
     *     context - activity context
     *
     */
    fun cacheString( fileData: String, fileName: String, context: Context)
    {
        if( askForPermissions( context ) )
        {
            //write file list response to external app storage
            //get path to external directory
            val path = context.getExternalFilesDir(null)

            //create digipack directory in case does not exist
            val digiPackDirectory = File(path, "DigiPack")
            digiPackDirectory.mkdir()

            print("cacheString says directory is " + digiPackDirectory.toString())
            //create file, write content to file
            val file = File(digiPackDirectory, fileName)


            FileOutputStream(file).use {
                it.write(fileData.toByteArray())

            }
        }
    }

    /**
     * Function Name: getStringFromCache
     * Algorithm: Function takes in a fileName and attempts to retrieve the file with the given name from
     * external app storage in the DigiPack directory. Returns the contents of the file as a string.
     * Also gets read permission if necessary
     *
     * Arguments:
     *     fileName - name of file to be accessed. Currently, invalid file names are not handled.
     *     context - application context
     *
     * Returns:
     *      String containing the contents of the specified file.
     */
    fun getStringFromCache( fileName: String, context: Context): String
    {
        if(askForPermissions( context ) ) {
            //access digipack directory
            val digiPackDirectory = File(context.getExternalFilesDir(null), "DigiPack")
            digiPackDirectory.mkdir()  //make directory if it doesn't exist
            var file : File

            if( File(digiPackDirectory, fileName).exists() ){
                //load file with specified name
                file = File(digiPackDirectory, fileName)
            }else{
                //file does not exist
                //create file
                try{
                    val create = File(digiPackDirectory, fileName).createNewFile()
                    file = File(digiPackDirectory, fileName)
                }catch (e: FileNotFoundException){
                    Log.e(context.getString(R.string.app_name), e.toString())
                    return ""
                }

            }
            return file.readText()
        }
        return ""
    }

    /**
     * Permissions management beneath this point
     * Don't worry about it
     */

    fun askForPermissions(context: Context): Boolean {
        println("ASK FOR PERMISSIONS ENTERED")
        if (!isPermissionsAllowed( context )) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //showPermissionDeniedDialog( context )
            } else {
                ActivityCompat.requestPermissions(context as Activity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_CODE)
            }
            return false
        }
        return true
    }


    fun isPermissionsAllowed(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

/*
    private fun showPermissionDeniedDialog( context: Context ) {
        AlertDialog.Builder(context)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", context.getPackageName(), null)
                    intent.data = uri
                    startActivity( applicationContext.intent )
                })
            .setNegativeButton("Cancel", null)
            .show()
    }
    */

}