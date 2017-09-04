package com.git.ruben.android_login

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.git.ruben.android_login.classes.User
import com.git.ruben.android_login.resources.AppConfig
import com.git.ruben.android_login.resources.Functions
import org.json.JSONObject

class loginActivity : AppCompatActivity() {

    private var email : EditText? = null
    private var password : EditText? = null
    private var vistaPrincipal : View? = null
    private var queue : RequestQueue? = null
    private var pDialog : ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // CASTING FROM VIEWS TO KOTLIN OBJECTS
        email = findViewById(R.id.loginEmail) as EditText
        password = findViewById(R.id.loginPassword) as EditText
        vistaPrincipal = findViewById(R.id.drawer_layout_login) as View

        // We create the queue for the HTTP Requests of the Volley Library
        queue = Volley.newRequestQueue(this)

        // Progress dialog
        pDialog = ProgressDialog(this)
        pDialog?.setCancelable(false)

        // Add the button register event when its clicked
        findViewById(R.id.btLogin).setOnClickListener {

            // Hide the Keyboard
            Functions.hideKeyboard(this.currentFocus, this)

            // Collect the user input data
            val e : String = email?.text.toString()
            val p : String = password?.text.toString()

            // Check if data its OK
            if(!e.isEmpty() && !p.isEmpty())
            {
                try
                {
                    // Try to login
                    attemptToLogin(e, p)
                }
                catch(excp : Exception)
                {
                    excp.printStackTrace()
                }
            }
            else
            {
                // Show the error to the user
                Functions.showSnackbar(vistaPrincipal!!, getString(R.string.errorFaltanDatos))
            }
        }

        // Add the link to register activity
        findViewById(R.id.btnLinkToRegisterScreen).setOnClickListener {
            var intent : Intent = Intent(this, registerActivity::class.java)
            startActivity(intent)
        }

    }

    /**
     * Method to try to login the user
     * @param email : User email
     * @param password : User password
     */
    private fun attemptToLogin(email : String, password : String)
    {
        // Tag to cancel the request
        val tag_string_req : String = "req_login"

        // Show the dialog
        pDialog?.setMessage(getString(R.string.mensajeCargaRegistro))
        pDialog?.show()

        // Create the Volley HTTP Request
        val myReq = object : StringRequest(Method.POST,
                AppConfig.URL_LOGIN,
                requestSuccess(),
                requestError()) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                var params = HashMap<String, String>()
                params.put("email", email)
                params.put("password", password)
                return params
            }
        }

        // Important to add the request to the queue to proceed to execute te request
        queue?.add(myReq)

    }

    // Method executed when the Volley request fails
    private fun requestError(): Response.ErrorListener? {
        return Response.ErrorListener { error ->

            try {

                // Show error message

                // ! -- Remember if we get this error in Android com.android.volley.NoConnectionError: java.net.SocketException: socket failed: EACCES (Permission denied)
                // its because we need to add an allowing permision to our app to access to Internet or local network in the manifest
                // <uses-permission android:name="android.permission.INTERNET" />

                Log.d("Volley", error.toString())
                pDialog?.hide()
                Functions.showSnackbar(vistaPrincipal!!, getString(R.string.noHayConexionServidor))

            }
            catch (excp : Exception) {

                excp.printStackTrace()

            }

        }
    }

    // Method executed when the Volley request is succesfully completed
    private fun requestSuccess(): Response.Listener<String>? {
        return Response.Listener { response ->

            try {

                val jObj : JSONObject = JSONObject(response)
                val error : Boolean = jObj.getBoolean("error")

                if(!error)
                {
                    // User succesfully logged in

                    // Create a Kotlin User Object with the json data returned
                    val user : User = User()
                    user.convertJson_toUser(jObj.getJSONObject("user"))

                    // Show message and clean user inputs
                    Functions.showSnackbar(vistaPrincipal!!, getString(R.string.Session_Started))
                    cleanInputs()

                }
                else
                {
                    // Show message error
                    var errorDescription = getDescriptionError(jObj.getString("error_msg"))
                    Functions.showSnackbar(vistaPrincipal!!, errorDescription)
                }

            }
            catch (excp : Exception) {

                excp.printStackTrace()

            }
            finally {

                // Hide the progress dialog
                pDialog?.hide()

            }

        }
    }

    /**
     * Method to ged the description of the error
     * @param : Code error
     * @return String : Error description
     */
    private fun getDescriptionError(codError : String): String
    {
        var description : String = ""

        when(codError) {
            "Wrong_Credentials" -> description = getString(R.string.Wrong_Credentials)
            "ErrorPostParamsMissing" -> description = getString(R.string.ErrorPostParamsMissing)
        }

        return description
    }

    /**
     * Method to clean the user inputs
     */
    private fun cleanInputs()
    {
        email?.setText("")
        password?.setText("")
    }
}
