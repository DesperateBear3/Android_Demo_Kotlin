package cc.bear3.android.demo.ui.base.lec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cc.bear3.android.demo.R

/**
 *
 * @author TT
 * @since 2020-12-8
 */
abstract class LecFragment : Fragment() {

    protected lateinit var root: FrameLayout
        private set

    private var loadingView: View? = null
    private var errorView: View? = null

    private val params = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    private val state = MutableLiveData(LecState.Content)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_lec, container, false) as FrameLayout

        params.topMargin = 0
        root.addView(onCreateContentView(inflater, container), params)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLecState(viewLifecycleOwner) {
            when (it) {
                LecState.Loading -> showLoadingLayout()
                LecState.Error -> showErrorLayout()
                LecState.Content -> showContentLayout()
            }
        }
    }

    protected abstract fun onCreateContentView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View

    protected abstract fun onCreateLoadingView(): View

    protected abstract fun onCreateErrorView(): View

    protected open fun onLoadingViewCreated(loadingView: View) {

    }

    protected open fun onErrorViewCreated(errorView: View) {

    }

    protected open fun getTopMargin(lecState: LecState): Int {
        return 0
    }

    protected fun observeLecState(owner: LifecycleOwner, onChanged: (LecState) -> Unit) {
        val wrappedObserver = Observer<LecState> { t -> onChanged.invoke(t) }
        state.observe(owner, wrappedObserver)
    }

    protected fun changeLecState(lecState: LecState) {
        state.value = lecState
    }

    protected open fun showLoadingLayout() {
        dismissErrorLayout()

        val topMargin = getTopMargin(state.value!!)

        if (loadingView == null) {
            loadingView = onCreateLoadingView()

            params.topMargin = topMargin
            root.addView(loadingView, params)
        } else {
            loadingView?.let {
                it.layoutParams = (it.layoutParams as FrameLayout.LayoutParams).apply {
                    this.topMargin = topMargin
                }
            }
        }

        loadingView?.let {
            onLoadingViewCreated(it)
        }
    }

    protected open fun dismissLoadingLayout() {
        loadingView?.let {
            root.removeView(it)
        }
    }

    protected open fun showErrorLayout() {
        dismissLoadingLayout()

        val topMargin = getTopMargin(state.value!!)

        if (errorView == null) {
            errorView = onCreateErrorView()

            params.topMargin = topMargin
            root.addView(errorView, params)
        } else {
            errorView?.let {
                it.layoutParams = (it.layoutParams as FrameLayout.LayoutParams).apply {
                    this.topMargin = topMargin
                }
            }
        }

        errorView?.let {
            onErrorViewCreated(it)
        }
    }

    protected open fun dismissErrorLayout() {
        errorView?.let {
            root.removeView(it)
        }
    }

    protected open fun showContentLayout() {
        dismissLoadingLayout()
        dismissErrorLayout()
    }
}