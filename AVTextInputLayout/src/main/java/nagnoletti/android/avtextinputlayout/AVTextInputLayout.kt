package nagnoletti.android.avtextinputlayout

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View.OnClickListener
import com.google.android.material.textfield.TextInputLayout
import nagnoletti.android.avtextinputlayout.AVTextInputLayout.EventListener
import nagnoletti.android.avtextinputlayout.AVTextInputLayout.Mode.Click
import nagnoletti.android.avtextinputlayout.AVTextInputLayout.Mode.Edit
import java.util.regex.Pattern

/**
 * AVTextInputLayout
 * "Action-Validation" TextInputLayout extending Google's Material TextInputLayout.
 * It provides [EventListener] to connect validation and action performed clicking end icon or both
 * the icon and the [editText] within the layout.
 * It also provides an easy way of setting soft keyboard layout for certain types of input through
 * the [R.styleable.AVTextInputLayout_input] attribute.
 *
 */
class AVTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleInt: Int = 0
) : TextInputLayout(context, attrs, defStyleInt) {

    /**
     * EventListener
     *  - [onAction]: connect tap event on end icon or both icon and [editText] depending on
     *  currently set [Mode].
     *  - [onValidation]: receive validation result and text on every input change.
     */
    interface EventListener {
        fun onAction(text: CharSequence?) = Unit
        fun onValidation(isValid: Boolean, text: CharSequence?) = Unit
    }

    /**
     * Mode
     * Determine tap event behavior.
     *  - [Edit]: connect tap event to the icon tap event.
     *  - [Click]: connect tap event to both the icon and the [editText] tap events.
     */
    enum class Mode(val raw: Int) {
        Edit(0),
        Click(1);

        companion object {
            val default: Mode get() = Edit
        }
    }

    /**
     * ValidationStrategy
     * Provides a regex to validate input with the [getRegex] function.
     * Returned value of [getEmptyInputError] is used to show or not an error when input is empty.
     * Returned value of [getInvalidInputError] is used to show or not an error when input doesn't
     * pass validation.
     */
    interface ValidationStrategy {

        fun getRegex(): String? = null
        fun getEmptyInputError(): String? = null
        fun getInvalidInputError(): String? = null

        fun validate(text: CharSequence): Boolean = getRegex()
            ?.let { Pattern.compile(it).matcher(text).matches() }
            ?: true
    }

    // region Styleable

    private var mode: Mode = Mode.default

    var validationStrategy: ValidationStrategy? = null

    private var inputType: Int? = null

    private var invalidError: CharSequence? = null

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
        parseAttributes(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        initInputType()
        initValidationStrategy()
        initMode()
        setInputTransformationMethod()
        setInputIconMode()

        editText?.addTextChangedListener(textWatcher)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        // Parse attributes
        attrs?.let {
            val a = context.obtainStyledAttributes(attrs, R.styleable.AVTextInputLayout)

            mode = a.hasValue(R.styleable.AVTextInputLayout_mode)
                .takeIf { it }
                ?.let {
                    val raw =
                        a.getInt(R.styleable.AVTextInputLayout_mode, Mode.default.raw)
                    Mode.values().singleOrNull { it.raw == raw }
                } ?: Mode.default

            inputType = a.hasValue(R.styleable.AVTextInputLayout_input)
                .takeIf { it }
                ?.let {
                    when (a.getInt(R.styleable.AVTextInputLayout_input, 0)) {
                        1 -> INPUT_TYPE_EMAIL
                        2 -> INPUT_TYPE_NUMBERS
                        3 -> INPUT_TYPE_PASSWORD
                        else -> null
                    }
                } ?: INPUT_TYPE_TEXT

            invalidError = a.hasValue(R.styleable.AVTextInputLayout_invalidError)
                .takeIf { it }
                ?.let { a.getString(R.styleable.AVTextInputLayout_invalidError) }

            a.recycle()
        }
    }

    private fun initInputType() {
        // Default to text type
        editText?.inputType = inputType ?: InputType.TYPE_CLASS_TEXT
    }

    private fun setInputIconMode() {
        when {
            inputType == INPUT_TYPE_PASSWORD -> endIconMode = END_ICON_PASSWORD_TOGGLE
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

    private fun initMode() {
        val mode = mode
        val isEditMode = mode == Edit
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

    private fun initValidationStrategy() {
        textChangedListener = null

        val validationStrategy = validationStrategy
        if (validationStrategy != null) {
            textChangedListener = { text: Editable? ->

                val isValid = text?.let { validationStrategy.validate(text) } ?: true
                error = when {
                    text.isNullOrBlank() -> validationStrategy.getEmptyInputError()
                    !isValid -> validationStrategy.getInvalidInputError()
                        ?: invalidError
                    else -> null
                }
                eventListener?.onValidation(isValid, text)
            }
        }
    }
}