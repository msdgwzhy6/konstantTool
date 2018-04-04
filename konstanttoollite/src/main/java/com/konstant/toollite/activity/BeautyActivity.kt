package com.konstant.toollite.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.konstant.toollite.R
import com.konstant.toollite.adapter.AdapterBeauty
import com.konstant.toollite.base.BaseActivity
import com.konstant.toollite.util.NetworkUtil
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout
import kotlinx.android.synthetic.main.activity_beauty.*
import org.json.JSONObject

class BeautyActivity : BaseActivity() {

    private val mUrlList = ArrayList<String>()
    private val mBaseUrl = "https://hot.browser.miui.com/opg-api/item?gid="
    private var mPageIndex = (Math.random() * 1000 + 2807480).toInt()
    private var isPullDown = true
    private val mAdapter by lazy { AdapterBeauty(mUrlList, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beauty)
        setTitle("美女图片")
        initBaseViews()
    }

    override fun initBaseViews() {
        super.initBaseViews()

        // 刷新监听
        refresh_layout.setHeaderView(BezierLayout(this))
        refresh_layout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                isPullDown = true
                mPageIndex -= 2
                getData()
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                isPullDown = false
                mPageIndex += 2
                getData()
            }
        })

        recycler_beauty.layoutManager = LinearLayoutManager(this)
        recycler_beauty.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        recycler_beauty.adapter = mAdapter

        // 进来时就刷新
        refresh_layout.startRefresh()
    }


    // 获取网络数据
    private fun getData() {
        NetworkUtil.get(mBaseUrl + mPageIndex, "") { state, data ->
            Log.i("MIUI图片", data)
            if (data.isNullOrEmpty() or (data.length < 150)) {
                mPageIndex += (Math.random() * 8 - 4).toInt()
                getData()
                return@get
            }
            refreshUI(parseUrlList(data))
        }
    }

    // 手动解析，整合数据
    private fun parseUrlList(string: String): ArrayList<String> {
        val urlList = ArrayList<String>()
        val obj = JSONObject(string)
        val array = obj.optJSONArray("data")

        (0 until array.length()).map {
            urlList.add(array.optJSONObject(it).optString("url"))
        }

        return urlList
    }

    // 刷新界面
    private fun refreshUI(urlList: ArrayList<String>) {
        Log.i("beauey","开始刷新界面")
        runOnUiThread {
            if (isPullDown) {
                mUrlList.clear()
                refresh_layout.finishRefreshing()
            } else {
                refresh_layout.finishLoadmore()
            }
            mUrlList.addAll(urlList)
            mAdapter.notifyDataSetChanged()
        }
    }
}
