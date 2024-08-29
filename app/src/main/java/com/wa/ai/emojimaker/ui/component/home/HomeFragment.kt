package com.wa.ai.emojimaker.ui.component.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.adjust.sdk.Adjust
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.AdNativeContentHomeBinding
import com.wa.ai.emojimaker.databinding.FragmentHomeBinding
import com.wa.ai.emojimaker.ui.adapter.HomeAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.SharePackageDialog
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiMakerActivity
import com.wa.ai.emojimaker.ui.component.emojimerge.MergeAct2
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.ui.component.main.MainViewModel
import com.wa.ai.emojimaker.ui.component.showstickers.ShowStickersActivity
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.FileUtils
import com.wa.ai.emojimaker.utils.FileUtils.getUriForFile
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

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

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java
    override fun registerOnBackPress() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_home
    override val title: String
        get() = getString(R.string.app_name)

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

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
                    kotlin.runCatching {
                        mMainActivity.showInterstitial {}
                    }
                },
                optionClick = { cate ->
                    sharePackageDialog.category = cate
                    if (!sharePackageDialog.isAdded)
                        sharePackageDialog.show(parentFragmentManager, sharePackageDialog.tag)
                })
            binding.rvCategory.adapter = adapter
        }

        binding.btnCreateSticker.setOnSafeClick {
            startActivity(Intent(context, EmojiMakerActivity::class.java))
            kotlin.runCatching {
                mMainActivity.forceShowInterstitial {}
            }.onFailure {
                it.printStackTrace()
            }
        }

        binding.btnMergeEmoji.setOnSafeClick {
            kotlin.runCatching {
                startActivity(Intent(context, MergeAct2::class.java))
                mMainActivity.forceShowInterstitial {}
            }.onFailure {
                it.printStackTrace()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
        mMainActivity.binding.titleToolbar.gone()
        mMainActivity.binding.imgToolbar.visible()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
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