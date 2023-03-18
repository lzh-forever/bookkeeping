package com.example.bookkeeping.ui.account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bookkeeping.R
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.databinding.FragmentAccountDetailBinding
import com.example.bookkeeping.ui.record.RecordFragment
import com.example.bookkeeping.util.clearResult
import com.example.bookkeeping.util.getFormattedDouble
import com.example.bookkeeping.util.getNavigationResult
import com.example.bookkeeping.util.showArgsExceptionToast
import com.example.bookkeeping.view.SettingBar
import java.util.UUID


class AccountDetailFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(AccountDetailViewModel::class.java) }

    private var _binding: FragmentAccountDetailBinding? = null
    private val binding
        get() = _binding!!


    private lateinit var accountId: UUID
    private var mAccount:Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                accountId = UUID.fromString(it.getString(RecordFragment.ACCOUNT_ID))
            }
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

        viewModel.getAccountFlowById(accountId).observe(viewLifecycleOwner){ account ->
            account?.let {
                mAccount = it
                initAccountDetail(it)
            }
        }

        initRecord()
        resultObserve()

        return binding.root
    }

    private fun initSettingBar() {
        binding.settingBar.setType(SettingBar.TYPE_WITH_SETTING_BTN)
        binding.settingBar.setText(resources.getText(R.string.account_detail))
        binding.settingBar.setSettingBlock {
            val bundle = Bundle().apply{
                with(AccountSettingFragment){
                    putParcelable(ACCOUNT,mAccount)
                    putInt(FROM, FROM_SETTING)
                }
            }
            findNavController().navigate(R.id.action_account_detail_to_account_setting,bundle)
        }
    }

    private fun initAccountDetail(account:Account) {
        binding.nameTv.setText(account.name)
        with(binding.assetLayout) {
            assetTv.setHidableText(getFormattedDouble(account.totalAsset))
            transferBtn.setOnClickListener { }
            updateBtn.setOnClickListener {
                val bundle = Bundle().apply {
                    putString(RecordFragment.ACCOUNT_ID, account.id.toString())
                    putSerializable(RecordFragment.RECORD_TYPE, RecordType.CURRENT_AMOUNT)
                    putString(
                        RecordFragment.ACCOUNT_ASSET,
                        getFormattedDouble(account.totalAsset)
                    )
                }
                findNavController().navigate(R.id.action_account_detail_to_record, bundle)
            }
        }
        with(binding.profitLayout) {
            profitTv.setHidableText(getFormattedDouble(account.totalAsset - account.netInvestment))
            // TODO: 收益率
        }
    }

    private fun initRecord() {

    }

    private fun resultObserve() {
        getNavigationResult<Record>(RESULT_RECORD)?.observe(viewLifecycleOwner) { record ->
            if (record.accountId == accountId) {
                viewModel.updateAsset(record, mAccount)
                clearResult<Record>(RESULT_RECORD)
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "AccountDetailFragment"
        const val ACCOUNT_ID = "account_id"
        const val RESULT_RECORD = "result_record"
    }
}