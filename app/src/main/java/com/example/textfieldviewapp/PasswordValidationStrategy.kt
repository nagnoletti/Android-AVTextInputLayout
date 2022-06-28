package com.example.textfieldviewapp

import android.content.Context
import nagnoletti.android.textfieldview.TextFieldViewValidationStrategy
/**
 * Validation strategy for a TextFieldView of type password.
 * At least an uppercase letter, a lowercase letter, a number and a special character
 */
class PasswordValidationStrategy(private val context: Context): TextFieldViewValidationStrategy() {

    override val regex: String?
        get() = "^(?=^.{8,}\$)((?=.*[A-Za-z0-9])(?=.*[^A-Za-z0-9])(?=.*[A-Z]))^.*\$"
    override val specializationFieldError: String?
        get() = context.getString(R.string.password_field_error)
}