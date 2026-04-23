package com.example.paceapp.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arpitkatiyarprojects.countrypicker.CountryPicker
import com.arpitkatiyarprojects.countrypicker.enums.CountryListDisplayType
import com.arpitkatiyarprojects.countrypicker.models.CountriesListDialogDisplayProperties
import com.arpitkatiyarprojects.countrypicker.models.CountriesListDialogProperties
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.arpitkatiyarprojects.countrypicker.models.CountryPickerColors
import com.arpitkatiyarprojects.countrypicker.models.CountryPickerDialogTextStyles
import com.arpitkatiyarprojects.countrypicker.models.FlagDimensions
import com.arpitkatiyarprojects.countrypicker.models.SelectedCountryDisplayProperties
import com.arpitkatiyarprojects.countrypicker.models.SelectedCountryProperties
import com.arpitkatiyarprojects.countrypicker.models.SelectedCountryTextStyles
import com.example.paceapp.R
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
fun CountryCodeField(
    modifier: Modifier,
    textStyle: TextStyle,
    defaultCountryCode: String = "US",// Defaults to US (+1)
    enabled: Boolean = true,
    onCountrySelected: (country: CountryDetails) -> Unit,
) {
    CountryPicker(
        modifier = modifier,
        isPickerEnabled = enabled,
        defaultPaddingValues = PaddingValues(horizontal = 8.dp),
        defaultCountryCode = defaultCountryCode,
        // Configure the Prefix UI
        selectedCountryDisplayProperties = SelectedCountryDisplayProperties(
            properties = SelectedCountryProperties(
                showCountryFlag = false,
                showCountryCode = false,
                showDropDownIcon = true,
                showCountryName = false,
                spaceAfterCountryPhoneCode = 8.dp,
                dropDownIconComposable = {
                    Image(
                        painter = painterResource(R.drawable.ic_down_arrow),
                        contentDescription = "Select Country"
                    )
                }
            ),
            textStyles = SelectedCountryTextStyles(
                countryPhoneCodeTextStyle = textStyle
            ),
        ),
        countryListDisplayType = CountryListDisplayType.BottomSheet,
        countriesListDialogDisplayProperties = CountriesListDialogDisplayProperties(
            properties = CountriesListDialogProperties(
                showCountryCode = false
            ),
            textStyles = CountryPickerDialogTextStyles(
                searchBarEnteredTextTextStyle = AppTheme.typography.size16.copy(
                    fontWeight = FontWeight.Medium,
                    color = AppColors.DarkCharcoal,
                ),
                searchBarHintTextStyle = AppTheme.typography.size16.copy(
                    fontWeight = FontWeight.Medium,
                    color = AppColors.HintGray,
                ),
                countryNameTextStyle = AppTheme.typography.size16.copy(
                    color = AppColors.DarkCharcoal,
                    fontWeight = FontWeight.Medium
                ),
                countryPhoneCodeTextStyle = AppTheme.typography.size18.copy(
                    color = AppColors.Black,
                    fontWeight = FontWeight.Medium
                ),
                titleTextStyle = AppTheme.typography.size20.copy(
                    color = AppColors.DarkCharcoal,
                    fontWeight = FontWeight.SemiBold
                )
            ),
            flagShape = RectangleShape,
            flagDimensions = FlagDimensions(width = 28.dp, height = 21.dp)
        ),
        countryPickerColors = CountryPickerColors(
            dropDownIconColor = AppColors.White,
            backIconColor = AppColors.RadiantBlue,
            searchIconColor = AppColors.DarkCharcoal,
            cancelIconColor = AppColors.DarkCharcoal,
            searchCursorColor = AppColors.NeonAquaBlue,
            selectedCountryContainerColor = AppColors.Transparent,
            countriesListContainerColor = AppColors.White,
            selectedCountryDisabledContainerColor = AppColors.FashionGray,
            dropDownDisabledIconColor = AppColors.FashionGray,
        ),
        onCountrySelected = onCountrySelected,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF091E5B)
@Composable
private fun CountryCodeFieldPreview() {
    CountryCodeField(
        textStyle = AppTheme.typography.size16.copy(color = AppColors.White),
        onCountrySelected = {},
        modifier = Modifier
    )
}
