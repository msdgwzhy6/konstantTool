package com.konstant.tool.lite.base

import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.konstant.tool.lite.R
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_h5.*
import kotlinx.android.synthetic.main.pop_h5.view.*
import kotlinx.android.synthetic.main.title_layout.*


/**
 * 时间：2019/5/5 12:10
 * 创建：吕卡
 * 描述：H5页面
 */

class H5Activity : BaseActivity() {

    companion object {
        private val TAG = "H5Activity"
        val H5_URL = "url"
        val H5_BROWSER = "browser"
    }

    private lateinit var mPop: PopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("加载中...")
        setContentView(R.layout.activity_h5)
        initBaseViews()
        val url = intent.getStringExtra(H5_URL)
        val browser = intent.getBooleanExtra(H5_BROWSER, false)
        if (browser) {
            view_web.openOnBrowser(url)
            finish()
            return
        }
        Log.d(TAG, url)
        view_web.loadUrl(url)
    }

    override fun initBaseViews() {
        super.initBaseViews()
        window.setFormat(PixelFormat.TRANSLUCENT)
        img_more.apply {
            visibility = View.VISIBLE
            setOnClickListener { onMorePressed() }
        }
        view_web.apply {
            registerTitleChanged(::setTitle)
            registerProgressChanged {
                if (it == 100) {
                    view_progress.visibility = View.GONE
                } else {
                    view_progress.visibility = View.VISIBLE
                    view_progress.progress = it
                }
            }
        }
    }

    private fun onMorePressed() {
        val view = layoutInflater.inflate(R.layout.pop_h5, null)
        view.tv_refresh.setOnClickListener { mPop.dismiss();onRefresh() }
        view.tv_browser.setOnClickListener { mPop.dismiss();view_web.openOnBrowser() }
        mPop = PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
        mPop.showAsDropDown(title_bar)
    }

    private fun onRefresh() {
        view_web.reload()
    }

    private fun withBrowser(url: String = view_web.url) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (view_web.onBackPressed()) return
        super.onBackPressed()
    }

    override fun onDestroy() {
        view_web.onDestroy()
        super.onDestroy()
    }
}
