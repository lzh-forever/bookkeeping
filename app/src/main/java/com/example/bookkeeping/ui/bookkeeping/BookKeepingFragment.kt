package com.example.bookkeeping.ui.bookkeeping

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookkeeping.R
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.databinding.FragmentBookKeepingBinding

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

        lifecycleScope.launchWhenCreated {
            Repository.getAccountList()
        }

        binding.accountRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AccountAdapter(emptyList())
        }

        viewModel.accountList.observe(viewLifecycleOwner) {
            val adapter = binding.accountRv.adapter as AccountAdapter
            adapter.updateList(it)
            Log.d("flow","collect")
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}