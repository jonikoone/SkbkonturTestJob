package com.jonikoone.skbkonturtestjob.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonikoone.skbkonturtestjob.databinding.ItemContactBinding
import com.jonikoone.skbkonturtestjob.model.Contact
import com.jonikoone.skbkonturtestjob.preparePhoneForSearch
import com.jonikoone.skbkonturtestjob.viewmodel.ContactViewModel

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ViewHolderContact>() {
    private val contacts = mutableListOf<Contact>()
    private val contactsWithFilter = mutableListOf<Contact>()

    private var actualList = contacts

    fun setContacts(newList: List<Contact>) {
        contacts.clear()
        contacts.addAll(newList)
        actualList = contacts
        notifyDataSetChanged()
    }

    inner class ViewHolderContact(private val itemViewModel: ItemContactBinding) :
        RecyclerView.ViewHolder(itemViewModel.root) {
        fun bind(contact: Contact) {
            itemViewModel.viewModel =
                ContactViewModel(itemViewModel.root.context).apply { this.contact = contact }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderContact {
        val layoutInflater = LayoutInflater.from(parent.context)
        val contactView = ItemContactBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ViewHolderContact(contactView)
    }

    override fun getItemCount() = actualList.size

    override fun onBindViewHolder(holder: ViewHolderContact, position: Int) {
        holder.bind(actualList[position])
    }

    fun applyFilter(newText: String?) {
        newText as CharSequence
        if (newText.isNotEmpty()) {
            contactsWithFilter.clear()
            contacts.filter {
                it.name.contains(newText, true) or
                        it.phoneForSearch.contains(newText)
            }
                .toCollection(contactsWithFilter)
            actualList = contactsWithFilter
            notifyDataSetChanged()
        } else {
            actualList = contacts
            notifyDataSetChanged()
        }
    }

}