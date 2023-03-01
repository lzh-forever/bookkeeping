package com.example.bookkeeping.ui.bookkeeping

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookkeeping.R

class BookKeepingFragment : Fragment() {

    companion object {
        fun newInstance() = BookKeepingFragment()
    }

    private lateinit var viewModel: BookKeepingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(BookKeepingViewModel::class.java)
        return inflater.inflate(R.layout.book_keeping_fragment, container, false)
    }


}