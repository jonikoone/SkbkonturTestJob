package com.jonikoone.skbkonturtestjob.services

import com.jonikoone.skbkonturtestjob.model.Contact
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ServiceContacts {
    @GET(value = "{file}")
    fun loadList1(@Path(value = "file") fileName: String): Call<List<Contact>>
}