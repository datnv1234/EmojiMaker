package com.wa.ai.emojimaker.ui.component.mycreative

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.adjust.sdk.Adjust
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.databinding.FragmentMyCreativeBinding
import com.wa.ai.emojimaker.ui.adapter.CreativeAdapter
import com.wa.ai.emojimaker.ui.adapter.MadeStickerAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.ConfirmDialog
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.ui.component.main.MainViewModel
import com.wa.ai.emojimaker.ui.component.showstickers.ShowStickersActivity
import com.wa.ai.emojimaker.ui.dialog.CreatePackageDialog
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

@SuppressLint("NotifyDataSetChanged")
class MyCreativeFragment : BaseBindingFragment<FragmentMyCreativeBinding, MyCreativeViewModel>() {

    private lateinit var mMainActivity: MainActivity
    private lateinit var mMainViewModel: MainViewModel

    private val creativeAdapter: CreativeAdapter by lazy {
        CreativeAdapter(requireContext(), itemClick = {
            val intent = Intent(requireContext(), ShowStickersActivity::class.java)
            intent.putExtra("local", true)
            intent.putExtra("category", it.id)
            intent.putExtra("category_name", it.name)
            intent.putExtra("category_size", it.itemSize)
            mMainActivity.openNextScreen {
                startActivity(intent)
            }
            mMainActivity.mFirebaseAnalytics?.logEvent("v_inter_ads_open_${it.name}", null)
        }, optionClick = {
        //sharePackageDialog.show(parentFragmentManager, sharePackageDialog.tag)
        }, delete = {
            deletePkgDialog.pkg = it
            if (!deletePkgDialog.isAdded)
                deletePkgDialog.show(parentFragmentManager, deletePkgDialog.tag)
        })
    }

    private val stickerAdapter : MadeStickerAdapter by lazy {
        MadeStickerAdapter(itemClick = {
            //toast("Clicked")
        })
    }

    /*private val sharePackageDialog : SharePackageDialog by lazy {
        SharePackageDialog().apply {
            addToWhatsapp = {
                toast(getString(R.string.coming_soon))
            }

            addToTelegram = {
                toast(getString(R.string.this_function_is_not_supported_yet))
                *//*if (viewModel.stickerUri.size != 0) {
                    AppUtils.importToTelegram(requireContext(), viewModel.stickerUri.toList())
                } else {
                    toast("Please wait..!")
                }*//*
            }

            share = {
                toast(getString(R.string.this_function_is_not_supported_yet))
                *//*if (viewModel.stickerUri.size != 0) {
                    AppUtils.shareMultipleImages(requireContext(), viewModel.stickerUri.toList())
                } else {
                    toast("Please wait..!")
                }*//*
            }

            download = {
                toast("Cannot download this category")
            }
        }
    }*/

    private val createPackageDialog: CreatePackageDialog by lazy {
        CreatePackageDialog().apply {
            confirm = {
                mMainViewModel.addPackage(it)
            }
        }
    }

    private val deletePkgDialog: ConfirmDialog by lazy {
        ConfirmDialog(getString(R.string.delete), getString(R.string.delete)).apply {
             confirm = { pkg ->
                 DeviceUtils.deletePackage(requireContext(), Constant.INTERNAL_MY_CREATIVE_DIR, pkg.id)
                 mMainViewModel.removePackage(pkg)
             }
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_my_creative

    override val title: String
        get() = getString(R.string.my_creative)


    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        binding.btnAddPackage.setOnSafeClick {
            if (!createPackageDialog.isAdded)
                createPackageDialog.show(parentFragmentManager, createPackageDialog.tag)
        }

    }

    override fun setupData() {
        mMainActivity = activity as MainActivity
        mMainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        mMainViewModel.packageMutableLiveData.observe(this) {
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

        //Get sticker list
        mMainViewModel.stickerMutableLiveData.observe(this) {
            stickerAdapter.submitList(it.toMutableList())
        }
        binding.rvSeeMore.adapter = stickerAdapter
        binding.btnSeeMore.setOnSafeClick {
            val intent = Intent(requireContext(), ShowStickersActivity::class.java)
            intent.putExtra("category", mMainViewModel.suggestCategory)
            intent.putExtra("category_name", Constant.categories[mMainViewModel.suggestCategory])
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        loadAds()
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
        mMainActivity.binding.titleToolbar.text = title
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun loadAds() {
        //setUpLoadInterAds()
    }

    override fun getViewModel(): Class<MyCreativeViewModel> = MyCreativeViewModel::class.java
    override fun registerOnBackPress() {

    }

    private fun setUpLoadInterAds() {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_MY_CREATIVE)) {
            mMainActivity.loadInterAds()
        }
    }

}