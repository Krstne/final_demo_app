package DigiJson

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

class DigiClass {

    @Serializable
    data class CourseList(
        @SerializedName("Courses")
        var Courses: ArrayList<Course>?
    ) : java.io.Serializable

    @Serializable
    data class Course(
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("courseID")
        var courseID: String? = null,
        @SerializedName("announcements")
        var announcements: ArrayList<Announcement>?,
        @SerializedName("coursework")
        var coursework: ArrayList<CourseWork>?,
        var students : ArrayList<String>?
    ) : java.io.Serializable

    @Serializable
    data class Announcement(
        @SerializedName("announcementID")
        var announcementID: String? = null,
        @SerializedName("text")
        var text: String? = null,
        @SerializedName("materials")
        var materials: ArrayList<Material>?,
        @SerializedName("creationtime")
        var creationtime: String? = null,
        var assigneemode: String? = null,
        var assignedstudents: ArrayList<String>?
    ) : java.io.Serializable

    @Serializable
    data class CourseWork(
            @SerializedName("courseworkID")
        var courseworkID: String? = null,
            @SerializedName("title")
        var title: String? = null,
            @SerializedName("description")
        var description: String? = null,
            @SerializedName("materials")
        var materials: ArrayList<Material>?,
            @SerializedName("creationtime")
        var creationtime: String? = null,
            @SerializedName("duedate")
        var duedate: Duedate? = null,
            @SerializedName("duetime")
        var duetime: Duetime? = null,
            @SerializedName("worktype")
        var worktype: String? = null,
        //this one idk ill have to check my code
            @SerializedName("details")
        var details: multchoice? = null,
        var assigneemode: String? = null,
        var students: ArrayList<String>?,
        var maxPoints: Int? = null
    ) : java.io.Serializable

    @Serializable
    data class Material(
        //psure these shouldnt be strings...
        @SerializedName("drivefiles")
        var drivefiles: DigiDrive.DF?,
        @SerializedName("ytlinks")
        var ytlinks: String? = null,
        @SerializedName("links")
        var links: String? = null,
        @SerializedName("forms")
        var forms: String? = null,
        @SerializedName("localfiles")
        var localfiles: String? = null
        //according to my code? yeah they're strings lol
    ) : java.io.Serializable



    @Serializable
    data class Duedate(
        @SerializedName("year")
        var year : Int? = null,
        @SerializedName("month")
        var month: Int? = null,
        @SerializedName("day")
        var day: Int? = null
    ) : java.io.Serializable

    @Serializable
    data class Duetime(
        @SerializedName("hours")
        var hours: Int? = null,
        @SerializedName("minutes")
        var minutes: Int? = null
    ) : java.io.Serializable

    @Serializable
    data class multchoice(
            var choices: ArrayList<String>?
    ) : java.io.Serializable
}