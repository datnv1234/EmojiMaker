package com.wa.ai.emojimaker.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class BaseBindingFragment<B : ViewDataBinding, T : BaseViewModel> : BaseFragment() {
    lateinit var binding: B
    open lateinit var viewModel: T
    private var lastClickTime: Long = 0

    protected abstract fun getViewModel(): Class<T>
    protected abstract fun registerOnBackPress()
    abstract val layoutId: Int
    abstract val title: String
    private var toast: Toast? = null
    private var handlerToast = Handler(Looper.getMainLooper())

    @SuppressLint("ShowToast")
    fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast?.show()
        handlerToast.postDelayed({
            toast?.cancel()
        }, 1500)
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerToast.removeCallbacksAndMessages(null)

    }

    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    protected abstract fun setupData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        initAction()
        setupData()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[getViewModel()]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreatedView(view, savedInstanceState)
    }

    private val callBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            registerOnBackPress()
        }
    }

    fun initSheet(view: View): BottomSheetBehavior<*> {
        return BottomSheetBehavior.from<View>(view)
    }

    open fun initAction(){}
}