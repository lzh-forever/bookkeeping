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
import com.example.bookkeeping.view.SettingBar
import java.util.*

class RecordFragment : Fragment() {

    lateinit var accountId: UUID
    lateinit var recordType: RecordType

    private val viewModel by lazy { ViewModelProvider(this).get(RecordViewModel::class.java) }

    private var _binding: FragmentRecordBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accountId = UUID.fromString(it.getString(ACCOUNT_ID))
            recordType = it.getSerializable(RECORD_TYPE) as RecordType
            Log.d("database"," recordFragment  id: $accountId  $recordType")
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
        binding.recordBtn.setOnClickListener {
            if (::accountId.isInitialized && ::recordType.isInitialized) {
                val record = Record(
                    date = binding.datePicker.localDate,
                    type = recordType, amount = binding.assertsTv.text.toString().toDouble(),
                    accountId = accountId, id = UUID.randomUUID()
                )
                viewModel.addRecord(record)
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
        const val ACCOUNT_ID = "account_id"
        const val RECORD_TYPE = "record_type"
    }
}