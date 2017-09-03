package com.git.ruben.android_login

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.git.ruben.android_login.classes.User
import com.git.ruben.android_login.resources.AppConfig
import com.git.ruben.android_login.resources.Functions

class registerActivity : AppCompatActivity() {

    private var name : EditText? = null
    private var email : EditText? = null
    private var password : EditText? = null
    private var queue : RequestQueue? = null
    private var pDialog : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        // CASTING DE VIEWS A KOTLIN OBJECTS
        name = findViewById(R.id.register_name) as EditText
        email = findViewById(R.id.register_email) as EditText
        password = findViewById(R.id.register_password) as EditText

        var btnRegister : Button = findViewById(R.id.btRegistrarse) as Button
        var btnLinkToLogin : Button = findViewById(R.id.btnLinkToLogIn) as Button

        // We create the queue for the HTTP Requests of the Volley Library
        queue = Volley.newRequestQueue(this)

        // Progress Dialog
        pDialog = ProgressDialog(this)
        pDialog?.setCancelable(false)

        // Add the button register event when its clicked
        btnRegister.setOnClickListener {

            // Hide the virtual keyboard
            Functions.hideKeyboard(this.currentFocus, this)

            // Collect the user data
            val n : String = name?.text.toString()
            val e : String = email?.text.toString()
            val p : String = password?.text.toString()

            if(!n.isEmpty() && !e.isEmpty() && !p.isEmpty())
            {
                try {

                    // Try to register
                    attemptToRegister(n, e, p)
                }
                catch (excp : Exception)
                {
                    excp.printStackTrace()
                }
            }
            else
            {
                lanzarSnack(R.id.drawer_layout_register, getString(R.string.errorFaltanDatos), Snackbar.LENGTH_LONG)
            }

        }

        btnLinkToLogin.setOnClickListener {
            var intent : Intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Method to register the user
     */
    private fun attemptToRegister(name : String, email : String, password : String)
    {
        // Tag to cancel the request
        val tag_string_req : String = "req_login"

        // Show the dialog
        pDialog?.setMessage(getString(R.string.mensajeCargaRegistro))
        pDialog?.show()

        // Create the Http request
        val myReq = object : StringRequest(Method.POST,
                AppConfig.URL_REGISTER,
                requestSuccess(),
                requestError()) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                var params = HashMap<String, String>()
                params.put("name", name)
                params.put("email", email)
                params.put("password", password)
                return params
            }
        }

        // Important to add the request to the queue to proceed to execute te request
        queue?.add(myReq)
    }

    /**
     * Lanzar mensaje en forma de SnackBar
     * @param layoutID : identificador del DrawerLayout
     * *
     * @param message : mensaje que mostrar
     * *
     * @param duration : duracion del SnackBar
     */
    private fun lanzarSnack(layoutID: Int, message: String, duration: Int) {
        Snackbar.make(findViewById(layoutID)!!, message, duration).show()
    }

    private fun requestSuccess() : Response.Listener<String>?
    {
        return Response.Listener { response ->

            try
            {
                val jObj : JSONObject = JSONObject(response)
                val error = jObj.getBoolean("error")

                // Check for an error in json
                if(!error)
                {
                    // User succesfully register

                    // Create an Kotlin User Object with the json data returned
                    val userObj = User()
                    userObj.convertJson_toUser(jObj.getJSONObject("user"))

                    // Show message and clean user inputs
                    lanzarSnack(R.id.drawer_layout_register, getString(R.string.RegistroOK), Snackbar.LENGTH_LONG)
                    limpiarFormulario()
                }
                else
                {
                    // Show message error
                    var errorDescription = getDescriptionError(jObj.getString("error_msg"))
                    lanzarSnack(R.id.drawer_layout_register, errorDescription, Snackbar.LENGTH_LONG)
                }
            }
            catch(excp : Exception)
            {
                excp.printStackTrace()
            }
            finally {
                // Remove the progress dialog
                pDialog?.hide()
            }
        }
    }

    private fun getDescriptionError(codError : String): String
    {
        var description : String = ""

        when(codError) {
            "UnknownErrorRegistration" -> description = getString(R.string.UnknownErrorRegistration)
            "ErrorPostParamsMissing" -> description = getString(R.string.ErrorPostParamsMissing)
            "UserAlreadyExist" -> description = getString(R.string.UserAlreadyExist)
        }

        return description
    }

    /**
     * Metodo para limpiar el formulario de registro
     */
    private fun limpiarFormulario()
    {
        this.name?.setText("")
        this.email?.setText("")
        this.password?.setText("")
    }

    private fun requestError() : Response.ErrorListener
    {
        return Response.ErrorListener { error ->

            // Show error message

            // ! -- Remember if we get this error in Android com.android.volley.NoConnectionError: java.net.SocketException: socket failed: EACCES (Permission denied)
            // its because we need to add an allowing permision to our app to access to Internet or local network in the manifest
            // <uses-permission android:name="android.permission.INTERNET" />

            Log.d("Volley", error.toString())
            pDialog?.hide()
            lanzarSnack(R.id.drawer_layout_register, getString(R.string.noHayConexionServidor), Snackbar.LENGTH_LONG)
        }
    }
}
