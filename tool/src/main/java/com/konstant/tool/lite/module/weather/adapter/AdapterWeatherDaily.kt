package com.konstant.tool.lite.module.weather.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.konstant.tool.lite.R
import com.konstant.tool.lite.network.response.WeatherResponse
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

/**
 * 描述:逐天预报的适配器
 * 创建人:菜籽
 * 创建时间:2018/4/6 下午10:24
 * 备注:
 */

class AdapterWeatherDaily(private val datas: List<WeatherResponse.WeatherBean>) : RecyclerView.Adapter<AdapterWeatherDaily.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather_days, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = datas.size


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = datas[position]
        val tWeeak = holder.view.findViewById(R.id.tv_weather_week) as TextView
        val tDate = holder.view.findViewById(R.id.tv_weather_date)as TextView
        val tInfo = holder.view.findViewById(R.id.tv_weather_info)as TextView
        val tTemp = holder.view.findViewById(R.id.tv_weather_temperature)as TextView
        val tDriect = holder.view.findViewById(R.id.tv_weather_direct)as TextView
        val tPower = holder.view.findViewById(R.id.tv_weather_power)as TextView
        val img = holder.view.findViewById(R.id.img_weather_icon)as ImageView

        val date = SimpleDateFormat("yyyy-MM-dd").parse(data.date)
//        val date = Date()
        val week = SimpleDateFormat("E").format(date)

        // 设置周几
        tWeeak.text = week

        // 设置日期
        val split = data.date.replace("-", "/").split("/")
        tDate.text = "${split[1]}/${split[2]}"

        // 设置多云转晴
        if (data.info.day[1] == data.info.night[1]) {
            tInfo.text = data.info.day[1]
        } else {
            tInfo.text = "${data.info.day[1]}转${data.info.night[1]}"
        }

        // 设置温度
        tTemp.text = "${data.info.day[2]}~${data.info.night[2]}℃"

        // 设置风向
        tDriect.text = data.info.day[3]

        // 设置风级
        tPower.text = data.info.day[4]

        // 设置图片
        when (data.info.day[0].toInt()) {
            0 -> {
                Picasso.get().load(R.drawable.weather_img_1).into(img)
            }
            1 -> {
                Picasso.get().load(R.drawable.weather_img_2).into(img)
            }
            2 -> {
                Picasso.get().load(R.drawable.weather_img_3).into(img)
            }
            3 -> {
                Picasso.get().load(R.drawable.weather_img_4).into(img)
            }
            4 -> {
                Picasso.get().load(R.drawable.weather_img_5).into(img)
            }
            5 -> {
                Picasso.get().load(R.drawable.weather_img_6).into(img)
            }
            6 -> {
                Picasso.get().load(R.drawable.weather_img_7).into(img)
            }
            7 -> {
                Picasso.get().load(R.drawable.weather_img_8).into(img)
            }
            8 -> {
                Picasso.get().load(R.drawable.weather_img_9).into(img)
            }
            9 -> {
                Picasso.get().load(R.drawable.weather_img_10).into(img)
            }
            10 -> {
                Picasso.get().load(R.drawable.weather_img_11).into(img)
            }
            11 -> {
                Picasso.get().load(R.drawable.weather_img_12).into(img)
            }
            12 -> {
                Picasso.get().load(R.drawable.weather_img_13).into(img)
            }
            13 -> {
                Picasso.get().load(R.drawable.weather_img_14).into(img)
            }
            14 -> {
                Picasso.get().load(R.drawable.weather_img_15).into(img)
            }
            15 -> {
                Picasso.get().load(R.drawable.weather_img_16).into(img)
            }
            30 -> {
                Picasso.get().load(R.drawable.weather_img_17).into(img)
            }
            17 -> {
                Picasso.get().load(R.drawable.weather_img_18).into(img)
            }
        }
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}