package nagnoletti.android.textfieldview;

import java.util.regex.Pattern

abstract class TextFieldViewValidationStrategy {

    abstract val regex: String?
    open val notEmptyFieldError: String? = null
    open val specializationFieldError: String? = null

    fun validate(text: CharSequence): Boolean = regex?.let {
        Pattern.compile(it).matcher(text)
            .matches()
    } ?: true

    companion object {
        val None = object : TextFieldViewValidationStrategy() {
            override val regex: String?
                get() = null
        }
    }
}