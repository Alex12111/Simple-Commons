package com.simplemobiletools.commons.adapters

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.simplemobiletools.commons.R
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.formatSize
import com.simplemobiletools.commons.extensions.getColoredDrawableWithColor
import com.simplemobiletools.commons.extensions.isActivityDestroyed
import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.filepicker_list_item.view.*

class FilepickerItemsAdapter(activity: BaseSimpleActivity, val fileDirItems: List<FileDirItem>, recyclerView: MyRecyclerView,
                             itemClick: (Any) -> Unit) : MyRecyclerViewAdapter(activity, recyclerView, itemClick) {

    private val folderDrawable = activity.resources.getColoredDrawableWithColor(R.drawable.ic_folder, textColor)
    private val fileDrawable = activity.resources.getColoredDrawableWithColor(R.drawable.ic_file, textColor)

    init {
        folderDrawable.alpha = 180
        fileDrawable.alpha = 180
        selectableItemCount = fileDirItems.size
    }

    override fun getActionMenuId() = 0

    override fun prepareItemSelection(view: View) {}

    override fun markItemSelection(select: Boolean, view: View?) {}

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = createViewHolder(R.layout.filepicker_list_item, parent)

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val fileDirItem = fileDirItems[position]
        val view = holder.bindView(fileDirItem, false) { itemView, layoutPosition ->
            setupView(itemView, fileDirItem)
        }
        bindViewHolder(holder, position, view)
    }

    override fun getItemCount() = fileDirItems.size

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {}

    override fun onViewRecycled(holder: MyRecyclerViewAdapter.ViewHolder?) {
        super.onViewRecycled(holder)
        if (!activity.isActivityDestroyed()) {
            Glide.with(activity).clear(holder?.itemView?.list_item_icon)
        }
    }

    private fun setupView(view: View, fileDirItem: FileDirItem) {
        view.apply {
            list_item_name.text = fileDirItem.name
            list_item_name.setTextColor(textColor)

            if (fileDirItem.isDirectory) {
                list_item_icon.setImageDrawable(folderDrawable)
                list_item_details.text = getChildrenCnt(fileDirItem)
            } else {
                val path = fileDirItem.path
                val options = RequestOptions().centerCrop().error(fileDrawable)
                Glide.with(context).load(path).transition(withCrossFade()).apply(options).into(list_item_icon)
                list_item_details.text = fileDirItem.size.formatSize()
            }

            list_item_details.setTextColor(textColor)
        }
    }

    private fun getChildrenCnt(item: FileDirItem): String {
        val children = item.children
        return activity.resources.getQuantityString(R.plurals.items, children, children)
    }
}
