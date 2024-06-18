package com.wa.ai.emojimaker.common

object Constant {


    const val PERMISSION_REQUEST_CODE = 200
    const val WAITING_TO_LOAD_BANNER = 5000L

    const val TAG = "datnv"
    const val ADS = "ads"

    //Intent

    //EventBus
    const val EVENT_NET_WORK_CHANGE = 21

    const val KEY_IS_RATE = "isRate"
    const val KEY_FIRST_SHOW_INTRO = "KEY_FIRST_SHOW_INTRO"

    const val TYPE_SHOW_INTRO_ACT = 2
    const val TYPE_SHOW_LANGUAGE_ACT = 3
    const val TYPE_SHOW_PERMISSION = 4


    const val KEY_CLICK_GO = "KEY_CLICK_GO"

    const val TYPE_LANG = "MultiLangAct_Lang"

    const val TYPE_LANGUAGE_SPLASH = 1
    const val TYPE_LANGUAGE_SETTING = 2
    const val KEY_INSERT_PLAY_LIST = 3


    const val PREF_SETTING_LANGUAGE = "pref_setting_language"
    const val PREF_LANGUAGE_CURRENT = "PREF_LANGUAGE_CURRENT"

    const val PREF_LANGUAGE_DEFAULT_DEVICE = "PREF_LANGUAGE_DEFAULT_DEVICE"
    const val PREF_CHECK_SET_LANGUAGE_DEFAULT = "PREF_CHECK_SET_LANGUAGE_DEFAULT"
    const val WRITE_REQUEST_CODE = 1

    const val TYPE_PHOTO = 0
    const val TYPE_MUSIC = 1
    const val TYPE_VIDEO = 2
    const val TYPE_FILE = 3
    const val KEY_MEDIA = "keyMedia"
    const val TYPE_DIALOG_ASCENDING = "TYPE_DIALOG_ASCENDING"
    const val REQUEST_PERMISSION_CODE = 1
    const val REQUEST_MEDIA_IMAGE = 10
    const val REQUEST_MEDIA_VIDEO = 11
    const val REQUEST_MEDIA_MUSIC = 12
    const val REQUEST_PERMISSION_PHOTO = 0
    const val REQUEST_PERMISSION_VIDEO = 1
    const val REQUEST_PERMISSION_MUSIC = 2

    //Local storage
    const val INTERNAL_MY_CREATIVE_DIR = "my_creative"
    const val INTERNAL_ITEM_OPTIONS_DIR = "item_options"

    const val CREATE_STICKER_DELAY = 3000L

    //Category
    val categories = mapOf(
        "cat_chic" to "Cat Chic",
        "orange_orchard" to "Orange Orchard",
        "funny_rat" to "Funny rat",
        "pet_pawtentials" to "Pet Pawtentials",
        "dog_diversity" to "Dog Diversity",
        "sly_spirits" to "Sly Spirits",
        "xiximi" to "Xiximi",
        "funny_cat" to "Funny Cat",
        "quacking_quacks" to "Quacking Quacks",
        "emoji" to "Emoji",
        "brainy_endeavors" to "Brainy Endeavors",
        "couple_emoji" to "Couple emoji",
        "cute_girl" to "Cute girl")

    const val ACCESSORIES = "accessories"
    const val BEARD = "beard"
    const val BROW = "brow"
    const val EYES = "eyes"
    const val FACE = "face"
    const val GLASS = "glass"
    const val HAIR = "hair"
    const val HAND = "hand"
    const val HAT = "hat"
    const val MOUTH = "mouth"
    const val NOSE = "nose"
}