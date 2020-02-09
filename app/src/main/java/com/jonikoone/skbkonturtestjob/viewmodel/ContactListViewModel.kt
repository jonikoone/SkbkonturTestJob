package com.jonikoone.skbkonturtestjob.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jonikoone.skbkonturtestjob.adapters.ContactsAdapter
import com.jonikoone.skbkonturtestjob.model.Contact
import com.jonikoone.skbkonturtestjob.services.ServiceContacts
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.await


class ContactListViewModel(val serviceContacts: ServiceContacts, private val context: Context?) :
    ViewModel(), CoroutineScope,
    SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    override val coroutineContext = Dispatchers.Main

    private val TAG = this::class.java.canonicalName

    val adapter by lazy { ContactsAdapter() }
    private var mShowProgressBar = MutableLiveData<Int>(View.VISIBLE)
    private var mIsShowWarningMessage = MutableLiveData<Int>(View.GONE)

    val showProgressBar: LiveData<Int> = mShowProgressBar
    val isShowWarningMessage: LiveData<Int> = mIsShowWarningMessage

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private fun uploadData() = async(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            mShowProgressBar.value = View.VISIBLE
            mIsShowWarningMessage.value = View.GONE
        }
        Log.i(TAG, "load contacts")
        context?.let {
            try {
                //выполнится если будет кеш или интренет
                val list1 = serviceContacts.loadList1("generated-01.json")
                val list2 = serviceContacts.loadList1("generated-02.json")
                val list3 = serviceContacts.loadList1("generated-03.json")
                val fullList = mutableListOf<Contact>()
                fullList += list1.await()
                fullList += list2.await()
                fullList += list3.await()
                withContext(Dispatchers.Main) {
                    mShowProgressBar.value = View.GONE
                    mIsShowWarningMessage.value = View.GONE
                    adapter.setContacts(fullList)
                }
            } catch (e: HttpException) {
                Log.e(TAG, "cache is empty and network is not available")
                withContext(Dispatchers.Main) {
                    mShowProgressBar.value = View.GONE
                    mIsShowWarningMessage.value = View.VISIBLE
                }
            }
        }

    }


    init {
        //загрузка данны при создании viewModel
        uploadData()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter.applyFilter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.applyFilter(newText)
        return false
    }

    override fun onRefresh() {
        launch {
            uploadData().await()
            swipeRefreshLayout.isRefreshing = false
        }
    }

}