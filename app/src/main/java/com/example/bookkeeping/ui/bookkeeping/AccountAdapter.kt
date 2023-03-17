package com.example.bookkeeping.ui.bookkeeping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookkeeping.R
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.databinding.ItemAccountBinding
import com.example.bookkeeping.ui.record.RecordFragment
import com.example.bookkeeping.util.getFormattedDouble
import com.example.bookkeeping.util.getFormattedRate

class AccountAdapter(private var accountList: List<Account>) :
    RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(binding: ItemAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.accountName
        val asserts = binding.assertsTv
        val profit = binding.profitTv
        val rate = binding.rateTv
    }

    fun updateList(newList: List<Account>) {
        val diffResult = DiffUtil.calculateDiff(AccountDiffCallback(accountList, newList))
        accountList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accountList[position]
        holder.apply {
            name.text = account.name
            asserts.setHidableText(getFormattedDouble(account.totalAsset))
            profit.setHidableText(getFormattedDouble(account.totalAsset - account.netInvestment))
            rate.text = getFormattedRate(account.rate)
        }
        Log.d("database", account.toString())
        holder.itemView.setOnClickListener {
            if (account.totalAsset > 0) {
//                val bundle = Bundle().apply {
//                    putSerializable(RecordFragment.RECORD_TYPE,RecordType.CURRENT_AMOUNT)
//                    putString(RecordFragment.ACCOUNT_ID,account.id.toString())
//                }
                it.findNavController().navigate(R.id.action_bookkeeping_to_account_detail)
            } else {
                val bundle = Bundle().apply {
                    putSerializable(RecordFragment.RECORD_TYPE,RecordType.CURRENT_AMOUNT)
                    putString(RecordFragment.ACCOUNT_ID,account.id.toString())
                }
                it.findNavController().navigate(R.id.action_bookkeeping_to_record,bundle)
            }
        }
    }

    override fun getItemCount(): Int = accountList.size

    inner class AccountDiffCallback(
        private val oldList: List<Account>,
        private val newList: List<Account>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}