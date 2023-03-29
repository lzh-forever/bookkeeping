package com.example.bookkeeping.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * An extension to RecyclerView.Adapter to provide sections with headers and footers to a
 * RecyclerView. Each section can have an arbitrary number of items.
 *
 * @param <H> Class extending RecyclerView.ViewHolder to hold and bind the header view
 * @param <VH> Class extending RecyclerView.ViewHolder to hold and bind the items view
 * @param <F> Class extending RecyclerView.ViewHolder to hold and bind the footer view
</F></VH></H> */
abstract class SectionedRecyclerViewAdapter<H : RecyclerView.ViewHolder?, VH : RecyclerView.ViewHolder?, F : RecyclerView.ViewHolder?> :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var sectionForPosition: IntArray? = null
    private var positionWithinSection: IntArray? = null
    private var isHeader: BooleanArray? = null
    private var isFooter: BooleanArray? = null

    /**
     * Returns the sum of number of items for each section plus headers and footers if they
     * are provided.
     */
    private var itemCount = 0


    init {
        registerAdapterDataObserver(SectionDataObserver())
    }

    final override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        setupIndices()
    }

    private fun setupIndices() {
        itemCount = countItems()
        allocateAuxiliaryArrays(itemCount)
        precomputeIndices()
    }

    private fun countItems(): Int {
        var count = 0
        val sections = getSectionCount()
        for (i in 0 until sections) {
            count += 1 + getItemCountForSection(i) + if (hasFooterInSection(i)) 1 else 0
        }
        return count
    }

    private fun precomputeIndices() {
        val sections = getSectionCount()
        var index = 0
        for (i in 0 until sections) {
            setPrecomputedItem(index, true, false, i, 0)
            index++
            for (j in 0 until getItemCountForSection(i)) {
                setPrecomputedItem(index, false, false, i, j)
                index++
            }
            if (hasFooterInSection(i)) {
                setPrecomputedItem(index, false, true, i, 0)
                index++
            }
        }
    }

    private fun allocateAuxiliaryArrays(count: Int) {
        sectionForPosition = IntArray(count)
        positionWithinSection = IntArray(count)
        isHeader = BooleanArray(count)
        isFooter = BooleanArray(count)
    }

    private fun setPrecomputedItem(
        index: Int,
        isHeader: Boolean,
        isFooter: Boolean,
        section: Int,
        position: Int
    ) {
        this.isHeader!![index] = isHeader
        this.isFooter!![index] = isFooter
        sectionForPosition!![index] = section
        positionWithinSection!![index] = position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder? = if (isSectionHeaderViewType(viewType)) {
            onCreateSectionHeaderViewHolder(parent, viewType)
        } else if (isSectionFooterViewType(viewType)) {
            onCreateSectionFooterViewHolder(parent, viewType)
        } else {
            onCreateItemViewHolder(parent, viewType)
        }
        return viewHolder!!
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val section = sectionForPosition!![position]
        val index = positionWithinSection!![position]
        if (isSectionHeaderPosition(position)) {
            onBindSectionHeaderViewHolder(holder as H, section)
        } else if (isSectionFooterPosition(position)) {
            onBindSectionFooterViewHolder(holder as F, section)
        } else {
            onBindItemViewHolder(holder as VH, section, index)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (sectionForPosition == null) {
            setupIndices()
        }
        val section = sectionForPosition!![position]
        val index = positionWithinSection!![position]
        return if (isSectionHeaderPosition(position)) {
            getSectionHeaderViewType(section)
        } else if (isSectionFooterPosition(position)) {
            getSectionFooterViewType(section)
        } else {
            getSectionItemViewType(section, index)
        }
    }

    override fun getItemCount(): Int = itemCount

    protected fun getSectionHeaderViewType(section: Int): Int {
        return TYPE_SECTION_HEADER
    }

    protected fun getSectionFooterViewType(section: Int): Int {
        return TYPE_SECTION_FOOTER
    }

    protected fun getSectionItemViewType(section: Int, position: Int): Int {
        return TYPE_ITEM
    }

    /**
     * Returns true if the argument position corresponds to a header
     */
    fun isSectionHeaderPosition(position: Int): Boolean {
        if (isHeader == null) {
            setupIndices()
        }
        return isHeader!![position]
    }

    /**
     * Returns true if the argument position corresponds to a footer
     */
    fun isSectionFooterPosition(position: Int): Boolean {
        if (isFooter == null) {
            setupIndices()
        }
        return isFooter!![position]
    }

    protected fun isSectionHeaderViewType(viewType: Int): Boolean {
        return viewType == TYPE_SECTION_HEADER
    }

    protected fun isSectionFooterViewType(viewType: Int): Boolean {
        return viewType == TYPE_SECTION_FOOTER
    }

    /**
     * Returns the number of sections in the RecyclerView
     */
    protected abstract fun getSectionCount(): Int

    /**
     * Returns the number of items for a given section
     */
    protected abstract fun getItemCountForSection(section: Int): Int

    /**
     * Returns true if a given section should have a footer
     */
    protected abstract fun hasFooterInSection(section: Int): Boolean

    /**
     * Creates a ViewHolder of class H for a Header
     */
    protected abstract fun onCreateSectionHeaderViewHolder(parent: ViewGroup, viewType: Int): H

    /**
     * Creates a ViewHolder of class F for a Footer
     */
    protected abstract fun onCreateSectionFooterViewHolder(parent: ViewGroup, viewType: Int): F

    /**
     * Creates a ViewHolder of class VH for an Item
     */
    protected abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * Binds data to the header view of a given section
     */
    protected abstract fun onBindSectionHeaderViewHolder(holder: H, section: Int)

    /**
     * Binds data to the footer view of a given section
     */
    protected abstract fun onBindSectionFooterViewHolder(holder: F, section: Int)

    /**
     * Binds data to the item view for a given position within a section
     */
    protected abstract fun onBindItemViewHolder(holder: VH, section: Int, position: Int)
    internal inner class SectionDataObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            setupIndices()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            setupIndices()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            setupIndices()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            setupIndices()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            setupIndices()
        }
    }

    companion object {
        protected const val TYPE_SECTION_HEADER = -1
        protected const val TYPE_SECTION_FOOTER = -2
        protected const val TYPE_ITEM = -3
    }
}