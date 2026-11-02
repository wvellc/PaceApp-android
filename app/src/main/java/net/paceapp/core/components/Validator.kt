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
        // Phone is digit-based, not a single regex, so it gets its own path.
        if (type == ValidatorType.Phone) return validatePhone(value)

        val regEx = when (type) {
            ValidatorType.Email -> Patterns.EMAIL_ADDRESS
            ValidatorType.Name -> Pattern.compile("^[a-zA-Z ]{2,}$")
            ValidatorType.Phone -> null
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

    // Phone: exactly 10 digits (grouping ignored), a leading 2–9 as every real
    // numbering plan uses, and not the same digit ten times over. Ten identical
    // digits gets its own gentle message instead of the generic one.
    private fun validatePhone(value: String?): Int? {
        val digits = (value ?: "").filter { it.isDigit() }
        return when {
            digits.length == 10 && digits.toSet().size == 1 -> R.string.phone_repeated_digits
            digits.matches(Regex("^[2-9][0-9]{9}$")) -> null
            else -> R.string.enter_a_valid_phone_number
        }
    }

}
