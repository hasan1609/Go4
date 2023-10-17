package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResultLokasiModel : Serializable {

    @SerializedName("geometry")
    val modelGeometry: ModelGeometry? = null

    @SerializedName("name")
    val name: String? = null

    @SerializedName("vicinity")
    val vicinity: String? = null

    @SerializedName("place_id")
    val placeId: String? = null
}

class ModelGeometry {

    @SerializedName("location")
    val modelLocation: ModelLocation? = null

}

class ModelLocation {

    @SerializedName("lat")
    val lat: Double? = null

    @SerializedName("lng")
    val lng: Double? = null

}