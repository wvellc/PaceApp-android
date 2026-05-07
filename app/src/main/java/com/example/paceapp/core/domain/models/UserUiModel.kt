package com.example.paceapp.core.domain.models

import com.example.paceapp.core.domain.enums.LoginTypes

data class UserUiModel(
    val id: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val fullNameAndGender: String,
    val contactInfo: String, // email or phone based on loginType
    val profilePic: String?,
    val loginType: LoginTypes
)