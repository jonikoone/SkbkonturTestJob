package com.jonikoone.skbkonturtestjob.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jonikoone.skbkonturtestjob.R
import com.jonikoone.skbkonturtestjob.databinding.FragmentContactListBinding
import com.jonikoone.skbkonturtestjob.services.ServiceContacts
import com.jonikoone.skbkonturtestjob.viewmodel.ContactListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ContactListFragment : Fragment(), KodeinAware, CoroutineScope {
    override val kodein by kodein()
    override val coroutineContext = Dispatchers.Main

    private val serviceContacts by instance<ServiceContacts>()

    private val viewModel by lazy { ContactListViewModel(serviceContacts, this@ContactListFragment.activity) }


    override fun onResume() {
        super.onResume()
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentContactListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val view = binding.root

        binding.viewModel = viewModel
        view.findViewById<RecyclerView>(R.id.recyclerContacts)
            ?.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )

        view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)?.let {
            viewModel.swipeRefreshLayout = it
            it.setOnRefreshListener(viewModel)
        }

        view.findViewById<Toolbar>(R.id.toolbar)?.apply {
            val menuItem = this.menu.findItem(R.id.action_search) as MenuItem
            val searchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextListener(viewModel)
        }

        return view
    }
}




