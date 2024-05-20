package com.wa.ai.emojimaker.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.FragmentSettingsBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.dialog.DialogRating
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.hideSystemUI

class SettingsFragment : BaseBindingFragment<FragmentSettingsBinding, SettingsViewModel>() {

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
        get() = getString(R.string.app_name)

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
    }

    override fun setupData() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_settings

    override fun getViewModel(): Class<SettingsViewModel> = SettingsViewModel::class.java
    override fun registerOnBackPress() {

    }

}