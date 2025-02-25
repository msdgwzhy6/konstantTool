package com.konstant.tool.lite.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.konstant.tool.lite.R
import com.konstant.tool.lite.data.bean.main.Function
import com.konstant.tool.lite.main.FunctionCollectorManager
import com.konstant.tool.lite.main.MainActivity
import com.konstant.tool.lite.module.compass.CompassActivity
import com.konstant.tool.lite.module.concentration.ConcentrationActivity
import com.konstant.tool.lite.module.date.DateCalculationActivity
import com.konstant.tool.lite.module.decibel.DecibelActivity
import com.konstant.tool.lite.module.deviceinfo.DeviceInfoActivity
import com.konstant.tool.lite.module.express.activity.ExpressListActivity
import com.konstant.tool.lite.module.extract.PackageActivity
import com.konstant.tool.lite.module.live.TVLiveActivity
import com.konstant.tool.lite.module.parse.ParseVideoActivity
import com.konstant.tool.lite.module.qrcode.QRCodeActivity
import com.konstant.tool.lite.module.rolltxt.RollTextActivity
import com.konstant.tool.lite.module.ruler.RulerActivity
import com.konstant.tool.lite.module.setting.SettingManager
import com.konstant.tool.lite.module.setting.activity.SettingActivity
import com.konstant.tool.lite.module.speed.NetSpeedActivity
import com.konstant.tool.lite.module.translate.TranslateActivity
import com.konstant.tool.lite.module.wallpaper.WallpaperActivity
import com.konstant.tool.lite.module.weather.activity.WeatherActivity
import com.konstant.tool.lite.module.wxfake.WechatFakeActivity
import com.konstant.tool.lite.util.AppUtil
import com.konstant.tool.lite.util.Density
import com.konstant.tool.lite.view.KonstantDialog
import com.konstant.tool.lite.view.KonstantPagerIndicator
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.item_drawer_left.view.*
import kotlinx.android.synthetic.main.layout_drawer_left.*
import kotlinx.android.synthetic.main.title_layout.*
import kotlinx.android.synthetic.main.title_layout.view.*
import me.imid.swipebacklayout.lib.SwipeBackLayout
import me.imid.swipebacklayout.lib.app.SwipeBackActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * 描述:所有activity的基类
 * 创建人:菜籽
 * 创建时间:2018/4/4 上午11:08
 * 备注:
 */

@SuppressLint("MissingSuperCall")
abstract class BaseActivity : SwipeBackActivity() {

    private val mToast by lazy { Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT) }
    protected val mDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setDefaultLanguage()
        AppUtil.addActivity(this)
    }

    override fun setContentView(layoutResID: Int) {
        setTheme(SettingManager.getTheme(this))
        super.setContentView(R.layout.activity_base)
        layoutInflater.inflate(layoutResID, base_content, true)
        initUserInterface()
    }

    private fun initUserInterface() {
        // 沉浸状态栏
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 隐藏导航栏
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = run {
                    val value = TypedValue()
                    theme.resolveAttribute(R.attr.tool_main_color, value, true)
                    value.data
                }
            }
        }

        // 滑动返回
        onSwipeBackChanged(SwipeBackStatus(SettingManager.getSwipeBackStatus(this)))
        initDrawLayout()
        onUserHeaderChanged(UserHeaderChanged())
    }

    open fun setDefaultLanguage() {
        if (SettingManager.getShowChinese(this)) {
            setChineseLanguage()
        } else {
            setEnglishLanguage()
        }
    }

    private fun setChineseLanguage() {
        Locale.setDefault(Locale("zh"))
        val config = resources.configuration
        config.locale = Locale.CHINESE
        resources.updateConfiguration(config, Density.getDisplayMetrics(this))
    }

    private fun setEnglishLanguage() {
        Locale.setDefault(Locale("en"))
        val config = resources.configuration
        config.locale = Locale.ENGLISH
        resources.updateConfiguration(config, Density.getDisplayMetrics(this))
    }

    // 更换主题，主题切换时，重新打开自身，避免界面闪烁
    @Subscribe
    open fun onThemeChanged(msg: ThemeChanged) {
        recreate()
    }

    override fun recreate() {
        if (AppUtil.isTop(this)) {
            finish()
            startActivity(javaClass)
            overridePendingTransition(R.anim.anim_activity_enter, R.anim.activity_anim_exit)
        } else {
            super.recreate()
        }
    }

    @Subscribe
    fun onLanguageChanged(msg: LanguageChanged) {
        recreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_NO -> {
                recreate()
            }
        }
    }

    // 是否启用侧滑手势
    fun setDrawerLayoutStatus(status: Boolean) {
        if (status) {
            draw_layout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            draw_layout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    // 是否启用滑动返回
    @Subscribe
    open fun onSwipeBackChanged(msg: SwipeBackStatus) {
        swipeBackLayout.apply {
            setEnableGesture(true)
            setDrawerLayoutStatus(true)
            when (msg.state) {
                0 -> {
                    setEnableGesture(false)
                }
                1 -> {
                    setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
                    setDrawerLayoutStatus(false)
                }
                2 -> {
                    setEdgeTrackingEnabled(SwipeBackLayout.EDGE_RIGHT)
                }
                3 -> {
                    setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM)
                }
                4 -> {
                    setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL)
                }
            }
        }
    }

    // 用户头像发生了变化
    @Subscribe
    open fun onUserHeaderChanged(msg: UserHeaderChanged) {
        val bitmap = SettingManager.getUserHeaderThumb(this)
        with(RoundedBitmapDrawableFactory.create(resources, bitmap)) {
            paint.isAntiAlias = true
            cornerRadius = Math.max(bitmap.width.toFloat(), bitmap.height.toFloat())
            drawer_header.setImageDrawable(this)
        }
    }

    protected open fun initBaseViews() {
        view_status_bar.height = getStatusBarHeight()
        title_bar.setOnClickListener { hideSoftKeyboard() }
        title_bar.img_back.setOnClickListener { finish() }
        base_content.setOnClickListener { hideSoftKeyboard() }
    }

    // 设置主标题
    fun setTitle(title: String) {
        main_title.visibility = View.VISIBLE
        view_segment.visibility = View.GONE
        main_title.text = title
    }

    // 分段标题
    fun setSegmentalTitle(vararg title: String) {
        main_title.visibility = View.GONE
        view_segment.visibility = View.VISIBLE
        view_segment.setText(*title)
    }

    fun setSubTitle(subTitle: String) {
        val view = findViewById(R.id.title_bar)
        val textView = view.findViewById(R.id.sub_title) as TextView
        view.findViewById<KonstantPagerIndicator>(R.id.title_indicator).visibility = View.GONE
        textView.text = subTitle
        textView.visibility = View.VISIBLE
    }

    // 显示、隐藏 主标题
    protected fun showTitleBar(status: Boolean = true) {
        title_bar.visibility = if (status) View.VISIBLE else View.GONE
    }

    // 显示、隐藏 状态栏
    protected fun showStatusBar(status: Boolean = true) {
        if (status) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    // 隐藏软键盘
    protected fun hideSoftKeyboard() {
        if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            return
        }
        if (currentFocus == null) {
            return
        }
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS)
    }

    // 显示软键盘
    protected fun showKeyboard(editText: EditText) {
        editText.requestFocus()
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        Handler().postDelayed({ inputManager.showSoftInput(editText, 0) }, 50)
    }

    // 滚动界面，防止输入法遮挡视图
    protected fun addLayoutListener(main: View, scroll: View) {
        main.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            //1、获取main在窗体的可视区域
            main.getWindowVisibleDisplayFrame(rect)
            //2、获取main在窗体的不可视区域高度，在键盘没有弹起时，main.getRootView().getHeight()调节度应该和rect.bottom高度一样
            val mainInvisibleHeight = main.rootView.height - rect.bottom
            val screenHeight = main.rootView.height//屏幕高度
            //3、不可见区域大于屏幕本身高度的1/4：说明键盘弹起了
            if (mainInvisibleHeight > screenHeight / 4) {
                val location = IntArray(2)
                scroll.getLocationInWindow(location)
                // 4､获取Scroll的窗体坐标，算出main需要滚动的高度
                val srollHeight = location[1] + scroll.height - rect.bottom
                //5､让界面整体上移键盘的高度
                main.scrollTo(0, srollHeight)
            } else {
                //3、不可见区域小于屏幕高度1/4时,说明键盘隐藏了，把界面下移，移回到原有高度
                main.scrollTo(0, 0)
            }
        }
    }

    // 展示吐司
    fun showToast(msg: String) {
        runOnUiThread {
            showLoading(state = false)
            mToast.apply { setText(msg);show() }
        }
    }

    fun cancelToast() {
        runOnUiThread { mToast.cancel() }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        AppUtil.removeActivity(this)
        mDisposable.dispose()
        super.onDestroy()
    }

    protected fun startActivity(cls: Class<*>) {
        if (draw_layout.isDrawerOpen(Gravity.LEFT)) {
            draw_layout.closeDrawers()
        }
        startActivity(Intent(this, cls))
    }

    fun startActivitySafely(intent: Intent?): Boolean {
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    // 初始化侧边栏
    private fun initDrawLayout() {
        val config = assets.open("MainFunction.json").bufferedReader().readText()
        val configs = Gson().fromJson<List<Function>>(config, object : TypeToken<List<Function>>() {}.type)
        val adapter = AdapterBase(configs)
        adapter.setOnItemClickListener { _, position ->
            val type = configs[position].type
            startActivityWithType(type)
        }
        adapter.setOnItemLongClickListener { _, position ->
            KonstantDialog(this)
                    .setMessage("${getString(R.string.base_collection)}'${configs[position].title}'${getString(R.string.base_function)}？")
                    .setPositiveListener {
                        FunctionCollectorManager.addCollectionFunction(configs[position])
                        showToast(getString(R.string.base_collection_success))
                    }
                    .createDialog()
        }
        recycler_view.apply {
            setLayoutManager(LinearLayoutManager(this@BaseActivity, LinearLayoutManager.VERTICAL, false))
            setAdapter(adapter)
        }

        text_mian.setOnClickListener { startActivity(MainActivity::class.java) }

        text_setting.setOnClickListener { startActivity(SettingActivity::class.java) }

    }

    // 获取状态栏高度
    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    // 显示加载窗口
    fun showLoading(state: Boolean, msg: String = getString(R.string.base_loading)) {
        runOnUiThread {
            tv_state.text = msg
            layout_loading.visibility = if (state) View.VISIBLE else View.GONE
        }
    }

    fun startActivityWithType(type: String) {
        when (type) {
            "1" -> {
                startActivity(TranslateActivity::class.java)
            }
            "2" -> {
                startActivity(RulerActivity::class.java)
            }
            "3" -> {
                startActivity(CompassActivity::class.java)
            }
            "4" -> {
                startActivity(ExpressListActivity::class.java)
            }
            "5" -> {
                startActivity(NetSpeedActivity::class.java)
            }
            "6" -> {
                startActivity(WeatherActivity::class.java)
            }
            "7" -> {
                startActivity(DateCalculationActivity::class.java)
            }
            "8" -> {
                startActivity(WallpaperActivity::class.java)
            }
            "9" -> {
                startActivity(ConcentrationActivity::class.java)
            }
            "10" -> {
                startActivity(DecibelActivity::class.java)
            }
            "11" -> {
                startActivity(TVLiveActivity::class.java)
            }
            "12" -> {
                startActivity(PackageActivity::class.java)
            }
            "13" -> {
                startActivity(WechatFakeActivity::class.java)
            }
            "14" -> {
                startActivity(RollTextActivity::class.java)
            }
            "15" -> {
                startActivity(DeviceInfoActivity::class.java)
            }
            "16" -> {
                startActivity(QRCodeActivity::class.java)
            }
            "17" -> {
                startActivity(ParseVideoActivity::class.java)
            }
        }
    }

    inner class AdapterBase(private val configs: List<Function>) : BaseRecyclerAdapter<AdapterBase.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drawer_left, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            super.onBindViewHolder(holder, position)
            holder.itemView.tv_title.text = configs[position].title
        }

        override fun getItemCount() = configs.size

        inner class Holder(view: View) : RecyclerView.ViewHolder(view)
    }
}