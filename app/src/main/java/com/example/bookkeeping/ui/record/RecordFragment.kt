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
import com.example.bookkeeping.util.setNavigationResult
import com.example.bookkeeping.util.showArgsExceptionToast
import com.example.bookkeeping.view.SettingBar
import java.util.*

class RecordFragment : Fragment() {


    private lateinit var account: Account

    private val viewModel by lazy { ViewModelProvider(this).get(RecordViewModel::class.java) }

    private var _binding: FragmentRecordBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                account = it.getParcelable(ACCOUNT) ?: throw Exception("account is null")
                val recordType = it.getSerializable(RECORD_TYPE) as RecordType
                viewModel.recordTypeLiveData.value = recordType
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
        buttonEnabledObserve()
        initClickListener()
        liveDataObserve()


        return binding.root
    }

    private fun liveDataObserve() {
        viewModel.recordTypeLiveData.observe(viewLifecycleOwner) {
            binding.transferTv.text = it.toString()
            Log.d("recordViewModel", "observe  $it")
        }
    }

    private fun initClickListener() {
        if (viewModel.recordTypeLiveData.value == null) {
            return
        }
        binding.recordBtn.setOnClickListener {
            val record = Record(
                date = binding.datePicker.localDate,
                type = viewModel.recordTypeLiveData.value!!,
                amount = binding.assetTv.text.toString().toDouble(),
                accountId = account.id,
                id = UUID.randomUUID()
            )
            viewModel.addRecord(record, account)
            findNavController().navigateUp()
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
        }
    }

    private fun buttonEnabledObserve() {
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
        binding.accountSettingBar.setType(SettingBar.TYPE_WITHOUT_BTN)
        binding.accountSettingBar.setText(resources.getText(R.string.init_asset))
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "RecordFragment"
        const val ACCOUNT = "account"
        const val RECORD_TYPE = "record_type"
    }
}