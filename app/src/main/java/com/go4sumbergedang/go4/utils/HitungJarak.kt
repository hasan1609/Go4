package com.go4sumbergedang.go4.utils

object HitungJarak {

    //Rumus Jarak Haversine
    fun getDistance(currentLatitude: Double,
                    currentLongitude: Double,
                    destinationLatitude: Double,
                    destinationLongitude: Double): Double {
        val earthRadius = 3958.75
        val dLatitude = Math.toRadians(destinationLatitude - currentLatitude)
        val dLongitude = Math.toRadians(destinationLongitude - currentLongitude)
        val a = Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2) +
                Math.cos(Math.toRadians(currentLatitude)) *
                Math.cos(Math.toRadians(destinationLatitude)) *
                Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val dDistance = earthRadius * c
        val meterConversion = 1609
        val myDistance = dDistance * meterConversion
        return Math.floor(myDistance / 1000)
    }

}