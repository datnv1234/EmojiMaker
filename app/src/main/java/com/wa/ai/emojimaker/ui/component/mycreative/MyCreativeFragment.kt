package com.wa.ai.emojimaker.ui.component.mycreative

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.adjust.sdk.Adjust
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
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
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.invisible
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible


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

            startActivity(intent)

        }, optionClick = {

        }, delete = {
            deletePkgDialog.pkg = it
            if (!deletePkgDialog.isAdded)
                deletePkgDialog.show(parentFragmentManager, deletePkgDialog.tag)
        })
    }

    private val stickerAdapter: MadeStickerAdapter by lazy {
        MadeStickerAdapter(itemClick = {
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
                DeviceUtils.deletePackage(
                    requireContext(),
                    Constant.INTERNAL_MY_CREATIVE_DIR,
                    pkg.id
                )
                mMainViewModel.removePackage(pkg)
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_my_creative

    override val title: String
        get() = getString(R.string.my_creative)


    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        if (SharedPreferenceHelper.getBoolean(Constant.KEY_IS_CREATE_PACKAGE, false)) {
            binding.tvCreatePackage.invisible()
        }
        binding.btnAddPackage.setOnSafeClick {
            SharedPreferenceHelper.storeBoolean(Constant.KEY_IS_CREATE_PACKAGE, true)
            if (!createPackageDialog.isAdded)
                createPackageDialog.show(parentFragmentManager, createPackageDialog.tag)
        }

    }

    override fun setupData() {
        mMainActivity = activity as MainActivity
        mMainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        mMainViewModel.packageMutableLiveData.observe(this) {
            if (it.isEmpty()) {
                binding.rlNative.gone()
            }

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

        setUpLoadNativeAds()
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

    override fun getViewModel(): Class<MyCreativeViewModel> = MyCreativeViewModel::class.java
    override fun registerOnBackPress() {
    }

    private fun setUpLoadNativeAds() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_MY_CREATIVE)
        ) {
            binding.rlNative.visible()
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            } else {
                loadNativeAds(getString(R.string.native_home))
            }
        } else {
            binding.rlNative.gone()
        }
    }

    private fun loadNativeAds(keyAds: String) {
        if (!DeviceUtils.checkInternetConnection(requireContext()) || !FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_MY_CREATIVE)
        ) {
            binding.rlNative.gone()
            return
        }
        kotlin.runCatching {
            context?.let { context ->
                NativeAdsUtils.instance.loadNativeAds(
                    context,
                    keyAds
                ) { nativeAds ->
                    if (nativeAds != null && isAdded && isVisible) {
                        val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                        NativeAdsUtils.instance.populateNativeAdVideoView(
                            nativeAds,
                            adNativeVideoBinding.root as NativeAdView
                        )
                        binding.frNativeAds.removeAllViews()
                        binding.frNativeAds.addView(adNativeVideoBinding.root)
                    } else {
                        binding.rlNative.gone()
                        FirebaseAnalytics.getInstance(context)
                            .logEvent("e_load_native_ads_select_animal", null)
                    }
                }
            }
        }.onFailure {
            it.printStackTrace()
        }


    }

}