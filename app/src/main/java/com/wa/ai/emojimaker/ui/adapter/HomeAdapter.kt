package com.wa.ai.emojimaker.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.gms.ads.nativead.NativeAdView
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.Category
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

internal class HomeAdapter(
    private val recyclerViewItems: MutableList<Any>,
    val watchMoreClick: (category: Category) -> Unit,
    val optionClick: (category: String) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    init {
        setHasStableIds(true)
    }
    inner class ItemViewHolder internal constructor(view: View) : ViewHolder(view) {
        val title: TextView
        val img1: ImageView
        val img2: ImageView
        val img3: ImageView
        val img4: ImageView
        val remainingNumber: TextView
        val btnOption: ImageButton
        val btnOpenCategory: ConstraintLayout

        init {
            title = view.findViewById(R.id.tvTitle)
            img1 = view.findViewById(R.id.imgPreview1)
            img2 = view.findViewById(R.id.imgPreview2)
            img3 = view.findViewById(R.id.imgPreview3)
            img4 = view.findViewById(R.id.imgPreview4)
            remainingNumber = view.findViewById(R.id.tvRemainingNumber)
            btnOption = view.findViewById(R.id.btnOption)
            btnOpenCategory = view.findViewById(R.id.btnOpenItem)
        }
    }

    inner class AdViewHolder internal constructor(view: View?) : ViewHolder(view!!)

    override fun getItemCount(): Int {
        return recyclerViewItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % MainActivity.ITEMS_PER_AD == 0) AD_VIEW_TYPE
        else ITEM_VIEW_TYPE
        //return ITEM_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE -> {
                val menuItemLayoutView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_category, parent, false)
                ItemViewHolder(menuItemLayoutView)
            }
            else -> {
                val adLayoutView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.ad_native_container, parent, false)
                AdViewHolder(adLayoutView)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        when (getItemViewType(holder.adapterPosition)) {
            ITEM_VIEW_TYPE -> {

                val itemHolder = holder as ItemViewHolder
                val categoryItem = recyclerViewItems[position] as Category
                val text = "+" + (categoryItem.itemSize - 3).toString()

                itemHolder.apply {
                    btnOption.setOnSafeClick {
                        optionClick(categoryItem.category)
                    }
                    btnOpenCategory.setOnSafeClick {
                        watchMoreClick(categoryItem)
                    }
                    title.text = categoryItem.categoryName
                    remainingNumber.text = text

                    Glide.with(context).load(
                        "file:///android_asset/categories/${categoryItem.category}/" +
                                categoryItem.avatar1
                    ).into(img1)
                    Glide.with(context).load(
                        "file:///android_asset/categories/${categoryItem.category}/" +
                                categoryItem.avatar2
                    ).into(img2)
                    Glide.with(context).load(
                        "file:///android_asset/categories/${categoryItem.category}/" +
                                categoryItem.avatar3
                    ).into(img3)
                    Glide.with(context).load(
                        "file:///android_asset/categories/${categoryItem.category}/" +
                                categoryItem.avatar4
                    ).into(img4)
                }
            }
            else -> {
                val adView = recyclerViewItems[position] as NativeAdView
                val adCardView = holder.itemView as ViewGroup

                if (adCardView.childCount > 0) {
                    adCardView.removeAllViews()
                }
                if (adView.parent != null) {
                    (adView.parent as ViewGroup).removeView(adView)
                }

                // Add the ad to the ad view.
                adCardView.addView(adView)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {
        // A menu item view type.
        private const val ITEM_VIEW_TYPE = 0

        // The ad view type.
        private const val AD_VIEW_TYPE = 1
    }

}