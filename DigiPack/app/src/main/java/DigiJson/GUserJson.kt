package DigiJson

import kotlinx.serialization.Serializable

class GUserJson {

    @Serializable
    data class GUser(
        var userID: String? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        var email: String? = null,
        var authCode: String? = null,
        var idToken: String? = null
    ) : java.io.Serializable
}