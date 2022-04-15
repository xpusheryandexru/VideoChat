package com.example.firebasertcandroid.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.firebasertcandroid.*
import com.example.firebasertcandroid.adapters.AdapterRooms
import com.example.firebasertcandroid.app.KEY_URI_WEB_VIDEO_CHAT
import com.example.firebasertcandroid.components.RoomDb
import com.example.firebasertcandroid.components.SharedPref
import com.example.firebasertcandroid.components.Signaling
import com.example.firebasertcandroid.components.Utils
import com.example.firebasertcandroid.databinding.FragmentHomeBinding
import com.example.firebasertcandroid.ui.CHAT_URI
import com.example.firebasertcandroid.ui.CHAT_URI_ERROR
import com.example.firebasertcandroid.ui.ChatActivity
import com.google.firebase.firestore.DocumentChange
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class FragmentHome : Fragment()
{

    private val viewModel: ViewModelFragmentHome by viewModels()

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var presenterHomeFragment: PresenterFragmentHome

    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var roomDb:RoomDb

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {

        lifecycle.coroutineScope.launch {
            roomDb.log("FragmentHome:onCreateView")
        }


        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val adapter=AdapterRooms { strUri ->
            presenterHomeFragment.openRoom(strUri)
        }

        _binding?.zzHomeFragmentRecycler?.adapter=adapter

        viewModel.listRooms.observeForever { listRoomsValue ->

            adapter.submitList(listRoomsValue)

            when (listRoomsValue?.size)
            {
                null -> {

                    _binding?.zzAnimatedView?.let {
                        utils.animateLoadingView(it)
                    }

                }

                0 -> {

                    _binding?.zzAnimatedView?.let { imageView ->

                        utils.textToImageView(
                            getString(R.string.zz_click_add),
                            imageView
                        )

                    }
                }

                else -> {

                    _binding?.zzAnimatedView?.let {
                        utils.animateLoadingView(it, stop = true)
                    }
                }
            }

        }

        _binding?.fab?.setOnClickListener{

            val strChatUri= sharedPref.pref.getString(KEY_URI_WEB_VIDEO_CHAT,null)

            if (strChatUri==null)
            {

                viewLifecycleOwner.lifecycle.coroutineScope.launch {

                    val arrayCharSequence=withContext(Dispatchers.Default)
                    {

                        //for tests (long operation possible)
                        //Thread.sleep(5000)

                        Array<CharSequence>(
                            size = viewModel.listRooms.value?.size ?: 0,
                            init = { i ->
                                val uri = Uri.parse(
                                    viewModel.listRooms.value?.get(i)?.document?.get("uri")?.toString() ?: ""
                                )
                                uri?.scheme + "://" + uri?.host
                            }
                        )
                            .toHashSet()
                            .dropWhile { charSequence ->
                                charSequence.contains("${null}")
                            }
                            .toTypedArray()

                    }

                    AlertDialog.Builder(context)
                        .setTitle(
                            when(arrayCharSequence.size)
                            {
                                0->R.string.zz_dialog_select_active_url_not_found_uri
                                else->R.string.zz_dialog_select_active_url
                            }
                        )
                        .setItems(arrayCharSequence)
                        { d, i ->

                            sharedPref.storeToPref(
                                KEY_URI_WEB_VIDEO_CHAT,
                                arrayCharSequence[i].toString()
                            )

                            d.dismiss()
                        }
                        .show()

                }

            }
            else
            {
                presenterHomeFragment.openRoom("${strChatUri}/room=new")
            }


        }

        presenterHomeFragment.resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        { result ->

            if (result.resultCode == Activity.RESULT_CANCELED)
            {

                when (val textError = result.data?.getStringExtra(CHAT_URI_ERROR))
                {
                    null -> {
                        utils.showToast(R.string.zz_fragment_home_chat_finish)
                    }

                    else -> {

                        utils.showToast(textError)


                        sharedPref.storeNull(
                            KEY_URI_WEB_VIDEO_CHAT
                        )

                    }

                }

            }
        }


        return binding.root
    }


}

@Module
@InstallIn(FragmentComponent::class)
class PresenterFragmentHome @Inject constructor()
{


    @Inject
    lateinit var sharePref: SharedPref

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var appContext: Context

    lateinit var resultLauncher:ActivityResultLauncher<Intent>

    fun openRoom(strUri: String)
    {

        val intent = Intent(appContext, ChatActivity::class.java)
            .apply {
                putExtra(CHAT_URI, strUri)
            }

        resultLauncher.launch(intent)


    }


}

@HiltViewModel
class ViewModelFragmentHome @Inject constructor() : ViewModel()
{
    @Inject
    lateinit var signaling: Signaling

    @Inject
    fun onCreatedViewModelFragmentHome()
    {
        _listRooms.value=null

        signaling.db.collection("rooms")
            .addSnapshotListener { snapshot, _ ->
                _listRooms.value=snapshot?.documentChanges
            }
    }

    private val _listRooms:MutableLiveData<List<DocumentChange>?> =
        MutableLiveData<List<DocumentChange>?>()
    internal val listRooms:MutableLiveData<List<DocumentChange>?>
    get() {
        return _listRooms
    }

}