package nagnoletti.android.textfieldview;

enum class TextFieldViewMode(val raw: Int) {
        Edit(0),
        Click(1);

        companion object {
            val default: TextFieldViewMode get() = Edit
        }
    }
