package com.wa.ai.emojimaker.ui.emojimaker

import android.content.Context
import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.local.dao.EmojiDao
import com.wa.ai.emojimaker.data.model.IconModel
import com.wa.ai.emojimaker.data.model.OptionsModel
import com.wa.ai.emojimaker.databinding.ActivityEmojiMakerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.EventTracking
import com.wa.ai.emojimaker.utils.customview.customsticker.Sticker
import com.wa.ai.emojimaker.utils.customview.customsticker.StickerView
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.visible
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.jvm.internal.Intrinsics
import kotlin.reflect.KClass

/*@Metadata(
    d1 = ["\u0000\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001B\u0005¢\u0006\u0002\u0010\u0004J\b\u0010\u001e\u001a\u00020\u001fH\u0002J \u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\t2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020#H\u0002J\b\u0010%\u001a\u00020\u001fH\u0016J\u0010\u0010&\u001a\u00020\u001f2\u0006\u0010'\u001a\u00020(H\u0002J\b\u0010)\u001a\u00020\u001fH\u0002J\u000e\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00020+H\u0016J\b\u0010,\u001a\u00020\u001fH\u0002J\b\u0010-\u001a\u00020\u001fH\u0002J\b\u0010.\u001a\u00020#H\u0016J\b\u0010/\u001a\u00020\u001fH\u0002J\b\u00100\u001a\u00020\u001fH\u0016J\u0018\u00101\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020#H\u0002J\b\u00102\u001a\u00020\u001fH\u0016J\u0018\u00103\u001a\u00020\u001f2\u000e\u00104\u001a\n\u0012\u0002\b\u0003\u0012\u0002\b\u000305H\u0016J\b\u00106\u001a\u00020\u001fH\u0002J\b\u00107\u001a\u00020\u001fH\u0002J \u00108\u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\t2\u0006\u00109\u001a\u00020#2\u0006\u0010$\u001a\u00020#H\u0002J\b\u0010:\u001a\u00020\u001fH\u0002J&\u0010;\u001a\u00020\u001f2\n\u00104\u001a\u0006\u0012\u0002\b\u00030<2\b\u0010=\u001a\u0004\u0018\u00010>2\u0006\u0010?\u001a\u00020@H\u0016J4\u0010A\u001a\u00020\u001f2\u000c\u0010B\u001a\b\u0012\u0004\u0012\u00020\u00150\b2\u000c\u0010C\u001a\b\u0012\u0004\u0012\u00020(0D2\u0006\u0010E\u001a\u00020\u001d2\u0006\u0010F\u001a\u00020@H\u0002R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000c\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X.¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X.¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX.¢\u0006\u0002\n\u0000¨\u0006G"],
    d2 = ["Lcom/emojimaker/emojistitch/ui/emoji/EmojiNewScreenActivity;", "Lcom/emojimaker/emojistitch/ui/base/BaseActivity;", "Lcom/emojimaker/emojistitch/ui/emoji/EmojiViewModel;", "Lcom/emojimaker/emojistitch/databinding/ActivityNewScreenEmojiBinding;", "()V", "emojiDaoNew", "Lcom/emojimaker/emojistitch/database/EmojiDao;", "listAccessoryNew", "", "Lcom/emojimaker/emojistitch/data/model/IconModel;", "listBeardNew", "listBrowNew", "listEyesNew", "listFaceNew", "listGlassNew", "listHairNew", "listHandNew", "listHatNew", "listMouthNew", "listNoseNew", "listOptionsNew", "Lcom/emojimaker/emojistitch/data/model/OptionsModel;", "loadingDialogNew", "Lcom/emojimaker/emojistitch/dialog/LoadingNewScreenDialog;", "optionsAdapterNew", "Lcom/emojimaker/emojistitch/ui/emoji/OptionsAdapter;", "packageDaoNew", "Lcom/emojimaker/emojistitch/database/PackageDao;", "pagerIconAdapterNew", "Lcom/emojimaker/emojistitch/ui/emoji/PagerIconAdapter;", "addDataNew", "", "addStickerNew", "iconModel", "i", "", "pager", "bindViewModel", "checkShowLockOrUnLockNew", "sticker", "Lcom/emojimaker/emojistitch/utils/custom_sticker/Sticker;", "clickLockStickerNew", "createViewModel", "Ljava/lang/Class;", "disableCreateNew", "enableCreateNew", "getContentView", "hideOptionNew", "initView", "logEventClickIconNew", "onBackPressed", "onFragmentResumed", "fragment", "Lcom/emojimaker/emojistitch/ui/base/BaseFragment;", "showBottomSheetLayerNew", "showDialogQuitNew", "showDialogUnlockItemsNew", "pos", "showOptionNew", "switchFragment", "Lkotlin/reflect/KClass;", "bundle", "Landroid/os/Bundle;", "addToBackStack", "", "updateNotificationIconNew", "options", "result", "", "adapter", "isSelect", "EmojiMaker1.0.3_04.03.2024_release"],
    k = 1,
    mv = [1, 7, 1],
    xi = 48
) */
/* compiled from: EmojiNewScreenActivity.kt */
class EmojiNewScreenActivity : BaseBindingActivity<ActivityEmojiMakerBinding, EmojiMakerViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_emoji_maker

    override fun getViewModel(): Class<EmojiMakerViewModel> = EmojiMakerViewModel::class.java


    var `_$_findViewCache`: MutableMap<Int?, View?> = LinkedHashMap<Any?, Any?>()

    /* access modifiers changed from: private */
    var emojiDaoNew: EmojiDao? = null

    /* access modifiers changed from: private */
    var listAccessoryNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listBeardNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listBrowNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listEyesNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listFaceNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listGlassNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listHairNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listHandNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listHatNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listMouthNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listNoseNew: List<IconModel?> = ArrayList()

    /* access modifiers changed from: private */
    var listOptionsNew: List<OptionsModel?> = ArrayList()

    /* access modifiers changed from: private */
    var loadingDialogNew: LoadingNewScreenDialog? = null

    /* access modifiers changed from: private */
    var optionsAdapterNew: OptionsAdapter? = null

    /* access modifiers changed from: private */
    var packageDaoNew: PackageDao? = null

    /* access modifiers changed from: private */
    var pagerIconAdapterNew: PagerIconAdapter? = null
    fun `_$_clearFindViewByIdCache`() {
        `_$_findViewCache`.clear()
    }

    override fun setupView(savedInstanceState: Bundle?) {

    }

    override fun setupData() {

    }

    fun `_$_findCachedViewById`(i2: Int): View? {
        val map = `_$_findViewCache`
        val view = map[Integer.valueOf(i2)]
        if (view != null) {
            return view
        }
        val findViewById: View = findViewById(i2) ?: return null
        map[Integer.valueOf(i2)] = findViewById
        return findViewById
    }

    fun switchFragment(kClass: KClass<*>?, bundle: Bundle?, z: Boolean) {
        Intrinsics.checkNotNullParameter(kClass, "fragment")
    }

    fun initView() {
        val context: Context = this
        EventTracking.`logEvent$default`(
            EventTracking.INSTANCE,
            context,
            EventTracking.EVENT_NAME_CREATE_EMOJI_VIEW,
            null as Bundle?,
            4,
            null as Any?
        )
        loadingDialogNew = LoadingNewScreenDialog(this)
        val instance: AppDatabase = AppDatabase.Companion.getInstance(context)
        var optionsAdapter: OptionsAdapter? = null
        packageDaoNew = if (instance != null) instance.packageNameDao() else null
        val instance2: AppDatabase = AppDatabase.Companion.getInstance(context)
        emojiDaoNew = if (instance2 != null) instance2.emojiDao() else null
        disableCreateNew()
        addDataNew()
        binding.stickerView.setLocked(false)
        binding.stickerView.setConstrained(true)
        /*binding.stickerView.setOnStickerOperationListener(
//            `EmojiNewScreenActivity$initView$1`(this)
        )*/
        pagerIconAdapterNew = PagerIconAdapter(
            context,
            listOptionsNew,
            `EmojiNewScreenActivity$initView$2`(this),
            `EmojiNewScreenActivity$initView$3`(this)
        )
        binding.vpIcon.setUserInputEnabled(false)
        val viewPager2: ViewPager2 = binding.vpIcon
        var pagerIconAdapter: PagerIconAdapter? = pagerIconAdapterNew
        if (pagerIconAdapter == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pagerIconAdapterNew")
            pagerIconAdapter = null
        }
        viewPager2.setAdapter(pagerIconAdapter)
        val recyclerView: RecyclerView = binding.rvOptions
        val optionsAdapter2 =
            OptionsAdapter(context, listOptionsNew, `EmojiNewScreenActivity$initView$4$1`(this))
        optionsAdapterNew = optionsAdapter2
        if (optionsAdapter2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("optionsAdapterNew")
        } else {
            optionsAdapter = optionsAdapter2
        }
        recyclerView.setAdapter(optionsAdapter)
    }

    fun bindViewModel() {
        val imageView: ImageView = binding.ivBack
        Intrinsics.checkNotNullExpressionValue(imageView, "binding.ivBack")
        ViewExKt.tapAndCheckInternet(imageView, `EmojiNewScreenActivity$bindViewModel$1`(this))
        val imageView2: ImageView = binding.ivFlipHorizontal
        Intrinsics.checkNotNullExpressionValue(imageView2, "binding.ivFlipHorizontal")
        ViewExKt.tapAndCheckInternet(imageView2, `EmojiNewScreenActivity$bindViewModel$2`(this))
        val imageView3: ImageView = binding.ivFlipVertical
        Intrinsics.checkNotNullExpressionValue(imageView3, "binding.ivFlipVertical")
        ViewExKt.tapAndCheckInternet(imageView3, `EmojiNewScreenActivity$bindViewModel$3`(this))
        val relativeLayout: RelativeLayout = binding.rlEmoji
        Intrinsics.checkNotNullExpressionValue(relativeLayout, "binding.rlEmoji")
        ViewExKt.tapAndCheckInternet(relativeLayout, `EmojiNewScreenActivity$bindViewModel$4`(this))
        val relativeLayout2: RelativeLayout =
            binding.rlDelete
        Intrinsics.checkNotNullExpressionValue(relativeLayout2, "binding.rlDelete")
        ViewExKt.tapAndCheckInternet(
            relativeLayout2,
            `EmojiNewScreenActivity$bindViewModel$5`(this)
        )
        val relativeLayout3: RelativeLayout = binding.rlLock
        Intrinsics.checkNotNullExpressionValue(relativeLayout3, "binding.rlLock")
        ViewExKt.tapAndCheckInternet(
            relativeLayout3,
            `EmojiNewScreenActivity$bindViewModel$6`(this)
        )
        val imageView4: ImageView = binding.ivRefresh
        Intrinsics.checkNotNullExpressionValue(imageView4, "binding.ivRefresh")
        ViewExKt.tapAndCheckInternet(imageView4, `EmojiNewScreenActivity$bindViewModel$7`(this))
        val imageView5: ImageView = binding.ivMore
        Intrinsics.checkNotNullExpressionValue(imageView5, "binding.ivMore")
        ViewExKt.tapAndCheckInternet(imageView5, `EmojiNewScreenActivity$bindViewModel$8`(this))
        val textView: TextView = binding.tvCreate
        Intrinsics.checkNotNullExpressionValue(textView, "binding.tvCreate")
        ViewExKt.tapAndCheckInternet(textView, `EmojiNewScreenActivity$bindViewModel$9`(this))
        val relativeLayout4: RelativeLayout =
            binding.rlRecent
        Intrinsics.checkNotNullExpressionValue(relativeLayout4, "binding.rlRecent")
        ViewExKt.tapAndCheckInternet(
            relativeLayout4,
            `EmojiNewScreenActivity$bindViewModel$10`(this)
        )
    }

    /* access modifiers changed from: private */
    fun checkShowLockOrUnLockNew(sticker: Sticker) {
        if (sticker.isLock) {
            binding.ivLockEmoji.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_lock_emoji, this.theme)
            )
        } else {
            binding.ivLockEmoji.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_unlock_emoji, this.theme)
            )
        }
    }

    /* access modifiers changed from: private */
    fun clickLockStickerNew() {
        if (binding.stickerView.isLockCurrent) {
            binding.stickerView.setLockedCurrent(false)
            binding.ivLockEmoji.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_unlock_emoji, this.theme)
            )
            return
        }
        binding.stickerView.setLockedCurrent(true)
        binding.ivLockEmoji.setImageDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_lock_emoji, this.theme)
        )
    }

    /* access modifiers changed from: private */
    fun updateNotificationIconNew(
        list: List<OptionsModel?>?,
        list2: List<Sticker?>?,
        pagerIconAdapter: PagerIconAdapter?,
        z: Boolean
    ) {
        val unused: Job = BuildersKt__Builders_commonKt.`launch$default`(
            this.lifecycleScope,
            getDefault.getDefault(),
            null as CoroutineStart?,
            `EmojiNewScreenActivity$updateNotificationIconNew$1`(
                list,
                list2,
                z,
                pagerIconAdapter,
                null as Continuation<`EmojiNewScreenActivity$updateNotificationIconNew$1`?>?
            ),
            2,
            null as Any?
        )
    }

    private fun showDialogQuitNew() {
        ExitEmojiNewScreenDialog(
            this,
            `EmojiNewScreenActivity$showDialogQuitNew$dialogExitEmoji$1`(this),
            `EmojiNewScreenActivity$showDialogQuitNew$dialogExitEmoji$2`(this)
        ).show()
    }

    /* access modifiers changed from: private */
    fun hideOptionNew() {
        binding.rlLock.gone()
    }

    /* access modifiers changed from: private */
    fun showOptionNew() {
        binding.rlLock.visible()
    }

    /* access modifiers changed from: private */
    fun disableCreateNew() {
        binding.tvCreate.isEnabled = false
        binding.tvCreate.alpha = 0.3f
    }

    /* access modifiers changed from: private */
    fun showBottomSheetLayerNew() {
        binding.stickerView.unSelectStickerCurrent()
        val stickerView: StickerView = binding.stickerView
        Intrinsics.checkNotNullExpressionValue(stickerView, "binding.stickerView")
        val stickers: List<Sticker?> =
            binding.stickerView.getStickers()
        Intrinsics.checkNotNull(
            stickers,
            "null cannot be cast to non-null type java.util.ArrayList<com.emojimaker.emojistitch.utils.custom_sticker.Sticker>{ kotlin.collections.TypeAliasesKt.ArrayList<com.emojimaker.emojistitch.utils.custom_sticker.Sticker> }"
        )
        LayerEmojiBottomSheet(
            this,
            stickerView,
            stickers as ArrayList<*>,
            `EmojiNewScreenActivity$showBottomSheetLayerNew$layerEmojiBottomSheet$1`.INSTANCE,
            `EmojiNewScreenActivity$showBottomSheetLayerNew$layerEmojiBottomSheet$2`(this)
        ).show(supportFragmentManager, "LayerEmojiBottomSheet")
    }

    /* access modifiers changed from: private */
    fun showDialogUnlockItemsNew(iconModel: IconModel?, i2: Int, i3: Int) {
        UnlockItemNewScreenDialog(
            this,
            null as Function0<*>?,
            `EmojiNewScreenActivity$showDialogUnlockItemsNew$unlockItemDialog$1`(
                this,
                iconModel,
                i2,
                i3
            ),
            2,
            null as DefaultConstructorMarker?
        ).show()
    }

    /* access modifiers changed from: private */
    fun enableCreateNew() {
        binding.tvCreate.isEnabled = true
        binding.tvCreate.alpha = 1.0f
    }

    /* access modifiers changed from: private */
    fun addStickerNew(iconModel: IconModel, i2: Int, i3: Int) {
        val drawableSticker = DrawableSticker(
            DataExKt.convertPhotoAssetToDrawable(this, iconModel.getPath()),
            iconModel.getPath()
        )
        drawableSticker.setPagerSelect(i3)
        drawableSticker.setPosSelect(i2)
        binding.stickerView.addSticker(drawableSticker)
        iconModel.isSelect = true
        Log.d("PagerShapeEmoji", "Pos. pager: $i3 , pos: $i2")
    }

    private fun addDataNew() {
        val unused: Job = BuildersKt__Builders_commonKt.`launch$default`(
            this.lifecycleScope,
            null as CoroutineContext?,
            null as CoroutineStart?,
            `EmojiNewScreenActivity$addDataNew$1`(
                this,
                null as Continuation<`EmojiNewScreenActivity$addDataNew$1`?>?
            ),
            3,
            null as Any?
        )
    }

    /* access modifiers changed from: private */
    fun logEventClickIconNew(i2: Int, i3: Int) {
        val bundle = Bundle()
        bundle.putString("category", listOptionsNew[i3]?.getNameEvent())
        bundle.putString(
            FirebaseAnalytics.Param.ITEM_ID,
            listOptionsNew[i3]?.getListIcon()?.get(i2)?.getPath()
        )
        EventTracking.INSTANCE.logEvent(
            this,
            EventTracking.EVENT_NAME_CREATE_EMOJI_CATEGORY_CHOOSE_ITEM,
            bundle
        )
    }

    override fun finish() {
        super.finish()
        if (binding.stickerView.stickerCount > 0) {
            showDialogQuitNew()
        } else {
            finish()
        }
    }
}
