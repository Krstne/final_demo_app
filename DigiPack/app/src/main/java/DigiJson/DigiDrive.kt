package DigiJson

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

class DigiDrive {

    @Serializable
    data class DF(
        @SerializedName("Files")
        var Files: ArrayList<DigiFile>?
    ) : java.io.Serializable

    @Serializable
    data class Drive(
        @SerializedName("driveid")
        var driveid: String? = null,
        @SerializedName("files")
        var files: ArrayList<DriveFile>,
        @SerializedName("permissions")
        var permissions: String? = null
    ) : java.io.Serializable

    @Serializable
    data class DriveFile(
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("driveID")
        var driveID: String? = null,
        @SerializedName("classID")
        var classID: String? = null,
        @SerializedName("localpath")
        var localpath: String? = null,
        @SerializedName("drivepath")
        var drivepath: String? = null,
        @SerializedName("classroompath")
        var classroompath: String? = null,
        @SerializedName("filesize")
        var filesize: String? = null
    ) : java.io.Serializable

    @Serializable
    data class DigiFile(
        @SerializedName("fileName")
        var fileName: String? = null,
        @SerializedName("fileid")
        var fileid: String? = null
    ) : java.io.Serializable
}