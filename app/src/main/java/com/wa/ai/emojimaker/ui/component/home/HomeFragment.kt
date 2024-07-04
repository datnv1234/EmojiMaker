package com.wa.ai.emojimaker.ui.component.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.adjust.sdk.Adjust
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.FragmentHomeBinding
import com.wa.ai.emojimaker.ui.adapter.HomeAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.SharePackageDialog
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiMakerActivity
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.ui.component.main.MainActivity.Companion.ITEMS_PER_AD
import com.wa.ai.emojimaker.ui.component.main.MainViewModel
import com.wa.ai.emojimaker.ui.component.showstickers.ShowStickersActivity
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.FileUtils
import com.wa.ai.emojimaker.utils.FileUtils.getUriForFile
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {

    private lateinit var mMainActivity: MainActivity
    private lateinit var mMainViewModel: MainViewModel
    private val keyAdNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME)

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
                }
            }

            download = { cate ->
                download(requireContext(), cate)
            }
        }
    }

    private val adWidth: Int
        get() {
            val displayMetrics = resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowManager =
                        requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            return (adWidthPixels / density).toInt()
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
        mMainActivity = activity as MainActivity
        mMainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mMainViewModel.categoriesMutableLiveData.observe(this) {
            val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> = HomeAdapter(
                it.toMutableList(),
                watchMoreClick = { cate ->
                    val intent = Intent(requireContext(), ShowStickersActivity::class.java)
                    intent.putExtra("category", cate.category)
                    intent.putExtra("category_name", cate.categoryName)
                    intent.putExtra("category_size", cate.itemSize)
                    startActivity(intent)
                },
                optionClick = { cate ->
                    sharePackageDialog.category = cate
                    if (!sharePackageDialog.isAdded)
                        sharePackageDialog.show(parentFragmentManager, sharePackageDialog.tag)
                })
            binding.rvCategory.adapter = adapter
            loadNative()
        }


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

    }

    private fun loadNative() {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_HOME)) {
            loadNativeAd(0)
        }
    }

    private fun loadNativeAd(index: Int) {
        if (index >= (mMainViewModel.categoriesMutableLiveData.value?.size ?: 0)) {
            return
        }
        val item = mMainViewModel.categoriesMutableLiveData.value?.get(index) as? NativeAdView
            ?: throw ClassCastException("Expected item at index $index to be a banner ad ad.")
        NativeAdsUtils.instance.loadNativeAds(
            requireContext(),
            keyAdNative
        ) { nativeAds ->
            if (nativeAds != null) {
                val adLayoutView =
                    LayoutInflater.from(requireContext())
                        .inflate(R.layout.ad_native_content, item, false) as NativeAdView
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adLayoutView
                )
                item.removeAllViews()
                item.addView(adLayoutView)
            }
            loadNativeAd(index + ITEMS_PER_AD)
        }
    }

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
}