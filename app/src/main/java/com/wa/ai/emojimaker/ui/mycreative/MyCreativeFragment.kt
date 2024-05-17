package com.wa.ai.emojimaker.ui.mycreative

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.FragmentMyCreativeBinding
import com.wa.ai.emojimaker.ui.adapter.CreativeAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.SharePackageDialog
import com.wa.ai.emojimaker.ui.showstickers.ShowStickersActivity
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.visible

class MyCreativeFragment : BaseBindingFragment<FragmentMyCreativeBinding, MyCreativeViewModel>() {

    private val sharePackageDialog : SharePackageDialog by lazy {
        SharePackageDialog().apply {
            addToWhatsapp = {
                toast(getString(R.string.this_function_is_not_supported_yet))
            }

            addToTelegram = {
                toast(getString(R.string.this_function_is_not_supported_yet))
                /*if (viewModel.stickerUri.size != 0) {
                    AppUtils.importToTelegram(requireContext(), viewModel.stickerUri.toList())
                } else {
                    toast("Please wait..!")
                }*/
            }

            share = {
                toast(getString(R.string.this_function_is_not_supported_yet))
                /*if (viewModel.stickerUri.size != 0) {
                    AppUtils.shareMultipleImages(requireContext(), viewModel.stickerUri.toList())
                } else {
                    toast("Please wait..!")
                }*/
            }

            download = {
                toast("Cannot download this category")
            }
        }
    }

    private val creativeAdapter: CreativeAdapter by lazy { CreativeAdapter(requireContext(), itemClick = {
        val intent = Intent(requireContext(), ShowStickersActivity::class.java)
        intent.putExtra("local", true)
        intent.putExtra("category", it.id)
        intent.putExtra("category_name", it.name)
        intent.putExtra("category_size", it.itemSize)
        startActivity(intent)
    }, optionClick = {
        //sharePackageDialog.show(parentFragmentManager, sharePackageDialog.tag)
    }, delete = {

    })
    }

    companion object {
        fun newInstance() = MyCreativeFragment()
    }
    override val layoutId: Int
        get() = R.layout.fragment_my_creative

    override val title: String
        get() = getString(R.string.my_creative)

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

    }

    override fun setupData() {
        viewModel.getItemSticker(requireContext())
        viewModel.stickerMutableLiveData.observe(this) {
            creativeAdapter.submitList(it.toMutableList())
            if (it.isEmpty()) {
                binding.llEmpty.visible()
                binding.rvSticker.gone()
            } else {
                binding.rvSticker.visible()
                binding.llEmpty.gone()
            }
        }
        binding.rvSticker.adapter = creativeAdapter
    }

    override fun getViewModel(): Class<MyCreativeViewModel> = MyCreativeViewModel::class.java
    override fun registerOnBackPress() {

    }


}