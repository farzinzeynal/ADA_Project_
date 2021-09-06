package com.example.adaproject.helpers

class Constants {
    companion object{
        val baseUrl:String = "http://ec2-52-17-33-184.eu-west-1.compute.amazonaws.com:8080"
        val baseUrlAuth = baseUrl+"/auth/"
        val male:String = "Erkek"
        val feMale = "Kız"
        val CAPTURE = 0
        val UPDATE = 1
        var token: String= ""
    }
}