package com.wa.ai.emojimaker.ui.mycreative

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.FragmentMyCreativeBinding
import com.wa.ai.emojimaker.ui.adapter.CreativeAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment

class MyCreativeFragment : BaseBindingFragment<FragmentMyCreativeBinding, MyCreativeViewModel>() {

    private val creativeAdapter: CreativeAdapter by lazy { CreativeAdapter(itemClick = {

    })}

    companion object {
        fun newInstance() = MyCreativeFragment()
    }
    override val layoutId: Int
        get() = R.layout.fragment_my_creative

    override val title: String
        get() = getString(R.string.app_name)

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

    }

    override fun setupData() {
        viewModel.getItemSticker(requireContext())
        viewModel.stickerMutableLiveData.observe(this) {
            creativeAdapter.submitList(it.toMutableList())
        }
        binding.rvSticker.adapter = creativeAdapter
    }

    override fun getViewModel(): Class<MyCreativeViewModel> = MyCreativeViewModel::class.java
    override fun registerOnBackPress() {
    }
}