package com.example.bookkeeping.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.bookkeeping.R
import com.example.bookkeeping.databinding.FragmentAccountDetailBinding
import com.example.bookkeeping.view.SettingBar


class AccountDetailFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(AccountDetailViewModel::class.java) }

    private var _binding: FragmentAccountDetailBinding? = null
    private val binding
        get() = _binding!!

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("ARG_PARAM1")
            param2 = it.getString("ARG_PARAM2")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDetailBinding.inflate(inflater, container, false)

        binding.settingBar.setType(SettingBar.TYPE_WITH_SETTING_BTN)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ACCOUNT_ID = "account_id"
        const val FROM = "from"
    }
}