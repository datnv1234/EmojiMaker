package com.wa.ai.emojimaker.ui.component.emojimaker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.provider.OpenableColumns
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.common.Constant.INTERNAL_MY_CREATIVE_DIR
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.ActivityEmojiMakerBinding
import com.wa.ai.emojimaker.ui.adapter.OptionAdapter
import com.wa.ai.emojimaker.ui.adapter.PagerIconAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.dialog.AddToPackageDialog
import com.wa.ai.emojimaker.ui.dialog.CreatePackageDialog
import com.wa.ai.emojimaker.ui.dialog.SaveStickerDialog
import com.wa.ai.emojimaker.ui.dialog.SaveSuccessDialog
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.AdsConsentManager
import com.wa.ai.emojimaker.utils.ads.BannerUtils
import com.wa.ai.emojimaker.utils.extention.checkInternetConnection
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible
import com.wa.ai.emojimaker.utils.sticker.BitmapStickerIcon
import com.wa.ai.emojimaker.utils.sticker.DrawableSticker
import com.wa.ai.emojimaker.utils.sticker.Sticker
import com.wa.ai.emojimaker.utils.sticker.StickerView
import com.wa.ai.emojimaker.utils.sticker.StickerViewModel
import com.wa.ai.emojimaker.utils.sticker.StickerViewSerializer
import com.wa.ai.emojimaker.utils.sticker.iconEvents.DeleteIconEvent
import com.wa.ai.emojimaker.utils.sticker.iconEvents.DuplicateIconEvent
import com.wa.ai.emojimaker.utils.sticker.iconEvents.ZoomIconEvent
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow

class EmojiMakerActivity : BaseBindingActivity<ActivityEmojiMakerBinding, StickerViewModel>() {

    private var adsConsentManager: AdsConsentManager? = null
    private val isAdsInitializeCalled = AtomicBoolean(false)
    private val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    private var mInterstitialAd: InterstitialAd? = null

    private val keyAdInterAllPrice =
        FirebaseRemoteConfig.getInstance()
            .getString(RemoteConfigKey.KEY_ADS_INTER_CREATE_EMOJI)
    private val keyAdInterHigh =
        FirebaseRemoteConfig.getInstance()
            .getString(RemoteConfigKey.KEY_ADS_INTER_CREATE_EMOJI_HIGH)

    private val bannerReload =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)
    private val bannerMonetDelay =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_MONET_DELAY)

    private val keyAdBannerAllPrice = FirebaseRemoteConfig.getInstance()
        .getString(RemoteConfigKey.KEY_ADS_BANNER_CREATE_EMOJI)
    private val keyAdBannerHigh = FirebaseRemoteConfig.getInstance()
        .getString(RemoteConfigKey.KEY_ADS_BANNER_CREATE_EMOJI_HIGH)

    private var retryAttempt = 0.0
    private var isFinishImmediately = false

    private lateinit var emojiViewModel: EmojiViewModel

    private val pagerIconAdapter: PagerIconAdapter by lazy {
        PagerIconAdapter(itemClick = {
            try {
                showInterstitialItemClick()
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            doAddSticker(it)
        })
    }


    /*
    * Declare dialog variable
    * */
    private val mSaveDialog: SaveStickerDialog by lazy {
        SaveStickerDialog(this).apply {
            addToPackage = {
                if (!mAddToPackageDialog.isAdded)
                    mAddToPackageDialog.show(supportFragmentManager, mAddToPackageDialog.tag)
            }

            download = {
                emojiViewModel.bitmapMutableLiveData.value?.let {
                }
            }

            share = {
                emojiViewModel.bitmapMutableLiveData.value?.let { it1 ->
                    share(it1)
                }
            }
        }
    }

    private val mAddToPackageDialog: AddToPackageDialog by lazy {
        AddToPackageDialog(this).apply {
            save = {
                if (it == null) {
                    toast(getString(R.string.please_input_package_name))
                } else {
                    emojiViewModel.bitmapMutableLiveData.value?.let { it1 ->
                        DeviceUtils.saveToPackage(
                            this@EmojiMakerActivity,
                            INTERNAL_MY_CREATIVE_DIR,
                            packageName = it.id,
                            bitmapImage = it1
                        )
                    }
                    if (!mSaveSuccessDialog.isAdded)
                        mSaveSuccessDialog.show(supportFragmentManager, mSaveSuccessDialog.tag)
                }
            }

            createNewPackage = {
                if (!mCreatePackageDialog.isAdded)
                    mCreatePackageDialog.show(supportFragmentManager, mCreatePackageDialog.tag)
            }

        }
    }

    private val mCreatePackageDialog: CreatePackageDialog by lazy {
        CreatePackageDialog(this).apply {
            confirm = { pkg ->
                emojiViewModel.bitmapMutableLiveData.value?.let {
                    DeviceUtils.saveToPackage(
                        this@EmojiMakerActivity,
                        INTERNAL_MY_CREATIVE_DIR,
                        packageName = pkg.id,
                        bitmapImage = it
                    )
                }
                if (!mSaveSuccessDialog.isAdded)
                    mSaveSuccessDialog.show(supportFragmentManager, mSaveSuccessDialog.tag)
            }
        }
    }

    private val mSaveSuccessDialog: SaveSuccessDialog by lazy {
        SaveSuccessDialog(this).apply {
            home = {
                isFinishImmediately = true
                startActivity(Intent(this@EmojiMakerActivity, MainActivity::class.java))
                try {
                    forceShowInterstitial(false)
                } catch (e: Exception) {
                    val params = Bundle().apply {
                        putString("EmojiMakerActivity", e.message)
                    }
                    mFirebaseAnalytics.logEvent("crash", params)
                }
                finish()
            }
            createMore = {
                newBoard()
                try {
                    showInterstitial(true)
                } catch (e: Exception) {
                    val params = Bundle().apply {
                        putString("EmojiMakerActivity", e.message)
                    }
                    mFirebaseAnalytics.logEvent("crash", params)
                }
            }
        }
    }

    /*
    * End of declaring dialog variable
    * */

    override val layoutId: Int
        get() = R.layout.activity_emoji_maker

    override fun getViewModel(): Class<StickerViewModel> = StickerViewModel::class.java

    @SuppressLint("ClickableViewAccessibility")
    override fun setupView(savedInstanceState: Bundle?) {
        emojiViewModel = ViewModelProvider(this)[EmojiViewModel::class.java]
        Timber.plant(Timber.DebugTree())
        viewModel.stickerOperationListener = MyStickerOperationListener(binding)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setupIcons()
        setupButtons()

        handleIntent(intent)
        intent.type = null // Don't run again if rotated/etc.
        setUpViewPager()

        binding.btnSave.setOnSafeClick(1000) {
            mFirebaseAnalytics.logEvent("EmojiMaker", null)
            val bitmap = binding.stickerView.createBitmap()
            emojiViewModel.setBitmap(bitmap)
            if (!mSaveDialog.isAdded) {
                mSaveDialog.show(supportFragmentManager, mSaveDialog.tag)
            }
            try {
                showInterstitial()
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
        }
        binding.btnWatchVideo.setOnSafeClick {
            try {
                forceShowInterstitial()
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            emojiViewModel.setLock(emojiViewModel.pageSelected.value!!, false)
        }

        binding.llLocked.setOnSafeClick {

        }
        emojiViewModel.pageSelected.observe(this) {
            if (it == LOCK1 && emojiViewModel.lock1.value!!) {
                binding.llLocked.visible()
            } else if (it == LOCK2 && emojiViewModel.lock2.value!!) {
                binding.llLocked.visible()
            } else if (it == LOCK3 && emojiViewModel.lock3.value!!) {
                binding.llLocked.visible()
            } else {
                binding.llLocked.gone()
            }
        }

        initAdsManager()
    }

    private fun setUpViewPager() {

    }

    override fun setupData() {

        emojiViewModel.getItemOption(this)
        emojiViewModel.optionMutableLiveData.observe(this) {
            pagerIconAdapter.submitList(it.toMutableList())
        }
        val optionAdapter = OptionAdapter(emojiViewModel.optionList, itemClick = {
            try {
                showInterstitialItemClick()
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            binding.vpIcon.setCurrentItem(it, true)
        })
        binding.vpIcon.apply {
            adapter = pagerIconAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    emojiViewModel.setPageSelected(position)
                    try {
                        showInterstitialItemClick()
                    } catch (e: Exception) {
                        val params = Bundle().apply {
                            putString("EmojiMakerActivity", e.message)
                        }
                        mFirebaseAnalytics.logEvent("crash", params)
                    }
                }
            })
        }
        binding.rvOptions.adapter = optionAdapter
        loadAds()
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun loadAds() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_BANNER_CREATE_EMOJI)
        ) {
            loadBannerNoCollapse()
        } else {
            binding.bannerContainer.gone()
        }
        emojiViewModel.loadBanner.observe(this) {
            loadBannerCollapse()
        }
    }

    private fun loadBannerNoCollapse() {
        emojiViewModel.starTimeCountReloadBanner(bannerReload + 5000L)
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_USE_BANNER_MONET)) {
            BannerUtils.instance?.loadBannerHigh(this, keyAdBannerHigh) { res2 ->
                if (!res2) {
                    binding.rlBanner.visible()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            BannerUtils.instance?.loadBanner(
                                this,
                                keyAdBannerAllPrice
                            ) { }
                        },
                        bannerMonetDelay
                    )
                } else {
                    binding.rlBanner.gone()
                }
            }
        } else {
            binding.rlBannerHigh.gone()
            BannerUtils.instance?.loadBanner(this, keyAdBannerAllPrice) { }
        }
    }

    private fun loadBannerCollapse() {
        emojiViewModel.starTimeCountReloadBanner(bannerReload)
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_USE_BANNER_MONET)) {
            BannerUtils.instance?.loadCollapsibleBannerHigh(this, keyAdBannerHigh) { res2 ->
                if (!res2) {
                    binding.rlBanner.visible()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            BannerUtils.instance?.loadCollapsibleBanner(
                                this,
                                keyAdBannerAllPrice
                            ) { }
                        },
                        bannerMonetDelay
                    )
                } else {
                    binding.rlBanner.gone()
                }
            }
        } else {
            binding.rlBannerHigh.gone()
            BannerUtils.instance?.loadCollapsibleBanner(this, keyAdBannerAllPrice) { }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent)
                } else if (intent.type == "text/plain") {
                    handleSendLink(intent)
                }
            }

            intent?.action == Intent.ACTION_SEND_MULTIPLE
                    && intent.type?.startsWith("image/") == true -> {
                handleSendMultipleImages(intent)
            }
        }
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let(this::doAddSticker)
    }

    private fun handleSendMultipleImages(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let { images ->
            images.forEach {
                (it as? Uri)?.let(this::doAddSticker)
            }
        }
    }

    private fun isValidUrl(text: String) = Patterns.WEB_URL.matcher(text).matches()

    private fun handleSendLink(intent: Intent) {
        requestPermission(Manifest.permission.INTERNET)
        requestPermission(Manifest.permission.ACCESS_NETWORK_STATE)

        if (hasPermission(Manifest.permission.INTERNET)
            && hasPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        ) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (text != null) {
                if (!isValidUrl(text)) {
                    Toast.makeText(this, "Invalid link", Toast.LENGTH_LONG).show()
                }
                FetchImageFromLinkTask(text, this).execute()
            }
        }
    }

    private fun setupIcons() {
        //currently you can config your own icons and icon event
        //the event you can custom
        val deleteIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_remove
            ),
            BitmapStickerIcon.LEFT_TOP
        )
        deleteIcon.iconEvent = DeleteIconEvent()
        val zoomIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_zoom_and_rotate
            ),
            BitmapStickerIcon.RIGHT_BOTTOM
        )
        zoomIcon.iconEvent = ZoomIconEvent()
        val duplicateIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_duplicate
            ),
            BitmapStickerIcon.LEFT_BOTTOM
        )
        duplicateIcon.iconEvent = DuplicateIconEvent()
        viewModel.icons.value = arrayListOf(
            deleteIcon,
            zoomIcon,
            duplicateIcon
        )
        viewModel.activeIcons.value = viewModel.icons.value
    }

    private fun save() {
        if (viewModel.currentFileName != null) {
            doSave(viewModel.currentFileName!!)
        } else {
            saveAs()
        }
    }

    private fun saveAs() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose File Name")

        val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
        val defaultFileName: String =
            formatter.format(Calendar.getInstance().time) + "." + SAVE_FILE_EXTENSION

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(defaultFileName, TextView.BufferType.EDITABLE)
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            doSave(input.text.toString())
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun doSave(fileName: String) {
        val cw = ContextWrapper(this)
        val directory: File = cw.getDir("mySticker", Context.MODE_PRIVATE)
        val file = File(directory, fileName)
        try {
            StickerViewSerializer().serialize(viewModel, file)
            viewModel.currentFileName = fileName
            Toast.makeText(this, "Saved to $file", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Timber.e(e, "Error writing %s", file)
            Toast.makeText(this, "Error writing $file", Toast.LENGTH_LONG).show()
        }
    }

    private fun createSticker(bitmap: Bitmap, packageName: String) {
        DeviceUtils.saveToPackage(
            this,
            INTERNAL_MY_CREATIVE_DIR,
            packageName = packageName,
            bitmapImage = bitmap
        )
    }

    private fun download(bitmap: Bitmap) {
        createSticker(bitmap, "Download")
    }

    private fun share(bitmap: Bitmap) {
        AppUtils.shareImage(this, bitmap)
    }

    private fun addToPackage(bitmap: Bitmap, category: String) {
        mAddToPackageDialog.show(supportFragmentManager, mAddToPackageDialog.tag)
    }

    private fun load() {
        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Open Saved File"),
                INTENT_PICK_SAVED_FILE
            )
        }
    }

    @SuppressLint("Range")
    private fun getFileNameOfUri(uri: Uri): String {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun doLoad(file: Uri) {
        val fileName = getFileNameOfUri(file)
        val extension = File(fileName).extension
        if (extension != SAVE_FILE_EXTENSION) {
            Toast.makeText(
                this,
                "File does not have '.$SAVE_FILE_EXTENSION' extension",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            val stream = contentResolver.openInputStream(file)!!
            StickerViewSerializer().deserialize(viewModel, stream, resources)
            //Toast.makeText(this, "Loaded $fileName", Toast.LENGTH_SHORT).show()
            viewModel.currentFileName = fileName
            viewModel.isLocked.value = true
        } catch (e: IOException) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Timber.e(e, "Error writing %s", file)
            Toast.makeText(this, "Error reading $file", Toast.LENGTH_LONG).show()
        }
    }

    private fun newBoard() {
        viewModel.removeAllStickers()
        viewModel.resetView()
        viewModel.currentFileName = null
    }

    private fun cropAll() {
        kotlin.runCatching {
            viewModel.stickers.value!!.forEach {
                (it as? DrawableSticker)?.cropDestructively(resources)
            }
            binding.stickerView.invalidate()
            Toast.makeText(this, "Cropped all images.", Toast.LENGTH_SHORT).show()
        }.onFailure {
            it.printStackTrace()
        }

    }

    private fun addSticker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), INTENT_PICK_IMAGE)
    }

    private fun doAddSticker(file: Uri) {
        val imageStream = contentResolver.openInputStream(file)
        val bitmap = BitmapFactory.decodeStream(imageStream)
        doAddSticker(bitmap)
    }

    private fun doAddSticker(bitmap: Bitmap?) {
        if (bitmap == null) {
            Toast.makeText(this, "Could not decode image", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val totalSize = bitmap.width * bitmap.height
            val newBitmap: Bitmap = if (totalSize > MAX_SIZE_PIXELS) {
                val scaleFactor: Float = MAX_SIZE_PIXELS.toFloat() / totalSize.toFloat()
                val newWidth = (bitmap.width * scaleFactor).toInt()
                val newHeight = (bitmap.height * scaleFactor).toInt()

                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
            } else {
                bitmap
            }

            val drawable = BitmapDrawable(resources, newBitmap)
            viewModel.addSticker(DrawableSticker(drawable))

        } catch (e: OutOfMemoryError) {
            Timber.e(e, "OutOfMemoryError when processing bitmap.")
            Toast.makeText(
                this,
                "Failed to add sticker due to memory limitations",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            when (requestCode) {
                INTENT_PICK_IMAGE -> {
                    val selectedImage = data.data!!
                    doAddSticker(selectedImage)
                }

                INTENT_PICK_SAVED_FILE -> {
                    val selectedFile = data.data!!
                    doLoad(selectedFile)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupButtons() {

        binding.ivBack.setOnSafeClick {
            try {
                forceShowInterstitial(false)
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            finish()
        }

        binding.buttonAdd.setOnClickListener {
            try {
                showInterstitialItemClick()
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            addSticker()
        }

        binding.buttonReset.setOnClickListener {
            try {
                showInterstitialItemClick(true)
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            viewModel.resetView()
        }

        binding.buttonLock.setOnCheckedChangeListener { _, isToggled ->
            try {
                showInterstitialItemClick(true)
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            viewModel.isLocked.value = isToggled
        }

        binding.buttonCrop.setOnCheckedChangeListener { _, isToggled ->
            try {
                showInterstitialItemClick(true)
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            viewModel.isCropActive.value = isToggled
        }

        binding.buttonResetZoom.setOnClickListener {
            viewModel.resetCurrentStickerZoom()
        }

        binding.buttonResetCrop.setOnClickListener {
            viewModel.resetCurrentStickerCropping()
        }

        binding.buttonRotate.setOnLongClickListener {
            viewModel.resetCurrentStickerRotation();
            true
        }

        binding.btnFlipHorizontal.setOnSafeClick {
            try {
                showInterstitialItemClick(true)
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            viewModel.flipCurrentSticker(StickerView.FLIP_HORIZONTALLY)
        }
        binding.btnFlipVertical.setOnSafeClick {
            try {
                showInterstitialItemClick(true)
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            viewModel.flipCurrentSticker(StickerView.FLIP_VERTICALLY)
        }
        binding.btnUndo.setOnSafeClick {
            try {
                showInterstitialItemClick(true)
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            toast("This function is not available!")
        }
        binding.btnReUndo.setOnSafeClick {
            try {
                showInterstitialItemClick(true)
            } catch (e: Exception) {
                val params = Bundle().apply {
                    putString("EmojiMakerActivity", e.message)
                }
                mFirebaseAnalytics.logEvent("crash", params)
            }
            toast("This function is not available!")
        }
    }

    override fun finish() {
        if (isFinishImmediately) {
            super.finish()
        } else {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.confirm))
                .setMessage(getString(R.string.are_you_sure_want_to_quit))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    try {
                        showInterstitial(false)
                    } catch (e: Exception) {
                        val params = Bundle().apply {
                            putString("EmojiMakerActivity", e.message)
                        }
                        mFirebaseAnalytics.logEvent("crash", params)
                    }
                    super.finish()
                }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }
    }

    private fun hasPermission(permission: String) =
        ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(permission: String) {
        if (!hasPermission(permission)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                PERM_RQST_CODE
            )
        }
    }

    internal class FetchImageFromLinkTask(val text: String, val context: EmojiMakerActivity) :
        AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val fuel = FuelManager()
                fuel.baseHeaders =
                    mapOf(Headers.USER_AGENT to "Mozilla/5.0 (X11; Linux x86_64; rv:76.0) Gecko/20100101 Firefox/76.0")

                fuel.head(text).response { _, head, result ->
                    result.fold({
                        val contentType = head.headers[Headers.CONTENT_TYPE]
                        if (!contentType.any { it.startsWith("image/") }) {
                            Toast.makeText(context, "Link is not an image", Toast.LENGTH_LONG)
                                .show()
                            return@response
                        }

                        fuel.get(text)
                            .response { _, _, body ->
                                body.fold({
                                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                                    context.doAddSticker(bitmap)
                                    View.GONE
                                }, {
                                    Toast.makeText(
                                        context,
                                        "Failed to download image: $it",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    View.GONE
                                    Timber.e(it)
                                })
                            }
                    }, {
                        Toast.makeText(context, "Failed to download image", Toast.LENGTH_LONG)
                            .show()
                        Timber.e(it)
                    })
                }
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, "Invalid link", Toast.LENGTH_LONG).show()
            }
            return null
        }
    }


    internal class MyStickerOperationListener(private val binding: ActivityEmojiMakerBinding) :
        StickerView.OnStickerOperationListener {
        override fun onStickerAdded(sticker: Sticker, direction: Int) {
            binding.stickerView.layoutSticker(sticker, direction)
            binding.stickerView.invalidate()
            binding.btnSave.isEnabled = true
        }

        override fun onStickerClicked(sticker: Sticker) {
            binding.stickerView.invalidate()
        }

        override fun onStickerDeleted(sticker: Sticker, isLastSticker: Boolean) {
            binding.stickerView.invalidate()
            binding.btnSave.isEnabled = !isLastSticker
        }

        override fun onStickerDragFinished(sticker: Sticker) {
            binding.stickerView.invalidate()
        }

        override fun onStickerTouchedDown(sticker: Sticker) {
            binding.stickerView.invalidate()
        }

        override fun onStickerZoomFinished(sticker: Sticker) {
            binding.stickerView.invalidate()
        }

        override fun onStickerFlipped(sticker: Sticker) {
            binding.stickerView.invalidate()
        }

        override fun onStickerDoubleTapped(sticker: Sticker) {
            binding.stickerView.invalidate()
        }

        override fun onStickerMoved(sticker: Sticker) {
            binding.stickerView.invalidate()
        }

        override fun onInvalidateView() {
            binding.stickerView.invalidate()
        }

        override fun onClearBoard() {
            binding.stickerView.invalidate()
            binding.btnSave.isEnabled = false
        }
    }

    private fun initAdsManager() {
        var isInitializeMobileAdsSdk = false
        adsConsentManager = AdsConsentManager.getInstance(applicationContext)
        adsConsentManager?.gatherConsent(this) { consentError ->
            if (consentError != null || adsConsentManager?.canRequestAds == true) {
                initializeMobileAdsSdk()
                isInitializeMobileAdsSdk = true
            }
        }

        if (!isInitializeMobileAdsSdk && adsConsentManager?.canRequestAds == true) {
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isAdsInitializeCalled.getAndSet(true)) {
            return
        }
        try {
            MobileAds.initialize(this) {}
        } catch (e: Exception) {
            Timber.e(e)
        }
        loadInterAds()
    }


    private fun loadInterAds() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_CREATE_EMOJI)
        ) {
            loadInterAdHigh()
        }
    }

    private fun loadInterAdAllPrice() {
        InterstitialAd.load(
            this,
            keyAdInterAllPrice,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mFirebaseAnalytics.logEvent("e_load_inter_splash", null)
                    mInterstitialAd = null
                    retryAttempt++
                    if (retryAttempt < 4) {
                        val delayMillis = TimeUnit.SECONDS.toMillis(
                            2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong()
                        )
                        Handler(Looper.getMainLooper()).postDelayed(
                            { loadInterAdAllPrice() },
                            delayMillis
                        )
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    mFirebaseAnalytics.logEvent("d_load_inter_splash", null)
                    mInterstitialAd = ad
                    retryAttempt = 0.0
                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue ->
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                mInterstitialAd?.responseInfo?.loadedAdapterResponseInfo
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros.toDouble() / 1000000.0
                            adRevenue.setRevenue(revenue, adValue.currencyCode)
                            adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                            Adjust.trackAdRevenue(adRevenue)

                            val analytics = FirebaseAnalytics.getInstance(this@EmojiMakerActivity)
                            val params = Bundle().apply {
                                putString(
                                    FirebaseAnalytics.Param.AD_PLATFORM,
                                    "admob mediation"
                                )
                                putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                                putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                                putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                                putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            }
                            analytics.logEvent("ad_impression_2", params)
                        }
                }
            }
        )
    }

    private fun loadInterAdHigh() {
        InterstitialAd.load(
            this,
            keyAdInterHigh,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mFirebaseAnalytics.logEvent("e_load_inter_create_emoji", null)
                    mInterstitialAd = null
                    loadInterAdAllPrice()
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    mFirebaseAnalytics.logEvent("d_load_inter_create_emoji", null)
                    mInterstitialAd = ad
                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue ->
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                mInterstitialAd?.responseInfo?.loadedAdapterResponseInfo
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros.toDouble() / 1000000.0
                            adRevenue.setRevenue(revenue, adValue.currencyCode)
                            adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                            Adjust.trackAdRevenue(adRevenue)

                            val analytics = FirebaseAnalytics.getInstance(this@EmojiMakerActivity)
                            val params = Bundle().apply {
                                putString(
                                    FirebaseAnalytics.Param.AD_PLATFORM,
                                    "admob mediation"
                                )
                                putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                                putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                                putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                                putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            }
                            analytics.logEvent("ad_impression_2", params)
                        }
                }
            }
        )
    }

    private fun showInterstitial(isReload: Boolean = true) {
        if (!checkInternetConnection()) {
            return
        }
        val timeLoad = FirebaseRemoteConfig.getInstance()
            .getLong(RemoteConfigKey.INTER_DELAY)

        val timeSubtraction =
            Date().time - SharedPreferenceHelper.getLong(Constant.TIME_LOAD_NEW_INTER_ADS)
        if (timeSubtraction <= timeLoad) {
            return
        }

        if (mInterstitialAd == null) {
            if (adsConsentManager?.canRequestAds == false) {
                return
            }
            if (isReload)
                loadInterAds()
            return
        }
        mInterstitialAd?.show(this)

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                if (isReload) {
                    loadInterAds()
                }
                SharedPreferenceHelper.storeLong(
                    Constant.TIME_LOAD_NEW_INTER_ADS,
                    Date().time
                )
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
            }
        }
    }

    private fun forceShowInterstitial(isReload: Boolean = true) {
        if (!checkInternetConnection()) {
            return
        }

        if (mInterstitialAd == null) {
            if (adsConsentManager?.canRequestAds == false) {
                return
            }
            if (isReload)
                loadInterAds()
            return
        }
        mInterstitialAd?.show(this)

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                if (isReload) {
                    loadInterAds()
                }
                SharedPreferenceHelper.storeLong(
                    Constant.TIME_LOAD_NEW_INTER_ADS,
                    Date().time
                )
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                mFirebaseAnalytics.logEvent("unlock_items", null)
            }
        }
    }

    private fun showInterstitialItemClick(isReload: Boolean = true) {
        if (!checkInternetConnection()) {
            return
        }
        val timeLoad = FirebaseRemoteConfig.getInstance()
            .getLong(RemoteConfigKey.INTER_ITEM_CLICK_DELAY)

        val timeSubtraction =
            Date().time - SharedPreferenceHelper.getLong(Constant.TIME_LOAD_NEW_INTER_ADS)
        if (timeSubtraction <= timeLoad) {
            return
        }

        if (mInterstitialAd == null) {
            if (adsConsentManager?.canRequestAds == false) {
                return
            }
            if (isReload)
                loadInterAds()
            return
        }
        mInterstitialAd?.show(this)
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                if (isReload) {
                    loadInterAds()
                }
                SharedPreferenceHelper.storeLong(
                    Constant.TIME_LOAD_NEW_INTER_ADS,
                    Date().time
                )
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
            }
        }
    }


    companion object {

        const val LOCK1 = 3
        const val LOCK2 = 6
        const val LOCK3 = 10

        const val PERM_RQST_CODE = 110
        const val SAVE_FILE_EXTENSION: String = "ref"

        const val INTENT_PICK_IMAGE = 1
        const val INTENT_PICK_SAVED_FILE = 2

        const val MAX_SIZE_PIXELS = 2000 * 2000
    }
}