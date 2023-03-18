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
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.databinding.FragmentRecordBinding
import com.example.bookkeeping.ui.account.AccountDetailFragment
import com.example.bookkeeping.util.setNavigationResult
import com.example.bookkeeping.util.showArgsExceptionToast
import com.example.bookkeeping.view.SettingBar
import java.util.*

class RecordFragment : Fragment() {

    private lateinit var accountId: UUID
    private lateinit var recordType: RecordType
    private var accountAsserts: String? = null


    private val viewModel by lazy { ViewModelProvider(this).get(RecordViewModel::class.java) }

    private var _binding: FragmentRecordBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                accountId = UUID.fromString(it.getString(ACCOUNT_ID))
                recordType = it.getSerializable(RECORD_TYPE) as RecordType
                accountAsserts = it.getString(ACCOUNT_ASSERTS)
                Log.d("database", " recordFragment  $accountId  $recordType  $accountAsserts")
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
        _binding = FragmentRecordBinding.inflate(inflater, container, false)

        initSettingBar()


        buttonEnabledObserve()

        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        if (accountAsserts.isNullOrEmpty()) {
            //第一次记账
            binding.recordBtn.setOnClickListener {
                val record = Record(
                    date = binding.datePicker.localDate,
                    type = recordType, amount = binding.assertsTv.text.toString().toDouble(),
                    accountId = accountId, id = UUID.randomUUID()
                )
                viewModel.addRecord(record)
                findNavController().navigateUp()
            }
        } else {
            //详情页跳转记账
            binding.assertsTv.hint = accountAsserts
            binding.recordBtn.setOnClickListener {
                val record = Record(
                    date = binding.datePicker.localDate,
                    type = recordType, amount = binding.assertsTv.text.toString().toDouble(),
                    accountId = accountId, id = UUID.randomUUID()
                )
                setNavigationResult(AccountDetailFragment.RESULT_RECORD,record)
                findNavController().navigateUp()
            }
        }
    }

    private fun buttonEnabledObserve() {
        binding.assertsTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onAssertsTextChanged(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.completed.observe(viewLifecycleOwner) {
            binding.recordBtn.isEnabled = it
        }
    }

    private fun initSettingBar() {
        binding.accountSettingBar.setType(SettingBar.TYPE_WITHOUT_BTN)
        binding.accountSettingBar.setText(resources.getText(R.string.init_asserts))
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "RecordFragment"
        const val ACCOUNT_ID = "account_id"
        const val RECORD_TYPE = "record_type"
        const val ACCOUNT_ASSERTS = "account_asserts"
    }
}