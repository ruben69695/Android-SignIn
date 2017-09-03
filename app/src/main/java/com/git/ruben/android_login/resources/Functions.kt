package com.git.ruben.android_login.resources

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by Ruben on 03/09/2017.
 */
class Functions {

    // Static variables and functions of the class
    companion object {

        /**
         * Method to hide the keyboard when you don't need it
         * @param currentFocus : actual focus view
         * @param context : actul context of the application normally use 'this' parameter
         */
        fun hideKeyboard(currentFocus : View, context : Context)
        {
            val view = currentFocus
            if(view != null)
            {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}