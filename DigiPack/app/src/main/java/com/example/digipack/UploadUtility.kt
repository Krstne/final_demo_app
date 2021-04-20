package com.example.digipack


import android.app.Activity
import android.app.ProgressDialog
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import android.content.ContentResolver





/**
 * Helper utility for uploading files to the server. Function of note is uploadFile. The remaining
 * functions are helpers called by uploadFile.
 */
class UploadUtility(activity: Activity) {

    var activity = activity;
    var dialog: ProgressDialog? = null
    var serverURL: String = "https://digipackweb.com:8000/file/upload"
    var serverSubmitUrl: String = "https://digipackweb.com:8000/file/upload"
    var serverUploadDirectoryPath: String = "https://digipackweb.com:8000/uploads/"
    var serverSubmitDirectoryPath: String = "https://digipackweb.com:8000/submit/"
    val client = OkHttpClient()

    /**
     * Function Name: uploadFile
     * Description: Takes in a file as a path string, path uri, or File, a file name, and the user's
     *              identifying gmail address. Builds a multipart form request and posts it to the
     *              server.
     *
     *     Post Request:  uploaded_file - File object to be uploaded to server
     *     '''''''''''''  file_name - name of the file to be uploaded to server
     *                    gmail - gmail address from the client; used for if <-- must be made to ID token later.
     */

    fun uploadFile(sourceFilePath: String, uploadedFileName: String? = null, idTok: String) {
        uploadFile(File(sourceFilePath), uploadedFileName, idTok)
    }

    fun uploadFile(sourceFileUri: Uri, uploadedFileName: String? = null, idTok: String) {
        val pathFromUri = URIPathHelper().getPath(activity, sourceFileUri)
        uploadFile(File(pathFromUri), uploadedFileName, idTok)
    }

    fun uploadFile(sourceFile: File, uploadedFileName: String? = null, idTok: String) {
        Thread {

            //use the specified name; if none specified, use the file's name.
            val fileName: String = if (uploadedFileName == null)  sourceFile.name else uploadedFileName


            //get the file type
            val mimeType = sourceFile.getMimeType();

            //if failed to get file type
            if (mimeType == null) {
                Log.e("file error", "Not able to get mime type")
                return@Thread  //log error and give up on upload
            }

            //display progress dialog
            toggleProgressDialog(true)   //true toggles dialog on

            //try block surrounds network actions
            try {
                //build the request
                val requestBody: RequestBody =
                        MultipartBody.Builder().setType(MultipartBody.FORM)
                                //file to be uploaded
                                .addFormDataPart("uploaded_file", fileName, sourceFile.asRequestBody(mimeType.toMediaTypeOrNull()))
                                .addFormDataPart("file_name", fileName) // fileName
                                .addFormDataPart("idTok", idTok)  //gmail for ID
                                .build()

                val request: Request = Request.Builder().url(serverURL).post(requestBody).build()
                showToast(request.toString())

                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d("File upload", "success, path: $serverUploadDirectoryPath$fileName")
                    showToast("File uploaded successfully at $serverUploadDirectoryPath$fileName")
                } else {
                    Log.e("File upload", "failed")
                    showToast("File uploading failed")
                }
            }
            //in case that upload failed, print file upload failed log, toast failure,
            catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("File upload", "failed")
                showToast("File uploading failed")
            }
            toggleProgressDialog(false) // false toggles dialog off
        }.start()  // launches upload thread
    }




    fun submitFile(sourceFilePath: String, uploadedFileName: String? = null, idTok: String, courseId: String, courseworkId: String) {
        submitFile(File(sourceFilePath), uploadedFileName, idTok, courseId, courseworkId)
    }

    fun submitFile(sourceFileUri: Uri, uploadedFileName: String? = null, idTok: String, courseId: String, courseworkId: String) {
        val pathFromUri = URIPathHelper().getPath(activity, sourceFileUri)
        submitFile(File(pathFromUri), uploadedFileName, idTok, courseId, courseworkId)
    }

    fun submitFile(sourceFile: File, uploadedFileName: String? = null, idTok: String, courseId: String, courseworkId: String) {
        Thread {

            //use the specified name; if none specified, use the file's name.
            val fileName: String = if (uploadedFileName == null)  sourceFile.name else uploadedFileName


            //get the file type
            val mimeType = sourceFile.getMimeType();

            //if failed to get file type
            if (mimeType == null) {
                Log.e("file error", "Not able to get mime type")
                return@Thread  //log error and give up on upload
            }

            //display progress dialog
            toggleProgressDialog(true)   //true toggles dialog on

            //try block surrounds network actions
            try {
                //build the request
                val requestBody: RequestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        //file to be uploaded
                        .addFormDataPart("uploaded_file", fileName, sourceFile.asRequestBody(mimeType.toMediaTypeOrNull()))
                        .addFormDataPart("file_name", fileName) // fileName
                        .addFormDataPart("idTok", idTok)  //idTok for auth
                        .addFormDataPart("courseId", courseId)
                        .addFormDataPart("courseworkId", courseworkId)
                        .build()

                val request: Request = Request.Builder().url(serverSubmitUrl).post(requestBody).build()
                showToast(request.toString())

                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d("File upload", "success, path: $serverSubmitDirectoryPath$fileName")
                    showToast("File uploaded successfully at $serverSubmitDirectoryPath$fileName")
                } else {
                    Log.e("File upload", "failed")
                    showToast("File uploading failed")
                }
            }
            //in case that upload failed, print file upload failed log, toast failure,
            catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("File upload", "failed")
                showToast("File uploading failed")
            }
            toggleProgressDialog(false) // false toggles dialog off
        }.start()  // launches upload thread
    }



    fun File.getMimeType(fallback: String = "image/*"): String {
        return MimeTypeMap.getFileExtensionFromUrl(toString())
                ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(toLowerCase()) }
                ?: fallback // You might set it to */*
    }























    /**
     * Dum stuf below this line
     */
    //show toast helper function takes in a string then displays a long toast with said string
    //(dum)
    fun showToast(message: String) {
        activity.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }


    //takes in a boolean
    //if true, display a progress dialog that says the file is uploading
    //false toggles display off
    //(dum)
    fun toggleProgressDialog(show: Boolean) {
        activity.runOnUiThread {
            if (show) {
                dialog = ProgressDialog.show(activity, "", "Uploading file...", true);
            } else {
                dialog?.dismiss();
            }
        }
    }

}