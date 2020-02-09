package com.jonikoone.skbkonturtestjob.model

import androidx.annotation.IdRes
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jonikoone.skbkonturtestjob.R
import com.jonikoone.skbkonturtestjob.preparePhoneForSearch
import java.io.Serializable
import java.util.*

data class EducationPeriod(
    @SerializedName("start")
    val start: Date,
    @SerializedName("end")
    val end: Date
) : Serializable

enum class Temperament(@IdRes val typeId: Int) : Serializable {
    melancholic(R.string.type_melancholic),
    sanguine(R.string.type_sanguine),
    choleric(R.string.type_choleric),
    phlegmatic(R.string.type_phlegmatic)
}

data class Contact(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("height")
    val height: Float,
    @SerializedName("biography")
    val biography: String,
    @SerializedName("temperament")
    @Expose
    val temperament: Temperament,
    @SerializedName("educationPeriod")
    @Expose
    val educationPeriod: EducationPeriod
) : Serializable {
    val phoneForSearch: String
        get() = phone.trim()
            .replace(preparePhoneForSearch, "")
}

