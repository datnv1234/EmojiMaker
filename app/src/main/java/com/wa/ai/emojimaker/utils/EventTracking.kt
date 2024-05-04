package com.wa.ai.emojimaker.utils

import android.content.Context
import android.os.Bundle
import kotlin.jvm.internal.Intrinsics

/*@Metadata(
    d1 = ["\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b;\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J \u0010?\u001a\u00020@2\u0006\u0010A\u001a\u00020B2\u0006\u0010C\u001a\u00020\u00042\b\b\u0002\u0010D\u001a\u00020ER\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000c\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00102\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00103\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00104\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00105\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00106\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00107\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00108\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u00109\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010:\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010;\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010<\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010=\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010>\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006F"],
    d2 = ["Lcom/emojimaker/emojistitch/utils/EventTracking;", "", "()V", "EVENT_NAME_ADD_TELEGRAM_VIEW", "", "EVENT_NAME_ADD_WHATSAPP_CLICK", "EVENT_NAME_CREATE_EMOJI_CATEGORY_CHOOSE_ITEM", "EVENT_NAME_CREATE_EMOJI_CREATE_ADD_CLICK", "EVENT_NAME_CREATE_EMOJI_CREATE_CLICK", "EVENT_NAME_CREATE_EMOJI_CREATE_DOWNLOAD", "EVENT_NAME_CREATE_EMOJI_CREATE_NEW_PACK_CLICK", "EVENT_NAME_CREATE_EMOJI_CREATE_SAVE_CLICK", "EVENT_NAME_CREATE_EMOJI_CREATE_SHARE", "EVENT_NAME_CREATE_EMOJI_VIEW", "EVENT_NAME_CREATION_DRAFT_DELETE_CLICK", "EVENT_NAME_CREATION_DRAFT_EDIT_CLICK", "EVENT_NAME_CREATION_DRAFT_VIEW", "EVENT_NAME_CREATION_PACKAGE_DELETE", "EVENT_NAME_CREATION_PACKAGE_MORE_CLICK", "EVENT_NAME_CREATION_PACKAGE_NEW_CLICK", "EVENT_NAME_CREATION_PACKAGE_VIEW", "EVENT_NAME_CREATION_PACKAGE_VIEW_LIST", "EVENT_NAME_CREATION_PACKAGE_VIEW_LIST_CREATE", "EVENT_NAME_DOWNLOAD_CLICK", "EVENT_NAME_EDIT_EMOJI_ADD_CLICK", "EVENT_NAME_EDIT_EMOJI_DELETE", "EVENT_NAME_EDIT_EMOJI_DONE_CLICK", "EVENT_NAME_EDIT_EMOJI_DOWNLOAD", "EVENT_NAME_EDIT_EMOJI_SAVE_CLICK", "EVENT_NAME_EDIT_EMOJI_SHARE", "EVENT_NAME_EDIT_EMOJI_VIEW", "EVENT_NAME_HOME_CREATE_CLICK", "EVENT_NAME_HOME_CREATION_CLICK", "EVENT_NAME_HOME_SEARCH_RESULT_VIEW", "EVENT_NAME_HOME_SETTING_CLICK", "EVENT_NAME_HOME_SUGGESTED_PACKAGE_CLICK", "EVENT_NAME_HOME_VIEW", "EVENT_NAME_LANGUAGE_FO_OPEN", "EVENT_NAME_LANGUAGE_FO_SAVE_CLICK", "EVENT_NAME_NO_INTERNET_VIEW", "EVENT_NAME_ONBOARDING1_VIEW", "EVENT_NAME_ONBOARDING2_VIEW", "EVENT_NAME_ONBOARDING3_START_CLICK", "EVENT_NAME_ONBOARDING3_VIEW", "EVENT_NAME_PERMISSION_CONTINUE_CLICK", "EVENT_NAME_PERMISSION_VIEW", "EVENT_NAME_RATE_NOT_NOW", "EVENT_NAME_RATE_SHOW", "EVENT_NAME_RATE_SUBMIT", "EVENT_NAME_SETTING_LANGUAGE_CLICK", "EVENT_NAME_SETTING_PRIVACY_POLICY_CLICK", "EVENT_NAME_SETTING_SHARE_CLICK", "EVENT_NAME_SETTING_VIEW", "EVENT_NAME_SHARE_CLICK", "EVENT_NAME_SPLASH_OPEN", "EVENT_NAME_SUCCESS_MORE_CLICK", "EVENT_NAME_SUCCESS_VIEW", "EVENT_NAME_SUGGESTED_PACKAGE_MORE_CLICK", "EVENT_NAME_SUGGESTED_PACK_CHOOSE_ITEM", "EVENT_NAME_SUGGESTED_PACK_CHOOSE_ITEM_ADD", "EVENT_NAME_SUGGESTED_PACK_CHOOSE_ITEM_DOWNLOAD", "EVENT_NAME_SUGGESTED_PACK_CHOOSE_ITEM_SHARE", "EVENT_NAME_SUGGESTED_PACK_VIEW_LIST", "logEvent", "", "context", "Landroid/content/Context;", "eventName", "params", "Landroid/os/Bundle;", "EmojiMaker1.0.3_04.03.2024_release"],
    k = 1,
    mv = [1, 7, 1],
    xi = 48
)*/ /* compiled from: EventTracking.kt */
class EventTracking private constructor() {
    fun logEvent(context: Context?, str: String?, bundle: Bundle?) {
        Intrinsics.checkNotNullParameter(context, "context")
//        Intrinsics.checkNotNullParameter(str, a.h.k0)
        Intrinsics.checkNotNullParameter(bundle, "params")
    }

    companion object {
        const val EVENT_NAME_ADD_TELEGRAM_VIEW = "add_telegram_click"
        const val EVENT_NAME_ADD_WHATSAPP_CLICK = "add_whatsapp_click"
        const val EVENT_NAME_CREATE_EMOJI_CATEGORY_CHOOSE_ITEM = "create_emoji_category_choose_item"
        const val EVENT_NAME_CREATE_EMOJI_CREATE_ADD_CLICK = "create_emoji_create_add_click"
        const val EVENT_NAME_CREATE_EMOJI_CREATE_CLICK = "create_emoji_create_click"
        const val EVENT_NAME_CREATE_EMOJI_CREATE_DOWNLOAD = "create_emoji_create_download"
        const val EVENT_NAME_CREATE_EMOJI_CREATE_NEW_PACK_CLICK =
            "create_emoji_create_new_pack_click"
        const val EVENT_NAME_CREATE_EMOJI_CREATE_SAVE_CLICK = "create_emoji_create_save_click"
        const val EVENT_NAME_CREATE_EMOJI_CREATE_SHARE = "create_emoji_create_share"
        const val EVENT_NAME_CREATE_EMOJI_VIEW = "create_emoji_view"
        const val EVENT_NAME_CREATION_DRAFT_DELETE_CLICK = "creation_draft_delete_click"
        const val EVENT_NAME_CREATION_DRAFT_EDIT_CLICK = "creation_draft_edit_click"
        const val EVENT_NAME_CREATION_DRAFT_VIEW = "creation_draft_view"
        const val EVENT_NAME_CREATION_PACKAGE_DELETE = "creation_package_delete"
        const val EVENT_NAME_CREATION_PACKAGE_MORE_CLICK = "creation_package_more_click"
        const val EVENT_NAME_CREATION_PACKAGE_NEW_CLICK = "creation_package_new_click"
        const val EVENT_NAME_CREATION_PACKAGE_VIEW = "creation_package_view"
        const val EVENT_NAME_CREATION_PACKAGE_VIEW_LIST = "creation_package_view_list"
        const val EVENT_NAME_CREATION_PACKAGE_VIEW_LIST_CREATE = "creation_package_view_list_create"
        const val EVENT_NAME_DOWNLOAD_CLICK = "download_click"
        const val EVENT_NAME_EDIT_EMOJI_ADD_CLICK = "edit_emoji_add_click"
        const val EVENT_NAME_EDIT_EMOJI_DELETE = "edit_emoji_delete"
        const val EVENT_NAME_EDIT_EMOJI_DONE_CLICK = "edit_emoji_done_click"
        const val EVENT_NAME_EDIT_EMOJI_DOWNLOAD = "edit_emoji_download"
        const val EVENT_NAME_EDIT_EMOJI_SAVE_CLICK = "edit_emoji_save_click"
        const val EVENT_NAME_EDIT_EMOJI_SHARE = "edit_emoji_share"
        const val EVENT_NAME_EDIT_EMOJI_VIEW = "edit_emoji_view"
        const val EVENT_NAME_HOME_CREATE_CLICK = "home_create_click"
        const val EVENT_NAME_HOME_CREATION_CLICK = "home_creation_click"
        const val EVENT_NAME_HOME_SEARCH_RESULT_VIEW = "home_search_result_view"
        const val EVENT_NAME_HOME_SETTING_CLICK = "home_setting_click"
        const val EVENT_NAME_HOME_SUGGESTED_PACKAGE_CLICK = "home_suggested_package_click"
        const val EVENT_NAME_HOME_VIEW = "home_view"
        const val EVENT_NAME_LANGUAGE_FO_OPEN = "language_fo_open"
        const val EVENT_NAME_LANGUAGE_FO_SAVE_CLICK = "language_fo_save_click"
        const val EVENT_NAME_NO_INTERNET_VIEW = "no_internet_view"
        const val EVENT_NAME_ONBOARDING1_VIEW = "onboarding1_view"
        const val EVENT_NAME_ONBOARDING2_VIEW = "onboarding2_view"
        const val EVENT_NAME_ONBOARDING3_START_CLICK = "onboarding3_start_click"
        const val EVENT_NAME_ONBOARDING3_VIEW = "onboarding3_view"
        const val EVENT_NAME_PERMISSION_CONTINUE_CLICK = "permission_continue_click"
        const val EVENT_NAME_PERMISSION_VIEW = "permission_view"
        const val EVENT_NAME_RATE_NOT_NOW = "rate_not_now"
        const val EVENT_NAME_RATE_SHOW = "rate_show"
        const val EVENT_NAME_RATE_SUBMIT = "rate_submit"
        const val EVENT_NAME_SETTING_LANGUAGE_CLICK = "setting_language_click"
        const val EVENT_NAME_SETTING_PRIVACY_POLICY_CLICK = "setting_privacy_policy_click"
        const val EVENT_NAME_SETTING_SHARE_CLICK = "setting_share_click"
        const val EVENT_NAME_SETTING_VIEW = "setting_view"
        const val EVENT_NAME_SHARE_CLICK = "share_click"
        const val EVENT_NAME_SPLASH_OPEN = "splash_open"
        const val EVENT_NAME_SUCCESS_MORE_CLICK = "success_more_click"
        const val EVENT_NAME_SUCCESS_VIEW = "success_view"
        const val EVENT_NAME_SUGGESTED_PACKAGE_MORE_CLICK = "suggested_pack_more_click"
        const val EVENT_NAME_SUGGESTED_PACK_CHOOSE_ITEM = "suggested_pack_choose_item"
        const val EVENT_NAME_SUGGESTED_PACK_CHOOSE_ITEM_ADD = "suggested_pack_choose_item_add"
        const val EVENT_NAME_SUGGESTED_PACK_CHOOSE_ITEM_DOWNLOAD =
            "suggested_pack_choose_item_download"
        const val EVENT_NAME_SUGGESTED_PACK_CHOOSE_ITEM_SHARE = "suggested_pack_choose_item_share"
        const val EVENT_NAME_SUGGESTED_PACK_VIEW_LIST = "suggested_pack_view_list"
        val INSTANCE = EventTracking()
        fun  /* synthetic */`logEvent$default`(
            eventTracking: EventTracking,
            context: Context?,
            str: String?,
            bundle: Bundle?,
            i2: Int,
            obj: Any?
        ) {
            var bundle = bundle
            if (i2 and 4 != 0) {
                bundle = Bundle()
            }
            eventTracking.logEvent(context, str, bundle)
        }
    }
}
