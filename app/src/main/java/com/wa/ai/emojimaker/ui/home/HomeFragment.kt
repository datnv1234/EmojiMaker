package com.wa.ai.emojimaker.ui.home

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.google.android.gms.ads.nativead.NativeAdView
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.databinding.FragmentHomeBinding
import com.wa.ai.emojimaker.ui.adapter.CategoryAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.SharePackageDialog
import com.wa.ai.emojimaker.ui.dialog.WaitingDialog
import com.wa.ai.emojimaker.ui.emojimaker.EmojiMakerActivity
import com.wa.ai.emojimaker.ui.main.MainActivity
import com.wa.ai.emojimaker.ui.showstickers.ShowStickersActivity
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.FileUtils
import com.wa.ai.emojimaker.utils.FileUtils.getUriForFile
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.invisible
import com.wa.ai.emojimaker.utils.extention.visible
import timber.log.Timber
import java.io.File

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {

    private val CREATE_STICKER_PACK_ACTION = "org.telegram.messenger.CREATE_STICKER_PACK"
    private val CREATE_STICKER_PACK_EMOJIS_EXTRA = "STICKER_EMOJIS"
    private val CREATE_STICKER_PACK_IMPORTER_EXTRA = "IMPORTER"

    lateinit var mMainActivity: MainActivity

    private val sharePackageDialog : SharePackageDialog by lazy {
        SharePackageDialog().apply {
            addToWhatsapp = {
                toast(getString(R.string.this_function_is_not_supported_yet))
            }

            addToTelegram = { cate ->
                viewModel.stickerUri.clear()
                val assetManager = requireContext().assets
                val listFile = assetManager.list("categories/$cate/")
                if (listFile != null) {                    //package's size > 0
                    for (file in listFile) {
                        val inputStream1 = assetManager.open("categories/$cate/$file")
                        viewModel.stickerUri.add(
                            getUriForFile(requireContext(),
                                FileUtils.copyAssetFileToCache(requireContext(), inputStream1, file)
                            )
                        )
                        inputStream1.close()
                    }
                }
                mMainActivity.openNextScreen {
                    AppUtils.doImport(requireContext(), viewModel.stickerUri)
                }
                mMainActivity.mFirebaseAnalytics?.logEvent("v_inter_ads_import_$cate", null)

            }

            share = { cate ->
                viewModel.stickerUri.clear()
                val assetManager = requireContext().assets
                val listFile = assetManager.list("categories/$cate/")
                if (listFile != null) {                    //package's size > 0
                    for (file in listFile) {
                        val inputStream1 = assetManager.open("categories/$cate/$file")
                        viewModel.stickerUri.add(
                            getUriForFile(requireContext(),
                                FileUtils.copyAssetFileToCache(requireContext(), inputStream1, file)
                            )
                        )
                        inputStream1.close()
                    }
                }
                if (viewModel.stickerUri.size != 0) {
                    mMainActivity.openNextScreen {
                        AppUtils.shareMultipleImages(requireContext(), viewModel.stickerUri.toList())
                    }
                    mMainActivity.mFirebaseAnalytics?.logEvent("v_inter_ads_share_$cate", null)
                }
            }

            download = { cate ->
                mMainActivity.openNextScreen {
                    download(requireContext(), cate)
                }
                mMainActivity.mFirebaseAnalytics?.logEvent("v_inter_ads_download_$cate", null)
            }
        }
    }
    private val categoryAdapter : CategoryAdapter by lazy {
        CategoryAdapter(requireContext(), optionClick = {
            //getUri(it)
            sharePackageDialog.category = it
            sharePackageDialog.show(parentFragmentManager, sharePackageDialog.tag)
        }, watchMoreClick = {
            val intent = Intent(requireContext(), ShowStickersActivity::class.java)
            intent.putExtra("category", it.category.toString())
            intent.putExtra("category_name", it.categoryName)
            intent.putExtra("category_size", it.itemSize)

            mMainActivity.openNextScreen {
                startActivity(intent)
            }
            mMainActivity.mFirebaseAnalytics?.logEvent("v_inter_ads_open_$it", null)
        })
    }

    private val mDialogPrepare: WaitingDialog by lazy {
        WaitingDialog(getString(R.string.loading_stickers)).apply {
            action = {}
        }
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
            mMainActivity.openNextScreen {
                startActivity(Intent(context, EmojiMakerActivity::class.java))
            }
            mMainActivity.mFirebaseAnalytics?.logEvent("v_inter_ads_create_sticker", null)
        }
        val timeConfig = mMainActivity.mFirebaseRemoteConfig.getLong(RemoteConfigKey.KEY_COLLAPSE_RELOAD_TIME)
        val timeDelay = if (timeConfig == 0L) {
            3000L
        } else {
            timeConfig
        }

        val countDownTimer: CountDownTimer = object : CountDownTimer(timeDelay, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                binding.rvCategory.visible()
            }
        }
        countDownTimer.start()
    }

    override fun setupData() {
        viewModel.getCategoryList(requireContext())
        viewModel.categoriesMutableLiveData.observe(this) {
            categoryAdapter.submitList(it.toMutableList())
            Timber.e("$it")
        }
        binding.rvCategory.adapter = categoryAdapter
        mMainActivity = activity as MainActivity
    }

    override fun onStart() {
        super.onStart()
        setUpLoadInterAds()

        if (mMainActivity.mFirebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_HOME)) {
            val adConfig = mMainActivity.mFirebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            }
            else {
                loadNativeAds(getString(R.string.native_home))
            }
        } else {
            binding.rlNative.visibility = View.GONE
        }

        mDialogPrepare.show(parentFragmentManager, mDialogPrepare.tag)
        val countDownTimer: CountDownTimer = object : CountDownTimer(Constant.WAITING_TO_LOAD_BANNER, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                mDialogPrepare.dismiss()
            }
        }
        countDownTimer.start()
    }

    private fun getUri(category: String) {
        val cw = ContextWrapper(context)
        val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
        val files = directory.listFiles()      // Get packages
        if (files != null) {                    //package's size > 0
            for (file in files) {
                if (file.isDirectory && file.name.equals(category)) {
                    val stickers = file.listFiles()
                    if (stickers != null) {
                        for (sticker in stickers) {
                            viewModel.stickerUri.add(getUriForFile(requireContext(), sticker))
                        }
                    }
                    break
                }
            }
        }
    }
    private fun getRawUri(filename: String): Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + requireContext().packageName + "/raw/" + filename)
    }

    private fun doImport(stickers: java.util.ArrayList<Uri>, emojis: java.util.ArrayList<String>) {
        val intent = Intent(CREATE_STICKER_PACK_ACTION)
        intent.putExtra(Intent.EXTRA_STREAM, stickers)
        intent.putExtra(CREATE_STICKER_PACK_IMPORTER_EXTRA, requireContext().packageName)
        intent.putExtra(CREATE_STICKER_PACK_EMOJIS_EXTRA, emojis)
        intent.setType("image/*")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            //no activity to handle intent
        }
    }

    private fun download(context: Context, category: String) {
        val assetManager = context.assets
        val listFile = assetManager.list("categories/$category/")

        if (AppUtils.checkPermission(context)) {
            AppUtils.requestPermissionAndContinue(requireActivity())
            return
        }
        if (listFile != null) {                    //package's size > 0
            for (file in listFile) {
                val inputStream1 = assetManager.open("categories/$category/$file")

                AppUtils.saveSticker(
                    context, AppUtils.convertFileToBitmap(
                        FileUtils.copyAssetFileToCache(
                            context,
                            inputStream1,
                            file
                        )
                    ), category
                )
            }
            toast(getString(R.string.download_done))
        } else {
            toast(getString(R.string.download_failed))
        }
    }

    private fun setUpLoadInterAds() {
        mMainActivity.keyAds = mMainActivity.mFirebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_INTER_HOME_SCREEN)
        if (mMainActivity.keyAds.isEmpty()) {
            mMainActivity.keyAds = getString(R.string.inter_home_screen)
        }
        if (mMainActivity.mFirebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_HOME_SCREEN)) {
            mMainActivity.loadInterAds(mMainActivity.keyAds)
        }
    }

    private fun loadNativeAds(keyAds:String) {
        if (!DeviceUtils.checkInternetConnection(requireContext())) binding.rlNative.visibility = View.GONE
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                requireContext(),
                keyAds
            ) { nativeAds ->
                if (nativeAds != null && isAdded && isVisible) {
                    //binding.frNativeAds.removeAllViews()
                    val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                    NativeAdsUtils.instance.populateNativeAdVideoView(
                        nativeAds,
                        adNativeVideoBinding.root as NativeAdView
                    )
                    binding.frNativeAds.addView(adNativeVideoBinding.root)
                } else {
                    binding.rlNative.visibility = View.GONE
                }
            }
        }

    }
}