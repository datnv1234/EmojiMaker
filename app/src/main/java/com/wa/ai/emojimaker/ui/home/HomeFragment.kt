package com.wa.ai.emojimaker.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.databinding.FragmentHomeBinding
import com.wa.ai.emojimaker.ui.adapter.CategoryAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.SharePackageDialog
import com.wa.ai.emojimaker.ui.emojimaker.EmojiMakerActivity
import com.wa.ai.emojimaker.ui.main.MainActivity
import com.wa.ai.emojimaker.ui.showstickers.ShowStickersActivity

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {

    private val mContext = context
    private val sharePackageDialog : SharePackageDialog by lazy {
        SharePackageDialog().apply {
            addToWhatsapp = {

            }

            addToTelegram = {

            }

            share = {

            }

            download = {

            }
        }
    }
    private val categoryAdapter : CategoryAdapter by lazy {
        CategoryAdapter(optionClick = {
            sharePackageDialog.show(parentFragmentManager, sharePackageDialog.tag)
        }, watchMoreClick = {
            val intent = Intent(requireContext(), ShowStickersActivity::class.java)
            intent.putExtra("category", it.category.toString())
            intent.putExtra("category_name", it.categoryName)
            startActivity(intent)
        })
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java
    override fun registerOnBackPress() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_home
    override val title: String
        get() = getString(R.string.app_name)

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        binding.btnCreateSticker.setOnClickListener {
            startActivity(Intent(context, EmojiMakerActivity::class.java))
        }
    }

    override fun setupData() {
        viewModel.getCategoryList()
        viewModel.categoriesMutableLiveData.observe(this) {
            categoryAdapter.submitList(it.toMutableList())
        }
        Log.d(TAG, "setupData: " + viewModel.categoriesMutableLiveData.value?.size)
        binding.rvCategory.adapter = categoryAdapter
    }

}