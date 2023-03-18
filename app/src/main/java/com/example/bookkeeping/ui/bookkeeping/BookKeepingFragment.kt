package com.example.bookkeeping.ui.bookkeeping

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookkeeping.R
import com.example.bookkeeping.databinding.FragmentBookKeepingBinding
import com.example.bookkeeping.ui.account.AccountSettingFragment

class BookKeepingFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(BookKeepingViewModel::class.java) }

    private var _binding: FragmentBookKeepingBinding? = null

    private val binding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookKeepingBinding.inflate(inflater, container, false)

        binding.accountRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AccountAdapter(emptyList())
        }

        viewModel.accountList.observe(viewLifecycleOwner) {
            val adapter = binding.accountRv.adapter as AccountAdapter
            adapter.updateList(it)
        }

        binding.addAccountBtn.setOnClickListener {
            val bundle = Bundle().apply {
                with(AccountSettingFragment){
                    putInt(FROM, FROM_CREATE)
                }
            }
            findNavController().navigate(R.id.action_bookkeeping_to_account_setting,bundle)
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}