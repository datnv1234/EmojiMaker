package com.wa.ai.emojimaker.ui.component.mycreative

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.adjust.sdk.Adjust
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.AdNativeContentBinding
import com.wa.ai.emojimaker.databinding.FragmentMyCreativeBinding
import com.wa.ai.emojimaker.functions.Utils
import com.wa.ai.emojimaker.ui.adapter.CreativeAdapter
import com.wa.ai.emojimaker.ui.adapter.MadeStickerHorizontalAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.ConfirmDialog
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.ui.component.main.MainViewModel
import com.wa.ai.emojimaker.ui.component.showstickers.ShowStickersActivity
import com.wa.ai.emojimaker.ui.dialog.CreatePackageDialog
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.FileUtils
import com.wa.ai.emojimaker.utils.FileUtils.copyFileToCache
import com.wa.ai.emojimaker.utils.FileUtils.getUriForFile
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.invisible
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible
import java.io.File

class MyCreativeFragment : BaseBindingFragment<FragmentMyCreativeBinding, MyCreativeViewModel>() {

    private lateinit var mMainActivity: MainActivity
    private lateinit var mMainViewModel: MainViewModel

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_MY_CREATIVE)

    private val stickerAdapter: MadeStickerHorizontalAdapter by lazy {
        MadeStickerHorizontalAdapter()
    }

    private val creativeAdapter: CreativeAdapter by lazy {
        CreativeAdapter(itemClick = {
            val intent = Intent(requireContext(), ShowStickersActivity::class.java)
            intent.putExtra("local", true)
            intent.putExtra("category", it.id)
            intent.putExtra("category_name", it.getName())
            intent.putExtra("category_size", it.itemSize)

            startActivity(intent)

        }, optionClick = {

        }, delete = {
            deletePkgDialog.pkg = it
            if (!deletePkgDialog.isAdded)
                deletePkgDialog.show(parentFragmentManager, deletePkgDialog.tag)
        }, rename = {
            if (!renamePackageDialog.isAdded) {
                renamePackageDialog.oldName = it.getName()
                renamePackageDialog.createNew = false
                renamePackageDialog.show(parentFragmentManager, createPackageDialog.tag)
            }
        }, share = { cate ->
            shareCreativeSticker(cate)
        })
    }

    private val renamePackageDialog: CreatePackageDialog by lazy {
        CreatePackageDialog(requireActivity()).apply {
            confirm = {
                creativeAdapter.updateFolderName(it.getName())
            }
        }
    }

    private val createPackageDialog: CreatePackageDialog by lazy {
        CreatePackageDialog(requireActivity()).apply {
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
                binding.llEmpty.visible()
                binding.rvSticker.gone()
            } else {
                loadNativeAd()
                creativeAdapter.submitList(it.toMutableList())
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

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
        mMainViewModel.getPackage(requireContext())
        mMainActivity.binding.titleToolbar.visible()
        mMainActivity.binding.imgToolbar.gone()
        mMainActivity.binding.titleToolbar.text = title
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    override fun getViewModel(): Class<MyCreativeViewModel> = MyCreativeViewModel::class.java
    override fun registerOnBackPress() {

    }

    private fun shareCreativeSticker(category: String) {
        getCreativeStickerUri(category)
        if (viewModel.stickerUri.size != 0)
            AppUtils.shareMultipleImages(requireContext(), viewModel.stickerUri.toList())
    }

    private fun getCreativeStickerUri(category: String) {
        viewModel.stickerUri.clear()
        val cw = ContextWrapper(requireContext())
        val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
        val files = directory.listFiles()      // Get packages
        if (files != null) {                    //package's size > 0
            for (file in files) {
                if (file.isDirectory && file.name.equals(category)) {
                    val stickers = file.listFiles()
                    if (stickers != null) {
                        for (sticker in stickers) {
                            viewModel.stickerUri.add(
                                FileUtils.getUriForFile(requireContext(), copyFileToCache(requireContext(), sticker))
                            )
                        }
                    }
                    break
                }
            }
        }
    }


    private fun loadNativeAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_MY_CREATIVE)
        ) {
            loadNativeAds(keyNative)
        } else {
            binding.rlNative.visibility = View.GONE
        }
    }

    private fun loadNativeAds(keyAds: String) {
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                requireActivity(),
                keyAds,
                { nativeAds ->
                    if (nativeAds != null && isAdded && isVisible) {
                        binding.rlNative.visible()
                        val adNativeVideoBinding = AdNativeContentBinding.inflate(layoutInflater)
                        NativeAdsUtils.instance.populateNativeAdVideoView(
                            nativeAds,
                            adNativeVideoBinding.root
                        )
                        binding.frNativeAds.removeAllViews()
                        binding.frNativeAds.addView(adNativeVideoBinding.root)
                    }
                },
                {

                }
            )
        }

    }
}