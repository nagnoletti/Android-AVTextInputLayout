package com.example.avtextinputlayout

import android.content.Context
import nagnoletti.android.avtextinputlayout.AVTextInputLayout

/**
 * Validation strategy for AVTextInputLayout used for passwords.
 * At least an uppercase letter, a lowercase letter, a number and a special character
 */
class PasswordValidationStrategy(private val context: Context) :
    AVTextInputLayout.ValidationStrategy {

    override fun getRegex(): String =
        "^(?=^.{8,}\$)((?=.*[A-Za-z0-9])(?=.*[^A-Za-z0-9])(?=.*[A-Z]))^.*\$"

    override fun getInvalidInputError(): String = context.getString(R.string.password_field_error)
}