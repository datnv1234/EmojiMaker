package com.wa.ai.emojimaker.ui.home

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.firebase.storage.FirebaseStorage
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.databinding.FragmentHomeBinding
import com.wa.ai.emojimaker.ui.adapter.CategoryAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.SharePackageDialog
import com.wa.ai.emojimaker.ui.emojimaker.EmojiMakerActivity
import com.wa.ai.emojimaker.ui.showstickers.ShowStickersActivity
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.FileUtils.getUriForFile
import java.io.File

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {

    private val CREATE_STICKER_PACK_ACTION = "org.telegram.messenger.CREATE_STICKER_PACK"
    private val CREATE_STICKER_PACK_EMOJIS_EXTRA = "STICKER_EMOJIS"
    private val CREATE_STICKER_PACK_IMPORTER_EXTRA = "IMPORTER"

    private val mContext = context
    private val sharePackageDialog : SharePackageDialog by lazy {
        SharePackageDialog().apply {
            addToWhatsapp = {
                toast(getString(R.string.this_function_is_not_supported_yet))
            }

            addToTelegram = {
                if (viewModel.stickerUri.size != 0) {
                    AppUtils.importToTelegram(requireContext(), viewModel.stickerUri.toList())
                } else {
                    toast("Please wait..!")
                }

            }

            share = {
                if (viewModel.stickerUri.size != 0) {
                    AppUtils.shareMultipleImages(requireContext(), viewModel.stickerUri.toList())
                } else {
                    toast("Please wait..!")
                }
            }

            download = {
                toast("Cannot download this category")
            }
        }
    }
    private val categoryAdapter : CategoryAdapter by lazy {
        CategoryAdapter(optionClick = {
            getUri(it)
            sharePackageDialog.show(parentFragmentManager, sharePackageDialog.tag)
        }, watchMoreClick = {
            val intent = Intent(requireContext(), ShowStickersActivity::class.java)
            intent.putExtra("category", it.category.toString())
            intent.putExtra("category_name", it.categoryName)
            intent.putExtra("category_size", it.itemSize)
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
        viewModel.getCategoryList(requireContext())
        viewModel.categoriesMutableLiveData.observe(this) {
            categoryAdapter.submitList(it.toMutableList())
        }
        binding.rvCategory.adapter = categoryAdapter
    }

    private fun getUri(category: String) {
/*
        val uris = ArrayList<Uri>()
        uris.add(getRawUri("sticker1"))
        uris.add(getRawUri("sticker2"))
        uris.add(getRawUri("sticker3"))
        uris.add(getRawUri("sticker4"))
        uris.add(getRawUri("sticker5"))
        uris.add(getRawUri("sticker6"))
        uris.add(getRawUri("sticker7"))
        uris.add(getRawUri("sticker8"))
        uris.add(getRawUri("sticker9"))

        val emojis = ArrayList<String>()
        emojis.add("☺️")
        emojis.add("\uD83D\uDE22")
        emojis.add("\uD83E\uDD73")
        emojis.add("\uD83E\uDD2A")
        emojis.add("\uD83D\uDE18️")
        emojis.add("\uD83D\uDE18️")
        emojis.add("\uD83E\uDD2A")
        emojis.add("\uD83E\uDD73")
        emojis.add("☺️")

        doImport(uris, emojis)*/
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
        /*viewModel.stickerUri.clear()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(category)
        storageRef.listAll().addOnSuccessListener { listResult ->
            for (item in listResult.items) {
                item.downloadUrl.addOnSuccessListener { uri ->
                    viewModel.stickerUri.add(uri)
                }
            }
        }*/
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
}