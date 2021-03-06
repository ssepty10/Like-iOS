package com.septi.like_iOS

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.septi.like_iOS.models.DialogItem
import kotlinx.android.synthetic.main.fragment_bottom_sheet.view.*
import java.util.*

class LikeiOS(
    private val listener: OnSelectionListener,
    private val title: String,
    private val options: ArrayList<DialogItem>
) : BottomSheetDialogFragment() {

    private lateinit var adapter: DialogAdapter
    private lateinit var mOnSelectionListener: OnSelectionListener

    class Builder(
        private var fragmentManager: FragmentManager? = null,
        private var listener: OnSelectionListener? = null,
        private var title: String? = null,
        private var options: ArrayList<DialogItem> = ArrayList()
    ) {
        fun fragmentManager(fragmentManager: FragmentManager) =
            apply { this.fragmentManager = fragmentManager }

        fun listener(listener: OnSelectionListener) = apply {
            this.listener = listener
        }

        fun addItem(text: String, isRed: Boolean = false) =
            apply {
                options.add(DialogItem(text, isRed))
            }

        fun title(title: String) = apply { this.title = title }

        private fun build() =
            listener?.let {
                title?.let {
                    LikeiOS(
                        listener!!,
                        title!!,
                        options
                    )

                }
            }

        fun buildAndShow() = build()!!.show(fragmentManager!!, UUID.randomUUID().toString())
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.fragment_bottom_sheet, null)
        dialog.setContentView(contentView)
        mOnSelectionListener = listener
        contentView.list_view_dialog.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                mOnSelectionListener.onSelection(options[position].text)
                dismiss()
            }
        adapter = DialogAdapter(context!!, options)
        contentView.tv_dialog_cancel.setOnClickListener {
            dismiss()
        }
        contentView.tv_dialog_title.text = title
        contentView.list_view_dialog.adapter = this.adapter
        adapter.notifyDataSetChanged()

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheet.let { sheet ->
                behavior.peekHeight = sheet.height
                sheet.parent.parent.requestLayout()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MarginLeftRightBottomSheetDialogTheme)
    }

    interface OnSelectionListener {
        fun onSelection(option: String)
    }
}

class DialogAdapter(private var context: Context, private var items: ArrayList<DialogItem>) :
    BaseAdapter() {
    private class ViewHolder(row: View?) {
        var optionName: TextView? = null

        init {
            this.optionName = row?.findViewById(R.id.tv_dialog)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_bottom_sheet, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val option = items[position]
        viewHolder.optionName?.text = option.text
        if (option.isRed) {
            viewHolder.optionName?.setTextColor(ContextCompat.getColor(context, R.color.red_ios))
        } else viewHolder.optionName?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.blue_ios
            )
        )
        return view
    }

    override fun getItem(i: Int): DialogItem {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}