package net.paceapp.core.components

import android.util.Patterns
import androidx.annotation.StringRes
import net.paceapp.R
import java.util.regex.Pattern

enum class ValidatorType(@param:StringRes val errorResId: Int?) {
    Email(R.string.enter_a_valid_email_address),
    Phone(R.string.enter_a_valid_phone_number),
    Password(R.string.password_error),
    ConfirmPassword(R.string.confirm_password_error),
    Name(R.string.enter_a_valid_name),
    Text(R.string.enter_a_valid_input),
    Number(R.string.enter_a_valid_number),
    None(null);
}

fun ValidatorType.isPasswordTypeField(): Boolean =
    this == ValidatorType.Password || this == ValidatorType.ConfirmPassword

object Validator {
    fun validate(value: String?, type: ValidatorType): Int? {
        val regEx = when (type) {
            ValidatorType.Email -> Patterns.EMAIL_ADDRESS
            ValidatorType.Name -> Pattern.compile("^[a-zA-Z ]{2,}$")
            ValidatorType.Phone -> Pattern.compile("^(?:[+0]9)?[0-9]{10,14}$")
            ValidatorType.Text -> Pattern.compile("^([a-zA-Z0-9\\s\\n'`!@#$%^&*()])+$")
            ValidatorType.Number -> Pattern.compile("^\\d*\\.?\\d+\$")
            /// Password (Hard) Regex
            /// Allowing all character except 'whitespace'
            /// Must contains at least: 1 uppercase letter, 1 lowercase letter, 1 number, & 1 special character (symbol)
            /// Minimum character: 8
            ValidatorType.Password, ValidatorType.ConfirmPassword -> Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])\\S{8,}\$")
            ValidatorType.None -> null
        }

        return when {
            regEx?.matcher(value ?: "")?.matches() == true -> null
            else -> type.errorResId
        }
    }

}
