package com.wa.ai.emojimaker.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.FragmentSettingsBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment

class SettingsFragment : BaseBindingFragment<FragmentSettingsBinding, SettingsViewModel>() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

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