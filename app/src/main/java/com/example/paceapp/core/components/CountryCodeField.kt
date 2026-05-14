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
import androidx.compose.ui.unit.sp
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
    val dialogTextStyle = AppTheme.typography.medium
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
                searchBarEnteredTextTextStyle = dialogTextStyle.copy(
                    fontSize = 16.sp,
                    color = AppColors.DarkCharcoal,
                ),
                searchBarHintTextStyle = dialogTextStyle.copy(
                    fontSize = 16.sp,
                    color = AppColors.HintGray,
                ),
                countryNameTextStyle = dialogTextStyle.copy(
                    color = AppColors.DarkCharcoal,
                    fontSize = 16.sp,
                ),
                countryPhoneCodeTextStyle = dialogTextStyle.copy(
                    color = AppColors.Black,
                    fontSize = 18.sp,
                ),
                titleTextStyle = AppTheme.typography.semiBold.copy(
                    color = AppColors.DarkCharcoal,
                    fontSize = 20.sp,
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
        textStyle = AppTheme.typography.medium.copy(fontSize = 16.sp,color = AppColors.White),
        onCountrySelected = {},
        modifier = Modifier
    )
}
