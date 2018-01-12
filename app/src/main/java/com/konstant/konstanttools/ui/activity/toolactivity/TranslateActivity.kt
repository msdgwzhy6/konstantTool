package com.konstant.konstanttools.ui.activity.toolactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.konstant.konstanttools.R
import com.konstant.konstanttools.base.BaseActivity
import kotlinx.android.synthetic.main.activity_translate.*

class TranslateActivity : BaseActivity() {

    private val languageNames by lazy { resources.getStringArray(R.array.translate_type_name) }
    private val languageShorts by lazy { resources.getStringArray(R.array.translate_type_short) }

    private var typeFrom = ""
    private var typeTo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)
        setTitle("翻译")
        initBaseViews()
    }

    override fun initBaseViews() {
        super.initBaseViews()
        layout_bg.setOnClickListener { hideSoftKeyboard() }

        // 初始化左边的spinner
        val adapterOrigin = object : ArrayAdapter<String>(this, R.layout.item_spinner_bg, languageNames) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val root = LayoutInflater.from(context).inflate(R.layout.item_spinner_pull_down_bg, parent, false)
                val tv = root.findViewById(R.id.text_label) as TextView
                tv.text = languageNames[position]
                return root
            }
        }
        adapterOrigin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_origin.adapter = adapterOrigin
        spinner_origin.setSelection(0)
        spinner_origin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                typeFrom = languageShorts[position]
                hideSoftKeyboard()
            }
        }


        // 初始化右边的spinner
        val typeName = mutableListOf<String>()
        (1 until languageNames.size).forEach { typeName.add(languageNames[it]) }
        val adapterResult = object : ArrayAdapter<String>(this, R.layout.item_spinner_bg, typeName) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val root = LayoutInflater.from(context).inflate(R.layout.item_spinner_pull_down_bg, parent, false)
                val tv = root.findViewById(R.id.text_label) as TextView
                tv.text = typeName[position]
                return root
            }
        }

        adapterResult.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_result.adapter = adapterResult
        spinner_result.setSelection(0)

    }
}
