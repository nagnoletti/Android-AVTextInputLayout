package nagnoletti.android.textfieldview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View.OnClickListener
import com.google.android.material.textfield.TextInputLayout

class TextFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleInt: Int = 0
) : TextInputLayout(context, attrs, defStyleInt) {

    interface EventListener {
        fun onAction(text: CharSequence?) = Unit
        fun onValidation(isValid: Boolean, text: CharSequence?) = Unit
    }

    // region Styleable

    private var mode: TextFieldViewMode = TextFieldViewMode.default
        set(value) {
            field = value
            changeMode(value)
        }

    var validationStrategy: TextFieldViewValidationStrategy? = null
        set(value) {
            field = value
            setValidationStrategyEffects(value)
        }

    private var inputType: Int? = null
        set(value) {
            field = value
            // Default to text type
            editText?.inputType = value ?: InputType.TYPE_CLASS_TEXT
        }

    private var fieldValidationError: CharSequence? = null

    // endregion

    var eventListener: EventListener? = null

    private var textChangedListener: ((Editable?) -> Unit)? = null
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(text: Editable?) {
            textChangedListener?.invoke(text)
        }
    }

    private companion object {
        const val INPUT_TYPE_PASSWORD =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        const val INPUT_TYPE_NUMBERS =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        const val INPUT_TYPE_EMAIL =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        const val INPUT_TYPE_TEXT = InputType.TYPE_CLASS_TEXT
    }


    init {
        inflate(context, R.layout.view_text_field, this)
        parseAttributes(attrs)
        editText?.addTextChangedListener(textWatcher)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        // Parse attributes
        attrs?.let {
            val a = context.obtainStyledAttributes(attrs, R.styleable.TextFieldView)

            mode = a.hasValue(R.styleable.TextFieldView_mode)
                .takeIf { it }
                ?.let {
                    val raw =
                        a.getInt(R.styleable.TextFieldView_mode, TextFieldViewMode.default.raw)
                    TextFieldViewMode.values().singleOrNull { it.raw == raw }
                } ?: TextFieldViewMode.default

            inputType = a.hasValue(R.styleable.TextFieldView_input)
                .takeIf { it }
                ?.let {
                    when (a.getInt(R.styleable.TextFieldView_input, 0)) {
                        1 -> INPUT_TYPE_EMAIL
                        2 -> INPUT_TYPE_NUMBERS
                        3 -> INPUT_TYPE_PASSWORD
                        else -> null
                    }
                } ?: INPUT_TYPE_TEXT

            fieldValidationError = a.hasValue(R.styleable.TextFieldView_fieldValidationError)
                .takeIf { it }
                ?.let { a.getString(R.styleable.TextFieldView_fieldValidationError) }

            a.recycle()
        }

        setInputTransformationMethod()
        setInputIconMode()
    }

    private fun setInputIconMode() {
        when {
            inputType == INPUT_TYPE_PASSWORD -> endIconMode =
                END_ICON_PASSWORD_TOGGLE
            endIconDrawable != null -> {
                errorIconDrawable = null
                endIconMode = END_ICON_CUSTOM
                setEndIconOnClickListener { eventListener?.onAction(editText?.text) }
            }
        }
    }

    private fun setInputTransformationMethod() {
        editText?.transformationMethod = when (inputType) {
            INPUT_TYPE_PASSWORD -> PasswordTransformationMethod.getInstance()
            else -> null
        }
    }

    /**
     * Initializes text field mode
     */
    private fun changeMode(mode: TextFieldViewMode) {
        val isEditMode = mode == TextFieldViewMode.Edit
        editText?.isFocusableInTouchMode = isEditMode
        editText?.isCursorVisible = isEditMode
        editText?.isLongClickable = isEditMode
        if (!isEditMode) {
            val onClickListener = OnClickListener { eventListener?.onAction(editText?.text) }
            editText?.setOnClickListener(onClickListener)
            setOnClickListener(onClickListener)
        } else {
            editText?.setOnClickListener(null)
            setOnClickListener(null)
        }
    }

    /**
     * Initializes effects of validation on the field text and validation callback
     */
    private fun setValidationStrategyEffects(textFieldViewValidationStrategy: TextFieldViewValidationStrategy?) {
        textChangedListener = null

        if (textFieldViewValidationStrategy != null) {
            textChangedListener = { text: Editable? ->

                val isValid = text?.let { textFieldViewValidationStrategy.validate(text) } ?: true
                error = when {
                    text.isNullOrBlank() -> textFieldViewValidationStrategy.notEmptyFieldError
                        ?: fieldValidationError
                    !isValid -> textFieldViewValidationStrategy.specializationFieldError
                        ?: fieldValidationError
                    else -> null
                }
                eventListener?.onValidation(isValid, text)
            }
        }
    }
}