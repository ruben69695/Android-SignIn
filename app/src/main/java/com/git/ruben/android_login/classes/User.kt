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
 * Constructor de la clase UserK
 * @param name Nombre de usuario
 * *
 * @param pass Contraseña del usuario
 * *
 * @param rol Rol del usuario, admin o standard
 * *
 * @param status 0 en el caso de desactivado, 1 en el caso de activado
 */
class User (var name : String, var email : String, var created_at : Date, var updated_at : Date) {

    // Secondary constructor
    constructor() : this("", "", Date(), Date())

    //region FUNCTIONS AND METHODS

    @Throws(JSONException::class)
    fun convertJson_toUser(json : JSONObject) {

        var format : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        // A partir del JSON rellenamos las propiedades del objeto Kotlin
        this.name = json.getString("name")
        this.email = json.getString("email")
        this.created_at = format.parse(json.getString("created_at"))
        this.updated_at = format.parse(json.getString("updated_at"))

    }
}