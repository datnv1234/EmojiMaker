package com.wa.ai.emojimaker.ui.emojimaker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.OpenableColumns
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.ActivityEmojiMakerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.sticker.BitmapStickerIcon
import com.wa.ai.emojimaker.utils.sticker.DrawableSticker
import com.wa.ai.emojimaker.utils.sticker.Sticker
import com.wa.ai.emojimaker.utils.sticker.StickerView
import com.wa.ai.emojimaker.utils.sticker.StickerViewModel
import com.wa.ai.emojimaker.utils.sticker.StickerViewSerializer
import com.wa.ai.emojimaker.utils.sticker.iconEvents.DeleteIconEvent
import com.wa.ai.emojimaker.utils.sticker.iconEvents.FlipHorizontallyEvent
import com.wa.ai.emojimaker.utils.sticker.iconEvents.FlipVerticallyEvent
import com.wa.ai.emojimaker.utils.sticker.iconEvents.ZoomIconEvent
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmojiMakerActivity : AppCompatActivity() {

    private lateinit var viewModel: StickerViewModel
    private lateinit var binding: ActivityEmojiMakerBinding
    private var rotateToastShowed = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji_maker)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_emoji_maker)

        viewModel = ViewModelProvider(this)[StickerViewModel::class.java]
        viewModel.stickerOperationListener = MyStickerOperationListener(binding)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this

        setupIcons()
        setupButtons()

        handleIntent(intent)
        intent.type = null // Don't run again if rotated/etc.
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
            && hasPermission(Manifest.permission.ACCESS_NETWORK_STATE))
        {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)!!
            if (!isValidUrl(text)) {
                Toast.makeText(this, "Invalid link", Toast.LENGTH_LONG).show()
            }

            FetchImageFromLinkTask(text, this).execute()
        }
    }

    private fun setupIcons() {
        //currently you can config your own icons and icon event
        //the event you can custom
        val deleteIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.sticker_ic_close_white_18dp
            ),
            BitmapStickerIcon.LEFT_TOP
        )
        deleteIcon.iconEvent = DeleteIconEvent()
        val zoomIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.sticker_ic_scale_white_18dp
            ),
            BitmapStickerIcon.RIGHT_BOTTOM
        )
        zoomIcon.iconEvent = ZoomIconEvent()
        val flipIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.sticker_ic_flip_white_18dp
            ),
            BitmapStickerIcon.RIGHT_TOP
        )
        flipIcon.iconEvent = FlipHorizontallyEvent()
        val flipVerticallyIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.sticker_ic_flip_vert_white_18dp
            ),
            BitmapStickerIcon.LEFT_BOTTOM
        )
        flipVerticallyIcon.iconEvent = FlipVerticallyEvent()
        viewModel.icons.value = arrayListOf(
            deleteIcon,
            zoomIcon,
            flipIcon,
            flipVerticallyIcon
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

        builder.setPositiveButton(
            "OK"
        ) { dialog, which -> doSave(input.text.toString()) }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun getSaveDirectory() =
        File(
            listOf(
                Environment.getExternalStorageDirectory().absolutePath,
                Environment.DIRECTORY_PICTURES,
                resources.getString(R.string.app_name)
            ).joinToString(File.separator)
        )

    private fun doSave(fileName: String) {
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val saveDir = getSaveDirectory()
            val file = File(saveDir, fileName)

            try {
                saveDir.mkdirs()
                StickerViewSerializer().serialize(viewModel, file)
                viewModel.currentFileName = fileName
                Toast.makeText(this, "Saved to $file", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Timber.e(e, "Error writing %s", file)
                Toast.makeText(this, "Error writing $file", Toast.LENGTH_LONG).show()
            }
        }
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
        viewModel.stickers.value!!.forEach {
            (it as? DrawableSticker)?.cropDestructively(resources)
        }
        binding.stickerView.invalidate()
        Toast.makeText(this, "Cropped all images.", Toast.LENGTH_SHORT).show()
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
        } else {
            // Resize absurdly large images
            val totalSize = bitmap.width * bitmap.height
            val newBitmap = if (totalSize > MAX_SIZE_PIXELS) {
                val scaleFactor: Float = MAX_SIZE_PIXELS.toFloat() / totalSize.toFloat()
                val scaled = Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scaleFactor).toInt(),
                    (bitmap.height * scaleFactor).toInt(),
                    false
                )
                Timber.w(
                    "Scaled huge bitmap, memory savings: %dMB",
                    (bitmap.allocationByteCount - scaled.allocationByteCount) / (1024 * 1024)
                )
                scaled
            } else {
                bitmap
            }

            val drawable = BitmapDrawable(resources, newBitmap)
            viewModel.addSticker(DrawableSticker(drawable))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                INTENT_PICK_IMAGE -> {
                    val selectedImage = data!!.data!!
                    doAddSticker(selectedImage)
                }
                INTENT_PICK_SAVED_FILE -> {
                    val selectedFile = data!!.data!!
                    doLoad(selectedFile)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupButtons() {
        /*binding.buttonOpen.setOnClickListener { load() }

        binding.buttonSave.setOnClickListener { save() }

        binding.buttonSaveAs.setOnClickListener { saveAs() }

        binding.buttonNew.setOnClickListener {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to create a new board?")
                .setPositiveButton("Yes") { _, _ -> newBoard() }
                .setNegativeButton("No", null)
                .show()
        }

        binding.buttonCropAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to crop all images? This will permanently modify the images to match their cropped areas, but it can save  space and improve performance.")
                .setPositiveButton("Yes") { _, _ -> cropAll() }
                .setNegativeButton("No", null)
                .show()
        }*/

        binding.buttonAdd.setOnClickListener {
            addSticker()
        }

        binding.buttonReset.setOnClickListener { viewModel.resetView() }

        binding.buttonDuplicate.setOnClickListener { viewModel.duplicateCurrentSticker() }

        binding.buttonLock.setOnCheckedChangeListener { _, isToggled ->
            viewModel.isLocked.value = isToggled
        }

        binding.buttonCrop.setOnCheckedChangeListener { _, isToggled ->
            viewModel.isCropActive.value = isToggled
        }

        binding.buttonResetZoom.setOnClickListener {
            viewModel.resetCurrentStickerZoom()
        }

        binding.buttonResetCrop.setOnClickListener {
            viewModel.resetCurrentStickerCropping()
        }

        /*
        binding.buttonHideShowUI.setOnCheckedChangeListener { _, isToggled ->
            setUIVisibility(isToggled)
        }*/
        binding.buttonRotate.setOnLongClickListener {
            viewModel.resetCurrentStickerRotation();
            true
        }
    }

    private fun setUIVisibility(isToggled: Boolean) {
        if (isToggled) {
//            binding.toolbarTop.visibility = View.GONE;
//            binding.toolbarBottom.visibility = View.GONE;
//            val top = ContextCompat.getDrawable(this, R.drawable.ic_baseline_visibility_off_24)
//            binding.buttonHideShowUI.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null)
        } else {
//            binding.toolbarTop.visibility = View.VISIBLE;
//            binding.toolbarBottom.visibility = View.VISIBLE;
//            val top = ContextCompat.getDrawable(this, R.drawable.ic_baseline_visibility_24)
//            binding.buttonHideShowUI.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null)
        }
    }

    override fun finish() {
        super.finish()
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Confirm")
            .setMessage("Are you sure you want to quit?")
            .setPositiveButton("Yes") { _, _ -> finish() }
            .setNegativeButton("No", null)
            .show()
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
        override fun onPreExecute() {
            super.onPreExecute()
            //context.binding.progressBarHolder.visibility = View.VISIBLE
        }

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
                            //context.binding.progressBarHolder.visibility = View.GONE
                            return@response
                        }

                        fuel.get(text)
                            .response { _, _, body ->
                                body.fold({
                                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                                    context.doAddSticker(bitmap)
                                    //context.binding.progressBarHolder.visibility =
                                        View.GONE
                                }, {
                                    Toast.makeText(
                                        context,
                                        "Failed to download image: $it",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    //context.binding.progressBarHolder.visibility =
                                        View.GONE
                                    Timber.e(it)
                                })
                            }
                    }, {
                        //context.binding.activityMain.visibility = View.GONE
                        Toast.makeText(context, "Failed to download image", Toast.LENGTH_LONG)
                            .show()
                        Timber.e(it)
                    })
                }
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, "Invalid link", Toast.LENGTH_LONG).show()
                //context.binding.activityMain.visibility = View.GONE
            }
            return null
        }
    }



    internal class MyStickerOperationListener(private val binding: ActivityEmojiMakerBinding) :
        StickerView.OnStickerOperationListener {
        override fun onStickerAdded(sticker: Sticker, direction: Int) {
            binding.stickerView.layoutSticker(sticker, direction)
            binding.stickerView.invalidate()
        }

        override fun onStickerClicked(sticker: Sticker) {
            binding.stickerView.invalidate()
        }

        override fun onStickerDeleted(sticker: Sticker) {
            binding.stickerView.invalidate()
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
    }

    companion object {
        const val PERM_RQST_CODE = 110
        const val SAVE_FILE_EXTENSION: String = "ref"

        const val INTENT_PICK_IMAGE = 1
        const val INTENT_PICK_SAVED_FILE = 2

        const val MAX_SIZE_PIXELS = 2000 * 2000
    }
}