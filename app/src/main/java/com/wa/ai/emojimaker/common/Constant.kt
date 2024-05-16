package com.wa.ai.emojimaker.common

object Constant {

    const val TAG = "datnv"
    const val BUFSIZE = 8192
    var DEFAULT_SERVER_PORT = 54613
    //Intent
    const val KEY_PASS_PATH_TO_DETAIL_IMAGE = "KEY_PASS_PATH_TO_DETAIL_IMAGE"
    const val KEY_PASS_FROM_PLAYLIST = "KEY_PASS_FROM_PLAYLIST"
    const val KEY_PASS_NAME_TO_DETAIL_IMAGE = "KEY_PASS_NAME_TO_DETAIL_IMAGE"
    const val KEY_PASS_PATH_TO_DETAIL_VIDEO = "KEY_PASS_PATH_TO_DETAIL_VIDEO"
    const val KEY_PASS_NAME_TO_DETAIL_VIDEO = "KEY_PASS_NAME_TO_DETAIL_VIDEO"
    const val KEY_PASS_NAME_TO_DETAIL_PLAYLIST = "KEY_PASS_NAME_TO_DETAIL_PLAYLIST"
    const val KEY_PASS_ID_TO_DETAIL_PLAYLIST = "KEY_PASS_ID_TO_DETAIL_PLAYLIST"
    const val KEY_PATH_SOUND = "KEY_PATH_SOUND"
    const val CHANGE_SOUND_CURRENT = 1
    const val COMPLETE_PLAY_SOUND = 2
    const val START_SOUND = 3
    const val STOP_SOUND = 4
    const val RUN_NEW_SOUND = "RUN_NEW_SOUND"
    const val SEEK_TO = "SEEK_TO"
    const val KEY_PASS_ID_SEARCH_PLAYLIST_DETAIL = "KEY_PASS_ID_SEARCH_PLAYLIST_DETAIL"

    //EventBus
    const val EVENT_NET_WORK_CHANGE = 21
    const val EVENT_GET_LIST_TV = 22
    const val EVENT_CONNECT_TV_SUCCESS = 23
    const val EVENT_CONNECT_TV_FAILED = 24
    const val EVENT_UPDATE_VOLUME_CHANGE = 25


    const val APP_ID = "3A9B611C"


    const val KEY_IS_RATE = "isRate"
    val DATABASE_NAME: String = "ReflectTVDatabaseName"
    const val SHARED_PREFERENCES_NAME = "SharedPreferencesDatabase"
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

    const val EVENT_UPDATE_PLAYLIST_SIZE = 0

    const val NAME_DEVICE = "NAME_DEVICE"

    //Local storage
    const val INTERNAL_MY_CREATIVE_DIR = "my_creative"

    const val CREATE_STICKER_DELAY = 3000L

}