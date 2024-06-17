package com.wa.ai.emojimaker.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.gms.ads.AdView
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.Category
import com.wa.ai.emojimaker.ui.component.main.MainActivity

internal class HomeAdapter(
    private val context: Context,
    private val recyclerViewItems: List<Any>
) : RecyclerView.Adapter<ViewHolder>() {

    inner class ItemViewHolder internal constructor(view: View) : ViewHolder(view) {
        val title: TextView
        val img1: ImageView
        val img2: ImageView
        val img3: ImageView
        val img4: ImageView
        val remainingNumber: TextView

        init {
            title = view.findViewById(R.id.tvTitle)
            img1 = view.findViewById(R.id.imgPreview1)
            img2 = view.findViewById(R.id.imgPreview2)
            img3 = view.findViewById(R.id.imgPreview3)
            img4 = view.findViewById(R.id.imgPreview4)
            remainingNumber = view.findViewById(R.id.tvRemainingNumber)
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
        return when (viewType) {
            ITEM_VIEW_TYPE -> {
                val menuItemLayoutView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_category, parent, false)
                ItemViewHolder(menuItemLayoutView)
            }
            else -> {
                val bannerLayoutView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.banner_ad_container, parent, false)
                AdViewHolder(bannerLayoutView)
            }
        }
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE -> {
                val itemHolder = holder as ItemViewHolder
                val categoryItem = recyclerViewItems[position] as Category

                val text = "+" + (categoryItem.itemSize - 3).toString()

                // Add the menu item details to the menu item view.
                itemHolder.title.text = categoryItem.categoryName
                itemHolder.img1.setImageBitmap(categoryItem.avatar1)
                itemHolder.img2.setImageBitmap(categoryItem.avatar2)
                itemHolder.img3.setImageBitmap(categoryItem.avatar3)
                itemHolder.img4.setImageBitmap(categoryItem.avatar4)
                itemHolder.remainingNumber.text = text
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