package com.example.bookkeeping.ui.record

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookkeeping.R
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType

import com.example.bookkeeping.databinding.FragmentRecordListBinding
import com.example.bookkeeping.ui.account.AccountSettingFragment
import com.example.bookkeeping.util.showArgsExceptionToast
import com.example.bookkeeping.view.SettingBar


class RecordListFragment : Fragment() {

    private lateinit var account: Account
    private val viewModel by lazy { ViewModelProvider(this).get(RecordListViewModel::class.java) }

    private var _binding: FragmentRecordListBinding? = null
    private val binding
        get() = _binding!!

    lateinit var list: List<List<Record>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                account = it.getParcelable(ACCOUNT) ?: throw Exception("account is null")
            } ?: throw Exception("arguments is null")
        } catch (e: Exception) {
            showArgsExceptionToast(TAG)
            Log.d(TAG, e.toString())
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordListBinding.inflate(inflater, container, false)

        initSettingBar()
        initRecord()

        return binding.root
    }

    private fun initSettingBar() {
        binding.settingBar.setType(SettingBar.TYPE_WITHOUT_BTN)
        binding.settingBar.setText(resources.getText(R.string.record))
    }


    private fun initRecord() {
        viewModel.getRecordById(account.id)
        binding.recordRv.apply {
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.recordListLiveData.observe(viewLifecycleOwner) {
            if (binding.recordRv.adapter == null) {
                val adapter = RecordAdapter(it)
                adapter.setClickBlock { record ->
                    jumpRecordFragment(record)
                }
                binding.recordRv.adapter = adapter
            } else {
                val adapter = binding.recordRv.adapter as RecordAdapter
                adapter.updateList(it)
            }
        }
    }

    private fun jumpRecordFragment(record: Record) {
        if (!::account.isInitialized) {
            return
        }
        val bundle = Bundle().apply {
            with(RecordFragment){
                putParcelable(ACCOUNT, account)
                putParcelable(RECORD, record)
                putInt(FROM, FROM_DETAIL)
            }
        }
        findNavController().navigate(R.id.action_record_list_to_record, bundle)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "RecordListFragment"
        const val ACCOUNT = "account"
    }
}