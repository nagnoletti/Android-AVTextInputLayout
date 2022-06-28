package com.example.textfieldviewapp

import android.app.Activity
import android.os.Bundle
import nagnoletti.android.textfieldview.TextFieldView

class MainActivity : Activity() {

    private var passwordInput: CharSequence? = null

    private val passwordTextField by lazy { findViewById<TextFieldView>(R.id.password_text_field) }
    private val clickableTextField by lazy { findViewById<TextFieldView>(R.id.clickable_text_field) }
    private val clickableIconTextField by lazy { findViewById<TextFieldView>(R.id.clickable_icon_text_field) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passwordTextField.apply {
            validationStrategy = PasswordValidationStrategy(context)
            eventListener = object : TextFieldView.EventListener {
                override fun onValidation(isValid: Boolean, text: CharSequence?) {
                    passwordInput = if (isValid) text else null
                }
            }
        }

        clickableTextField.apply {
            eventListener = object : TextFieldView.EventListener {
                override fun onAction(text: CharSequence?) {
                    clickableTextField.editText?.setText(R.string.something)
                }
            }
        }

        clickableIconTextField.apply {
            eventListener = object : TextFieldView.EventListener {
                override fun onAction(text: CharSequence?) {
                    clickableIconTextField.editText?.setText(R.string.something)
                }
            }
        }
    }
}