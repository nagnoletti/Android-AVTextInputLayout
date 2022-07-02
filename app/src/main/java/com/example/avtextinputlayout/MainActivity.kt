package com.example.avtextinputlayout

import android.app.Activity
import android.os.Bundle
import com.example.avtextinputlayout.R
import nagnoletti.android.textfieldview.AVTextInputLayout

class MainActivity : Activity() {

    private var passwordInput: CharSequence? = null

    private val passwordTextField by lazy { findViewById<AVTextInputLayout>(R.id.password_text_field) }
    private val clickableTextField by lazy { findViewById<AVTextInputLayout>(R.id.clickable_text_field) }
    private val clickableIconTextField by lazy { findViewById<AVTextInputLayout>(R.id.clickable_icon_text_field) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passwordTextField.apply {
            validationStrategy = PasswordValidationStrategy(context)
            eventListener = object : AVTextInputLayout.EventListener {
                override fun onValidation(isValid: Boolean, text: CharSequence?) {
                    passwordInput = if (isValid) text else null
                }
            }
        }

        clickableTextField.apply {
            eventListener = object : AVTextInputLayout.EventListener {
                override fun onAction(text: CharSequence?) {
                    clickableTextField.editText?.setText(R.string.something)
                }
            }
        }

        clickableIconTextField.apply {
            eventListener = object : AVTextInputLayout.EventListener {
                override fun onAction(text: CharSequence?) {
                    clickableIconTextField.editText?.setText(R.string.something)
                }
            }
        }
    }
}