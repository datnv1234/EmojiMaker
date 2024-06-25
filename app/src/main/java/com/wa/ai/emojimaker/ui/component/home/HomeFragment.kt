package com.wa.ai.emojimaker.ui.component.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.adjust.sdk.Adjust
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.AdNativeVideoHorizontalBinding
import com.wa.ai.emojimaker.databinding.FragmentHomeBinding
import com.wa.ai.emojimaker.ui.adapter.CategoryAdapter
import com.wa.ai.emojimaker.ui.adapter.HomeAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.SharePackageDialog
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiMakerActivity
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.ui.component.main.MainViewModel
import com.wa.ai.emojimaker.ui.component.showstickers.ShowStickersActivity
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.FileUtils
import com.wa.ai.emojimaker.utils.FileUtils.getUriForFile
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import timber.log.Timber

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {

    private lateinit var mMainActivity: MainActivity
    private lateinit var mMainViewModel: MainViewModel

    private val sharePackageDialog: SharePackageDialog by lazy {
        SharePackageDialog().apply {
            addToWhatsapp = {
                toast(getString(R.string.coming_soon))
            }

            addToTelegram = { cate ->
                viewModel.stickerUri.clear()
                val assetManager = requireContext().assets
                val listFile = assetManager.list("categories/$cate")
                if (listFile != null) {                    //package's size > 0
                    for (file in listFile) {
                        val inputStream1 = assetManager.open("categories/$cate/$file")
                        viewModel.stickerUri.add(
                            getUriForFile(
                                requireContext(),
                                FileUtils.copyAssetFileToCache(requireContext(), inputStream1, file)
                            )
                        )
                        inputStream1.close()
                    }
                }
                AppUtils.doImport(requireContext(), viewModel.stickerUri)
                mMainActivity.mFirebaseAnalytics?.logEvent("v_inter_ads_import_$cate", null)

            }

            share = { cate ->
                viewModel.stickerUri.clear()
                val assetManager = requireContext().assets
                val listFile = assetManager.list("categories/$cate")
                if (listFile != null) {                    //package's size > 0
                    for (file in listFile) {
                        val inputStream1 = assetManager.open("categories/$cate/$file")
                        viewModel.stickerUri.add(
                            getUriForFile(
                                requireContext(),
                                FileUtils.copyAssetFileToCache(requireContext(), inputStream1, file)
                            )
                        )
                        inputStream1.close()
                    }
                }
                if (viewModel.stickerUri.size != 0) {
                    AppUtils.shareMultipleImages(
                        requireContext(),
                        viewModel.stickerUri.toList()
                    )
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
    }

    override fun setupData() {
        mMainActivity = activity as MainActivity
        mMainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> = HomeAdapter(
            requireContext(),
            mMainViewModel.categories,
            watchMoreClick = {
                val intent = Intent(requireContext(), ShowStickersActivity::class.java)
                intent.putExtra("category", it.category)
                intent.putExtra("category_name", it.categoryName)
                intent.putExtra("category_size", it.itemSize)
                mMainActivity.openNextScreen {
                    startActivity(intent)
                }
            },
            optionClick = {
                sharePackageDialog.category = it
                if (!sharePackageDialog.isAdded)
                    sharePackageDialog.show(parentFragmentManager, sharePackageDialog.tag)
            })
        binding.rvCategory.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
        mMainActivity.binding.titleToolbar.text = title
    }

    override fun onStart() {
        super.onStart()
        loadAds()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun loadAds() {
        //setUpLoadInterAds()
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_HOME)
        ) {
            loadNativeAds()
        } else {
            binding.rlNative.gone()
        }
    }

    /*private fun getUri(category: String) {
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
    }*/
//    private fun getRawUri(filename: String): Uri {
//        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + requireContext().packageName + "/raw/" + filename)
//    }

//    private fun doImport(stickers: java.util.ArrayList<Uri>, emojis: java.util.ArrayList<String>) {
//        val intent = Intent(CREATE_STICKER_PACK_ACTION)
//        intent.putExtra(Intent.EXTRA_STREAM, stickers)
//        intent.putExtra(CREATE_STICKER_PACK_IMPORTER_EXTRA, requireContext().packageName)
//        intent.putExtra(CREATE_STICKER_PACK_EMOJIS_EXTRA, emojis)
//        intent.setType("image/*")
//        try {
//            startActivity(intent)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            //no activity to handle intent
//        }
//    }

    private fun download(context: Context, category: String) {
        val assetManager = context.assets
        val listFile = assetManager.list("categories/$category")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (AppUtils.checkPermission(context)) {
                AppUtils.requestPermissionAndContinue(requireActivity())
                return
            }
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
                inputStream1.close()
            }
            toast(getString(R.string.download_done))
        } else {
            toast(getString(R.string.download_failed))
        }
    }

    private fun setUpLoadInterAds() {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_HOME_SCREEN)) {
            mMainActivity.loadInterAds()
        }
    }

    private fun loadNativeAds() {
        if (!DeviceUtils.checkInternetConnection(requireContext())) binding.rlNative.visibility =
            View.GONE
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                requireContext(),
                mMainActivity.keyAdsNativeHome
            ) { nativeAds ->

                if (nativeAds != null && isAdded && isVisible) {
                    if (isDetached) {
                        nativeAds.destroy()
                        return@loadNativeAds
                    }
                    //binding.frNativeAds.removeAllViews()
                    val adNativeVideoBinding =
                        AdNativeVideoHorizontalBinding.inflate(layoutInflater)
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