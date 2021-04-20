package DigiJson

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

class DigiServer {
    @Serializable
    data class succ(
        @SerializedName("success")
        var success: Int? = null
    ) : java.io.Serializable
}