# Android-AVTextInputLayout
Little extension of TextInputLayout to provide a comfy way of doing validation, interact with tap events, tweak behavior and input type.

**Feel free to use the AVTextInputLayout library within this project, open issues or suggest other common use cases to be implemented.**

The app module contains some examples to use the AVTextInputLayout viewgroup.

Use `app:input` styleable attribute to tweak soft-keyboard appearance.
You can use the following values:
  - `text`: text input;
  - `email`: email input;
  - `numbers`: only numbers keyboard, similar to numberPassword but with visible input (no number input punctuation);
  - `password`: password text variation input.

Use `app:mode` to set how the tap event should be triggered:
  - `edit`: only icon tap events trigger AVTextInputLayout.EventListener's `onAction` callback.
  - `click`: the callback is triggered by tapping on both icon and input (inner TextInputEditText, making it uneditable by the user).

Use `app:invalidError` to provide a static validation error used when optional AVTextInputLayout.ValidationStrategy doesn't return an error.

Learn how to set validation strategy and event listener of the AVTextInputLayout [here](./app/src/main/java/com/example/avtextinputlayout/MainActivity.kt).
