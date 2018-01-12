package com.konstant.konstanttools.ui.activity.toolactivity.weather

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.alibaba.fastjson.JSON
import com.konstant.konstanttools.R
import com.konstant.konstanttools.base.BaseActivity
import com.konstant.konstanttools.tools.FileUtils
import com.konstant.konstanttools.ui.adapter.AdapterViewpager
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.title_layout.*

class WeatherActivity : BaseActivity() {

    private val mFragmentList = mutableListOf<Fragment>()
    private var mSavedInstanceState: Bundle? = null
    private val mAdapter by lazy { AdapterViewpager(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSavedInstanceState = savedInstanceState
        setContentView(R.layout.activity_weather)
        setTitle("天气查询")
        initBaseViews()
    }

    override fun initBaseViews() {
        super.initBaseViews()
        img_more.visibility = View.VISIBLE
        img_more.setOnClickListener { startActivity(Intent(this, CityManagerActivity::class.java)) }
        layout_viewpager.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()

        val weatherCodeList = readLocalCityList()

        mFragmentList.clear()
        mFragmentList.add(WeatherFragment.newInstance(""))
        weatherCodeList.forEach {
            mFragmentList.add(WeatherFragment.newInstance("CN$it"))
        }
        mAdapter.updateFragmentList(mFragmentList)
        Log.i("mFragmentList size","${mFragmentList.size}")

    }

    private fun readLocalCityList(): List<String> {
        val list = ArrayList<String>()
        val bytes: ByteArray? = FileUtils.readFileFromInnerStorage(this, "cityList.json")
        bytes?.let {
            val cityList = JSON.parseArray(String(bytes), DirectData.ProvinceBean.CityBean.CountyBean::class.java)
            cityList.forEach { list.add(it.weatherCode) }
        }
        return list
    }

}


