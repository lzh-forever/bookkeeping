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
import com.example.bookkeeping.databinding.FragmentAccountSettingBinding


class AccountSettingFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(AccountSettingViewModel::class.java) }

    private var _binding: FragmentAccountSettingBinding? = null

    private val binding
        get() = _binding!!

    private var name: String? = null
    private var from: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ACCOUNT_NAME)
            from = it.getInt(FROM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountSettingBinding.inflate(inflater, container, false)

        binding.nameTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameTextChanged(s)
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        if (from == FROM_CREATE) {
            binding.accountSettingBar.saveBtn.visibility = View.GONE
            viewModel.completed.observe(viewLifecycleOwner) {
                binding.createBtn.isEnabled = it
            }
            binding.createBtn.setOnClickListener {
                viewModel.addAccount(binding.nameTv.text.toString())
                findNavController().navigateUp()
            }
        } else if (from == FROM_DETAIL) {
            binding.createBtn.visibility = View.GONE
            binding.nameTv.setText(name)
            binding.accountSettingBar.saveBtn.setOnClickListener {
                // TODO:  
            }
        }

        binding.accountSettingBar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ACCOUNT_NAME = "account_name"
        const val FROM = "from"
        const val FROM_CREATE = 0
        const val FROM_DETAIL = 1
    }
}