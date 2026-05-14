package com.example.paceapp.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
fun SearchTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholderText: String = "Search here...",
    trailingIcon: (@Composable () -> Unit)? = null
) {
    BasicTextField(
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        textStyle = AppTheme.typography.medium.copy(
            lineHeight = 24.sp,
            fontSize = 16.sp,
            color = AppColors.White // Assumes white text for the dark field
        ),
        cursorBrush = SolidColor(AppColors.NeonAquaBlue),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
            capitalization = KeyboardCapitalization.Words,
        ),
        decorator = { innerTextField ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, AppColors.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Leading Search Icon
                Image(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search Icon",
                    modifier = Modifier.size(24.dp)
                )


                // Text Field & Hint Wrapper
                Box(modifier = Modifier.weight(1f)) {
                    // Check if the state's text is empty to show the placeholder
                    if (state.text.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = AppTheme.typography.medium.copy(
                                lineHeight = 24.sp,
                                fontSize = 16.sp,
                                color = AppColors.HintGray.copy(alpha = 0.5f)
                            ),
                        )
                    }
                    innerTextField()
                }

                // Conditional Clear Icon
//                if (state.text.isNotEmpty()) {
//                    Image(
//                        painter = painterResource(R.drawable.ic_sync),
//                        contentDescription = "Clear Icon",
//                        modifier = Modifier
//                            .size(24.dp)
//                            .defaultClickable {
//                                // Clears the text directly via the TextFieldState API
//                                state.edit { replace(0, length, "") }
//                            }
//                    )
//                }

                // Trailing Clickable Filter Icon
                if (trailingIcon != null) {
                    trailingIcon()
                }
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SearchTextFieldPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Previewing with an initial state
        val filledState = rememberTextFieldState(initialText = "Morning run")
        SearchTextField(
            state = filledState
        )

        // Previewing the empty state
        val emptyState = rememberTextFieldState()
        SearchTextField(
            state = emptyState
        )
    }
}