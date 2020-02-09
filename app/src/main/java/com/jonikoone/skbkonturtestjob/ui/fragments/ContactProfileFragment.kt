package com.jonikoone.skbkonturtestjob.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jonikoone.skbkonturtestjob.R
import com.jonikoone.skbkonturtestjob.databinding.FragmentContactProfileBinding
import com.jonikoone.skbkonturtestjob.model.Contact
import com.jonikoone.skbkonturtestjob.viewmodel.ContactViewModel

class ContactProfileFragment : Fragment() {

    companion object {
        private const val ARG_TAG_CONTACT = "arg tag contact"

        fun newInstance(contact: Contact) = ContactProfileFragment().apply {
            val args = Bundle()
            args.putSerializable(ARG_TAG_CONTACT, contact)
            arguments = args
        }
    }

    private val viewModel by lazy { ContactViewModel(context!!) }

    override fun onResume() {
        super.onResume()
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentContactProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_profile, container, false)
        val view = binding.root
        var contact: Contact? = null

        arguments?.let {
            contact = it.getSerializable(ARG_TAG_CONTACT) as Contact?
        }
        viewModel.contact = contact
        binding.viewModel = viewModel

        return view
    }

}