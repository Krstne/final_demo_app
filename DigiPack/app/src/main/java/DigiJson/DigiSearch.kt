package DigiJson

import kotlinx.serialization.Serializable

class DigiSearch {
    @Serializable
    data class DigiRes(
            var resultslist : ArrayList<Results>?
    ) : java.io.Serializable

    @Serializable
    data class Results(
            var query : String? = null,
            var imagebool : Boolean? = null,
            var numresults : Int? = null,
            var results : ArrayList<SearchResult>?
    ) : java.io.Serializable

    @Serializable
    data class SearchResult(
            var title : String? = null,
            var link : String? = null,
            var displaylink : String? = null,
            var snippet : String? = null,
            var mimetype : String? = null,
            var fileformat : String? = null
    ) : java.io.Serializable

    @Serializable
    data class QueryList(
            var queries: ArrayList<String>
    )
}