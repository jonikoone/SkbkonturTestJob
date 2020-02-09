package com.jonikoone.skbkonturtestjob.screens

import androidx.fragment.app.Fragment
import com.jonikoone.skbkonturtestjob.model.Contact
import com.jonikoone.skbkonturtestjob.ui.fragments.ContactListFragment
import com.jonikoone.skbkonturtestjob.ui.fragments.ContactProfileFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

sealed class AppScreens(private val screenName: String, private val fragment: Fragment) :
    SupportAppScreen() {
    override fun getScreenKey() = screenName
    override fun getFragment() = fragment
}

class ContactListScreen : AppScreens("Contacts", ContactListFragment())

class ContactProfileScreen(contact: Contact) :
    AppScreens("Detail contact", ContactProfileFragment.newInstance(contact))


