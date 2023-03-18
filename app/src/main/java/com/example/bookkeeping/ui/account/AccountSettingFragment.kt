package com.example.bookkeeping.ui.account

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
import androidx.navigation.ui.navigateUp
import com.example.bookkeeping.R
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.databinding.FragmentAccountSettingBinding
import com.example.bookkeeping.ui.record.RecordFragment
import com.example.bookkeeping.util.showArgsExceptionToast
import com.example.bookkeeping.view.SettingBar
import java.util.UUID


class AccountSettingFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(AccountSettingViewModel::class.java) }

    private var _binding: FragmentAccountSettingBinding? = null
    private val binding
        get() = _binding!!

    private var from: Int = 0
    private lateinit var account: Account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getInt(FROM)
        }
        if (from == FROM_SETTING) {
            try {
                account = arguments?.getParcelable(ACCOUNT) ?: throw Exception("account is null")
            } catch (e: Exception) {
                showArgsExceptionToast(TAG)
                Log.d(TAG, e.toString())
                findNavController().navigateUp()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountSettingBinding.inflate(inflater, container, false)

        initSettingBar()

        if (from == FROM_CREATE) {
            binding.createBtn.visibility = View.VISIBLE
            buttonEnabledObserve()
        } else if (from == FROM_SETTING) {
            binding.nameTv.setText(account.name)
        }

        return binding.root
    }

    private fun initSettingBar() {
        with(binding.accountSettingBar) {
            setType(from)
            if (from == FROM_CREATE) {
                setText(resources.getText(R.string.add_account))
            } else if (from == FROM_SETTING) {
                setText(resources.getText(R.string.account_setting))
                setSaveBlock {
                    viewModel.updateAccount(binding.nameTv.text.toString(), account)
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun buttonEnabledObserve() {
        binding.nameTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameTextChanged(s)
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        viewModel.completed.observe(viewLifecycleOwner) {
            binding.createBtn.isEnabled = it
        }
        binding.createBtn.setOnClickListener {
            viewModel.addAccount(binding.nameTv.text.toString())
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "AccountSettingFragment"
        const val ACCOUNT = "account"
        const val FROM = "from"
        const val FROM_CREATE = SettingBar.TYPE_WITHOUT_BTN
        const val FROM_SETTING = SettingBar.TYPE_WITH_SAVE_BTN
    }
}