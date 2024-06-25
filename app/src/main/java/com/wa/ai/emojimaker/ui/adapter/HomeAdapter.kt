package com.wa.ai.emojimaker.ui.adapter

import android.content.Context
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
import com.google.android.gms.ads.AdView
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.Category
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

internal class HomeAdapter(
    private val context: Context,
    private val recyclerViewItems: List<Any>,
    val watchMoreClick: (category: Category) -> Unit,
    val optionClick: (category: String) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

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
//        return if (position % MainActivity.ITEMS_PER_AD == 0) BANNER_AD_VIEW_TYPE
//        else ITEM_VIEW_TYPE
        return ITEM_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val menuItemLayoutView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
        return ItemViewHolder(menuItemLayoutView)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
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
                val bannerHolder = holder as AdViewHolder
                val adView = recyclerViewItems[position] as AdView
                val adCardView = bannerHolder.itemView as ViewGroup
                // The AdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // AdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled AdViewHolder.
                if (adCardView.childCount > 0) {
                    adCardView.removeAllViews()
                }
                if (adView.parent != null) {
                    (adView.parent as ViewGroup).removeView(adView)
                }

                // Add the banner ad to the ad view.
                adCardView.addView(adView)
            }
        }
    }


    companion object {
        // A menu item view type.
        private const val ITEM_VIEW_TYPE = 0

        // The banner ad view type.
        private const val BANNER_AD_VIEW_TYPE = 1
    }

}