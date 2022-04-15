package com.example.firebasertcandroid.ui.log

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.firebasertcandroid.adapters.AdapterLog
import com.example.firebasertcandroid.databinding.FragmentLogBinding
import com.example.firebasertcandroid.components.RoomDb
import com.example.firebasertcandroid.components.EntryLog
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FragmentLog:Fragment()
{
    private val viewModel:ViewModelLog by viewModels()

    @Inject
    lateinit var roomDb:RoomDb
    @Inject
    lateinit var appContext: Context


    private lateinit var binding: FragmentLogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLogBinding.inflate(inflater, container, false)

        val adapterLog= AdapterLog()

        binding.fab.setOnClickListener {

            lifecycleScope.launch {
                roomDb.log("test log entry ")
                viewModel.refresh(adapterLog)
            }

        }

        binding.zzFragmentLogRecycler.adapter=adapterLog

        viewModel.refresh(adapterLog)

        return binding.root
    }

}

@HiltViewModel
class ViewModelLog @Inject constructor() :ViewModel()
{
    @Inject lateinit var roomDb: RoomDb

    fun refresh(adapterLog:AdapterLog)
    {
        roomDb.getUsers().asLiveData().observeForever { l->
            adapterLog.submitList(l)
        }
    }

}


