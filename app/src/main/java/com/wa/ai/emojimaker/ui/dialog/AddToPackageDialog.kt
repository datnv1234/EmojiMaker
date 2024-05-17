package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.DialogAddToPackageBinding
import com.wa.ai.emojimaker.evenbus.CreatePackageEvent
import com.wa.ai.emojimaker.ui.adapter.PackageAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AddToPackageDialog : BaseBindingDialogFragment<DialogAddToPackageBinding>() {


    lateinit var save: ((pkg: PackageModel?) -> Unit)
    lateinit var createNewPackage: ((binding : DialogAddToPackageBinding) -> Unit)

    override val layoutId: Int
        get() = R.layout.dialog_add_to_package

    lateinit var viewModel: AddToPackageViewModel

    private val packageAdapter : PackageAdapter by lazy {
        PackageAdapter()
    }
    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AddToPackageViewModel::class.java]
        viewModel.getPackage(requireContext())
        viewModel.packageMutableLiveData.observe(this) {
            packageAdapter.submitList(it.toMutableList())
        }
        binding.rvPackage.adapter = packageAdapter
        setup()
    }

    private fun getPackage() : PackageModel? = packageAdapter.getCurrentPackage()

    private fun setup() {
        binding.btnCreateNewPackage.setOnSafeClick {
            createNewPackage.invoke(binding)
        }
        binding.btnSave.setOnSafeClick {
            dismiss()
            save.invoke(getPackage())
        }
        binding.bg.setOnSafeClick {
            dismiss()
        }
        binding.btnClose.setOnSafeClick {
            dismiss()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCreatePackageEvent(event: CreatePackageEvent) {
        dismiss()
    }
}