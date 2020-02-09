package com.jonikoone.skbkonturtestjob.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import com.jonikoone.skbkonturtestjob.model.Contact
import com.jonikoone.skbkonturtestjob.screens.ContactProfileScreen
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

class ContactViewModel(private val context: Context) : ViewModel(), KodeinAware {
    override val kodein by kodein(context)

    private val cicerone by instance<Cicerone<Router>>()

    companion object {
        private const val format = "dd.MM.yyyy"
    }

    var contact: Contact? = null

    val name
        get() = contact?.name
    val phone
        get() = contact?.phone
    val height
        get() = contact?.height.toString()
    val biography
        get() = contact?.biography
    val temperament
        get() = context.resources.getString(contact?.temperament?.typeId!!)

    val educationPeriod: String
        get() = "${DateFormat.format(format, contact?.educationPeriod?.start)} " +
                "- ${DateFormat.format(format, contact?.educationPeriod?.end)}"

    fun exit() {
        cicerone.router.exit()
    }

    fun showContact() {
        cicerone.router.navigateTo(
            ContactProfileScreen(contact!!)
        )
    }

    fun startCall() {
        context.startActivity(Intent(Intent.ACTION_DIAL).apply {
            setData(Uri.parse("tel:$phone"))
        })
    }

}