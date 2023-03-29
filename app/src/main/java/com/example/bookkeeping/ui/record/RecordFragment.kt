package com.example.bookkeeping.ui.record

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.bookkeeping.databinding.FragmentRecordBinding
import com.example.bookkeeping.ui.account.AccountDetailFragment
import com.example.bookkeeping.ui.bottom.BottomSheet
import com.example.bookkeeping.util.getFormattedDouble
import com.example.bookkeeping.util.setNavigationResult
import com.example.bookkeeping.util.showArgsExceptionToast
import com.example.bookkeeping.view.SettingBar
import java.util.*

class RecordFragment : Fragment() {


    private lateinit var account: Account
    private var from = FROM_DETAIL
    private var record: Record? = null

    private val viewModel by lazy { ViewModelProvider(this).get(RecordViewModel::class.java) }

    private var _binding: FragmentRecordBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                account = it.getParcelable(ACCOUNT) ?: throw Exception("account is null")
                record = it.getParcelable(RECORD)
                val recordType = record?.type ?: it.getSerializable(RECORD_TYPE) as RecordType
                viewModel.setRecordType(recordType)
                from = it.getInt(FROM)
                Log.d("database", " recordFragment  $account  $recordType")
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
        _binding = FragmentRecordBinding.inflate(inflater, container, false)

        initSettingBar()
        initRecordDetail()
        initClickListener()
        buttonEnabledObserve()


        return binding.root
    }

    private fun initRecordDetail() {
        record?.let {
            binding.datePicker.updateDate(it.date)
            binding.assetTv.setText(getFormattedDouble(it.amount))
            if (it.type.isTransferType()) {
                binding.transferTv.text = it.type.toString()
            }
        }
    }


    private fun initClickListener() {
        if (viewModel.recordTypeLiveData.value == null) {
            return
        }
        if (record == null) {
            //添加record
            binding.recordBtn.visibility = View.VISIBLE
            binding.recordBtn.setOnClickListener {
                val record = Record(
                    date = binding.datePicker.localDate,
                    type = viewModel.recordTypeLiveData.value!!,
                    amount = binding.assetTv.text.toString().toDouble(),
                    accountId = account.id,
                    id = UUID.randomUUID()
                )
                viewModel.addRecord(record, account) {
                    findNavController().navigateUp()
                }
            }
        } else {
            //更新record
            binding.deleteBtn.visibility = View.VISIBLE
            binding.deleteBtn.setOnClickListener {
                record?.let {
                    viewModel.deleteRecord(it, account) {
                        findNavController().navigateUp()
                    }
                }
            }
        }


        if (viewModel.recordTypeLiveData.value!!.isTransferType()) {
            binding.transferCard.visibility = View.VISIBLE
            binding.transferCard.setOnClickListener {
                val bottomSheet = BottomSheet(viewModel.recordTypeLiveData.value!!) {
                    viewModel.setRecordType(it)
                }
                activity?.supportFragmentManager?.let {
                    bottomSheet.show(it, bottomSheet.tag)
                }
            }
            viewModel.recordTypeLiveData.observe(viewLifecycleOwner) {
                binding.transferTv.text = it.toString()
            }
        }
    }

    private fun buttonEnabledObserve() {
        if (record != null) {
            binding.recordBtn.visibility = View.GONE
            return
        }
        binding.assetTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onAssetTextChanged(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.completed.observe(viewLifecycleOwner) {
            binding.recordBtn.isEnabled = it
        }
    }

    private fun initSettingBar() {
        with(binding.accountSettingBar) {
            if (record == null) {
                setType(SettingBar.TYPE_WITHOUT_BTN)
            } else {
                setType(SettingBar.TYPE_WITH_SAVE_BTN)
                setSaveBlock {
                    if (binding.assetTv.text.toString().isNotEmpty()) {
                        record?.let {
                            val updateRecord = it.copy(
                                date = binding.datePicker.localDate,
                                type = viewModel.recordTypeLiveData.value!!,
                                amount = binding.assetTv.text.toString().toDouble(),
                                updateTime = System.currentTimeMillis()
                            )
                            viewModel.updateRecord(it, updateRecord, account) {
                                findNavController().navigateUp()
                            }
                        }

                    }
                }
            }
            setText(getSettingBarText())
        }
    }

    private fun getSettingBarText(): CharSequence {
        val titleText = if (record != null) {
            resources.getString(R.string.record_detail)
        } else when (viewModel.recordTypeLiveData.value) {
            RecordType.CURRENT_AMOUNT -> {
                if (from == FROM_INIT) {
                    resources.getText(R.string.init_asset)
                } else {
                    resources.getText(R.string.update)
                }
            }
            RecordType.TRANSFER_IN, RecordType.TRANSFER_OUT -> {
                resources.getText(R.string.title_transfer)
            }
            else -> {
                ""
            }
        }
        return titleText
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "RecordFragment"
        const val ACCOUNT = "account"
        const val RECORD_TYPE = "record_type"
        const val FROM = "from"
        const val FROM_DETAIL = 0
        const val FROM_INIT = 1
        const val RECORD = "record"
    }
}