package com.example.bookkeeping.ui.account

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
import com.example.bookkeeping.databinding.FragmentAccountDetailBinding
import com.example.bookkeeping.ui.record.RecordAdapter
import com.example.bookkeeping.ui.record.RecordFragment
import com.example.bookkeeping.ui.record.RecordListFragment
import com.example.bookkeeping.util.*
import com.example.bookkeeping.view.SettingBar
import java.util.UUID


class AccountDetailFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(AccountDetailViewModel::class.java) }

    private var _binding: FragmentAccountDetailBinding? = null
    private val binding
        get() = _binding!!


    private lateinit var accountId: UUID
    private lateinit var mAccount: Account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                accountId = UUID.fromString(it.getString(ACCOUNT_ID))
            } ?: throw NullPointerException("arguments is null")
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
        _binding = FragmentAccountDetailBinding.inflate(inflater, container, false)

        initSettingBar()
        observeAccountFlow()
        initRecord()
        initClickListener()

        return binding.root
    }

    private fun observeAccountFlow() {
        viewModel.getAccountFlowById(accountId).observe(viewLifecycleOwner) { account ->
            account?.let {
                mAccount = it
                updateAccountDetail()
            }
        }
    }

    private fun initSettingBar() {
        binding.settingBar.setType(SettingBar.TYPE_WITH_SETTING_BTN)
        binding.settingBar.setText(resources.getText(R.string.account_detail))
        binding.settingBar.setSettingBlock {
            val bundle = Bundle().apply {
                with(AccountSettingFragment) {
                    putParcelable(ACCOUNT, mAccount)
                    putInt(FROM, FROM_SETTING)
                }
            }
            findNavController().navigate(R.id.action_account_detail_to_account_setting, bundle)
        }
    }

    private fun initClickListener() {
        with(binding.assetLayout) {
            transferBtn.setOnClickListener {
                jumpRecordFragment(RecordType.TRANSFER_IN)
            }
            updateBtn.setOnClickListener {
                jumpRecordFragment(RecordType.CURRENT_AMOUNT)
            }
        }
    }

    private fun updateAccountDetail() {
        binding.nameTv.setText(mAccount.name)
        binding.assetLayout.assetTv.setHidableText(getFormattedDouble(mAccount.totalAsset))
        with(binding.profitLayout) {
            profitTv.setHidableText(getFormattedDouble(mAccount.totalAsset - mAccount.netInvestment))
            rateTv.text = getFormattedRate(mAccount.rate)
        }
    }

    private fun initRecord() {
        viewModel.getRecordById(accountId)
        binding.recordRv.apply {
            layoutManager = LinearLayoutManager(context)
            val adapter = RecordAdapter(emptyList())
            adapter.setClickBlock { record ->
                jumpRecordFragment(record)
            }
            this.adapter = adapter
            isNestedScrollingEnabled = false
        }
        viewModel.getRecordFlowById(accountId).observe(viewLifecycleOwner) {
            val adapter = binding.recordRv.adapter as RecordAdapter
            adapter.updateList(it)
        }
        binding.moreTv.setOnClickListener {
            jumpRecordListFragment()
        }

        viewModel.recordListLiveData.observe(viewLifecycleOwner) {
            Log.d(TAG, it.toString())
        }

        viewModel.rateLiveData.observe(viewLifecycleOwner) {
            Log.d(TAG, it.toString())
            binding.chartLayout.setData(getChartData(it))
        }

    }

    private fun jumpRecordFragment(recordType: RecordType) {
        if (!::mAccount.isInitialized) {
            return
        }
        val bundle = Bundle().apply {
            with(RecordFragment) {
                putParcelable(ACCOUNT, mAccount)
                putSerializable(RECORD_TYPE, recordType)
                putInt(FROM, FROM_DETAIL)
            }
        }
        findNavController().navigate(R.id.action_account_detail_to_record, bundle)
    }

    private fun jumpRecordFragment(record: Record) {
        if (!::mAccount.isInitialized) {
            return
        }
        val bundle = Bundle().apply {
            with(RecordFragment) {
                putParcelable(ACCOUNT, mAccount)
                putParcelable(RECORD, record)
                putInt(FROM, FROM_DETAIL)
            }
        }
        findNavController().navigate(R.id.action_account_detail_to_record, bundle)
    }

    private fun jumpRecordListFragment() {
        if (!::mAccount.isInitialized) {
            return
        }
        val bundle = Bundle().apply {
            with(RecordListFragment) {
                putParcelable(ACCOUNT, mAccount)
            }
        }
        findNavController().navigate(R.id.action_account_detail_to_record_list, bundle)
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "AccountDetailFragment"
        const val ACCOUNT_ID = "account_id"
    }
}