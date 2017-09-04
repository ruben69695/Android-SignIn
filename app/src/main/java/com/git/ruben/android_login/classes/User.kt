package com.git.ruben.android_login.classes

import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ruben on 4/06/17.
 */

/**
 * Constructor of the User class
 * @param name Username
 * @param email : User email
 * @param created_at : Creation date
 * @param updated_at : Update date
 * *
 */
class User (var name : String, var email : String, var created_at : Date, var updated_at : Date) {

    // Secondary constructor
    constructor() : this("", "", Date(), Date())

    @Throws(JSONException::class)
    fun convertJson_toUser(json : JSONObject) {

        var format : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        // Thanks to the JSON we fill the class properties
        this.name = json.getString("name")
        this.email = json.getString("email")
        this.created_at = format.parse(json.getString("created_at"))
        this.updated_at = format.parse(json.getString("updated_at"))

    }
}