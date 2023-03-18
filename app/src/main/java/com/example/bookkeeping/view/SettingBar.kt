package com.example.bookkeeping.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.bookkeeping.R


class SettingBar(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs),
    View.OnClickListener {
    private val settingBtn: ImageButton
    private val backBtn: ImageButton
    private val saveBtn: Button
    private val title: TextView
    private var settingBlock: (() -> Unit)? = null
    private var saveBlock: (() -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_account_setting_bar, this)
        settingBtn = view.findViewById<ImageButton>(R.id.setting_btn)
            .apply { setOnClickListener(this@SettingBar) }
        backBtn = view.findViewById<ImageButton>(R.id.back_btn)
            .apply { setOnClickListener(this@SettingBar) }
        saveBtn =
            view.findViewById<Button>(R.id.save_btn).apply { setOnClickListener(this@SettingBar) }
        title = view.findViewById(R.id.title_tv)
    }

    override fun onClick(v: View?) {
        when (v) {
            backBtn -> {
                findNavController().navigateUp()
            }
            saveBtn -> {
                saveBlock?.invoke()
            }
            settingBtn -> {
                settingBlock?.invoke()
            }
        }
    }

    fun setType(type: Int) {
        when (type) {
            TYPE_WITHOUT_BTN -> {
                saveBtn.visibility = View.GONE
                settingBtn.visibility = View.GONE
            }
            TYPE_WITH_SAVE_BTN -> {
                saveBtn.visibility = View.VISIBLE
                settingBtn.visibility = View.GONE
            }
            TYPE_WITH_SETTING_BTN -> {
                saveBtn.visibility = View.GONE
                settingBtn.visibility = View.VISIBLE
            }
        }
    }

    fun setText(s: CharSequence) {
        title.text = s
    }

    fun setSettingBlock(block: (() -> Unit)? = null) {
        settingBlock = block
    }

    fun setSaveBlock(block: (() -> Unit)? = null) {
        saveBlock = block
    }

    companion object {
        const val TYPE_WITHOUT_BTN = 0
        const val TYPE_WITH_SAVE_BTN = 1
        const val TYPE_WITH_SETTING_BTN = 2
    }

}