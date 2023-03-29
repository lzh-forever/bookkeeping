package com.example.bookkeeping.ui.record

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.databinding.ItemRecordBinding
import com.example.bookkeeping.databinding.ItemRecordHeaderBinding
import com.example.bookkeeping.util.getFormattedDouble

import com.example.bookkeeping.view.SectionedRecyclerViewAdapter

class RecordAdapter(private var list: List<List<Record>>) :
    SectionedRecyclerViewAdapter<RecordAdapter.HeaderViewHolder, RecordAdapter.RecordViewHolder, RecyclerView.ViewHolder?>() {
    private var block: ((Record) -> Unit)? = null

    inner class RecordViewHolder(binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val type = binding.typeTv
        val amount = binding.amountTv
    }

    inner class HeaderViewHolder(binding: ItemRecordHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val date = binding.dateTv
    }


    override fun onCreateSectionHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeaderViewHolder {
        val binding = ItemRecordHeaderBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindSectionHeaderViewHolder(holder: HeaderViewHolder, section: Int) {
        holder.apply {
            date.text = list[section][0].date.toString()
        }
    }


    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindItemViewHolder(holder: RecordViewHolder, section: Int, position: Int) {
        val record = list[section][position]
        holder.apply {
            type.text = record.type.toString()
            amount.setHidableText(getFormattedDouble(record.amount))
        }
        holder.itemView.setOnClickListener {
            block?.invoke(record)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<List<Record>>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setClickBlock(block: ((Record) -> Unit)?){
        this.block = block
    }

    override fun hasFooterInSection(section: Int): Boolean = false
    override fun onCreateSectionFooterViewHolder(parent: ViewGroup, viewType: Int) = null
    override fun onBindSectionFooterViewHolder(holder: RecyclerView.ViewHolder?, section: Int) {}

    override fun getSectionCount(): Int = list.size
    override fun getItemCountForSection(section: Int): Int = list[section].size

}