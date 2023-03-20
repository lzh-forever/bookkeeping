package com.example.bookkeeping.ui.bottom


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.databinding.ItemBottomSheetBinding

class BottomSheetAdapter(
    private var bottomList: List<SheetData>,
    private var block: ((RecordType) -> Unit)? = null
) :
    RecyclerView.Adapter<BottomSheetAdapter.BottomSheetViewHolder>() {

    inner class BottomSheetViewHolder(binding: ItemBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val typeTv = binding.itemTv
        val checked = binding.checked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val binding =
            ItemBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val data = bottomList[position]
        holder.apply {
            typeTv.text = data.text
            checked.visibility = if (data.selected) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        holder.itemView.setOnClickListener {
            block?.invoke(data.type)
        }
    }

    override fun getItemCount(): Int = bottomList.size

    fun setBlock(block: ((RecordType) -> Unit)?) {
        this.block = block
    }

}