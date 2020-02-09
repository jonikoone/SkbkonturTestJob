package com.jonikoone.skbkonturtestjob.ui.activities

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.jonikoone.skbkonturtestjob.R
import com.jonikoone.skbkonturtestjob.receivers.NetworkStatusReceiver
import com.jonikoone.skbkonturtestjob.screens.AppScreens
import com.jonikoone.skbkonturtestjob.screens.ContactListScreen
import com.jonikoone.skbkonturtestjob.screens.ContactProfileScreen
import com.jonikoone.skbkonturtestjob.services.ServiceContacts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.android.support.SupportAppScreen
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), KodeinAware, CoroutineScope {
    private val TAG = this::class.java.canonicalName
    override val coroutineContext = Dispatchers.Main
    override val kodein = Kodein.lazy {
        bind<Cicerone<Router>>() with singleton { Cicerone.create() }
        bind<OkHttpClient>() with singleton {
            OkHttpClient.Builder()
                .cache(Cache(this@MainActivity.cacheDir, 10_485_760L)) // cache 10M
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(CacheInterceptor())
                .build()
        }
        bind<ServiceContacts>() with singleton {
            Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .client(instance())
                .build()
                .create(ServiceContacts::class.java)
        }
    }

    private val cicerone by instance<Cicerone<Router>>()

    private val navigator = object : SupportAppNavigator(
        this,
        R.id.fragmentContainer
    ) {
        override fun createFragment(screen: SupportAppScreen?): Fragment {
            screen as AppScreens
            return when (screen) {
                is ContactListScreen -> screen.fragment
                is ContactProfileScreen -> screen.fragment
            }
        }
    }

    private var fragmentContainer: View? = null
    lateinit var showSnackbarNegative: () -> Unit
    lateinit var showSnackbarPositive: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cicerone.router.newRootScreen(ContactListScreen())
        fragmentContainer = findViewById(R.id.fragmentContainer)

        showSnackbarNegative = {
            Snackbar.make(fragmentContainer!!, getString(R.string.connectionFailed), Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(resources.getColor(R.color.colorWarning))
                .show()
        }

        showSnackbarPositive = {
            Snackbar.make(fragmentContainer!!, getString(R.string.connectionOK), Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                .show()
        }

        registerReceiver(NetworkStatusReceiver().apply { mainActivity = this@MainActivity },
            IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

    }

    override fun onResume() {
        super.onResume()
        cicerone.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        cicerone.navigatorHolder.removeNavigator()
    }

    override fun onBackPressed() {
        cicerone.router.exit()
    }

    fun isOnline(): Boolean {
        var isConnected: Boolean
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        isConnected =
            activeNetwork?.type == (ConnectivityManager.TYPE_WIFI or ConnectivityManager.TYPE_MOBILE)
        Log.e(TAG, "connection status is $isConnected")
        showSnackbarNegative
        return isConnected
    }


    inner class CacheInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            request = if (isOnline())
                request.newBuilder()
                    .cacheControl(
                        CacheControl.Builder()
                            .maxAge(1, TimeUnit.MINUTES).build()
                    ).build()
            else request.newBuilder()
                .cacheControl(
                    CacheControl.Builder().onlyIfCached()
                        .maxStale(7, TimeUnit.DAYS).build()
                ).build()

            return chain.proceed(request)
        }
    }
}


