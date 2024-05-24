package com.wa.ai.emojimaker.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.databinding.FragmentSettingsBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.DialogRating
import com.wa.ai.emojimaker.ui.main.MainActivity
import com.wa.ai.emojimaker.ui.multilang.MultiLangActivity
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.hideSystemUI

class SettingsFragment : BaseBindingFragment<FragmentSettingsBinding, SettingsViewModel>() {

    private lateinit var mMainActivity: MainActivity
    private val ratingDialog: DialogRating by lazy {
        DialogRating().apply {
            onRating = {
                toast(getString(R.string.thank_you))
                ratingDialog.dismiss()
            }
            onClickFiveStar = {
                binding.rate.gone()
                binding.viewLineRate.gone()
                toast(getString(R.string.thank_you))
                ratingDialog.dismiss()
            }

            onDismiss = {
                activity?.hideSystemUI()
            }
        }
    }

    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig


    override val title: String
        get() = getString(R.string.settings)

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

        binding.language.setOnClickListener {
            val intent = Intent(requireContext(), MultiLangActivity::class.java)
            intent.putExtra(Constant.TYPE_LANG, Constant.TYPE_LANGUAGE_SETTING)
            mMainActivity.openNextScreen {
                startActivity(intent)
            }
        }

        binding.share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share my application")
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${requireContext().packageName}"
            )
            mMainActivity.openNextScreen {
                startActivity(Intent.createChooser(shareIntent, "Share the application via"))
            }
        }

        binding.rate.setOnClickListener {
            mMainActivity.openNextScreen {
                ratingDialog.show(parentFragmentManager, null)
            }
            //openPlayStoreForRating()
        }

        binding.about.setOnClickListener {
            mMainActivity.openNextScreen {
                Intent(Intent.ACTION_VIEW, Uri.parse(this.getString(R.string.privacy_policy_link))).apply {
                    startActivity(this)
                }
            }
        }
    }

    override fun setupData() {
        mMainActivity = activity as MainActivity
    }

    override val layoutId: Int
        get() = R.layout.fragment_settings

    override fun getViewModel(): Class<SettingsViewModel> = SettingsViewModel::class.java
    override fun registerOnBackPress() {

    }

    override fun onStart() {
        super.onStart()
        setUpLoadInterAds()
    }

    private fun setUpLoadInterAds() {
        mMainActivity.keyAds = mMainActivity.mFirebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_INTER_SETTINGS)
        if (mMainActivity.keyAds.isEmpty()) {
            mMainActivity.keyAds = getString(R.string.inter_settings)
        }
        if (mMainActivity.mFirebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_SETTINGS)) {
            mMainActivity.loadInterAds(mMainActivity.keyAds)
        }
    }

}