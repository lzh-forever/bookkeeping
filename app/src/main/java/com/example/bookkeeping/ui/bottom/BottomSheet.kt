package com.example.bookkeeping.ui.bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookkeeping.R
import com.example.bookkeeping.data.room.entity.RecordType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet(
    private val selectedType: RecordType,
    private var block: ((RecordType) -> Unit)? = null
) :
    BottomSheetDialogFragment() {

    private lateinit var bottomSheetAdapter: BottomSheetAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_bottom_sheet, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.bottom_sheet_rv)
        bottomSheetAdapter = BottomSheetAdapter(getTransferList(), getBlockForAdapter())
        recyclerView.apply {
            adapter = bottomSheetAdapter
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }

    fun setBlock(block: ((RecordType) -> Unit)?) {
        if (::bottomSheetAdapter.isInitialized) {
            bottomSheetAdapter.setBlock(getBlockForAdapter())
        }
    }

    private fun getTransferList(): List<SheetData> {
        return listOf(
            getSheetData(RecordType.TRANSFER_IN), getSheetData(RecordType.TRANSFER_OUT)
        )
    }

    private fun getSheetData(recordType: RecordType) =
        SheetData(recordType.toString(), recordType, recordType == selectedType)


    private fun getBlockForAdapter(): ((RecordType) -> Unit) = {
        block?.invoke(it)
        dismiss()
    }

}
