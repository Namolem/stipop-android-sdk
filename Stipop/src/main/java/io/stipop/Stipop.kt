package io.stipop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import io.stipop.activity.DetailActivity
import io.stipop.activity.Keyboard
import io.stipop.activity.StoreActivity

class Stipop(private val activity: Activity, private val stipopButton: ImageView) {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance:Stipop? = null

        var userId = -1
        var lang = "en"
        var countryCode = "us"

        var keyboardHeight = 0

        fun configure(context:Context) {
            Config.configure(context)
        }

        fun connect(activity: Activity, stipopButton:ImageView, userId:Int, lang: String, countryCode:String) {

            Stipop.userId = userId
            Stipop.lang = lang
            Stipop.countryCode = countryCode

            if (instance == null) {
                instance = Stipop(activity, stipopButton)
            }

            instance!!.connect()
        }

        fun show() {
            if (instance == null) {
                return
            }

            instance!!.show()
        }

        fun detail(packageId: Int) {
            if (instance == null) {
                return
            }

            instance!!.detail(packageId)
        }
    }


    private lateinit var rootView: View


    private var connected = false
    private var stickerIconEnabled = false

    fun connect() {
        this.stipopButton.setImageResource(R.mipmap.ic_sticker_normal)

        this.connected = true

        this.rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View

        this.setSizeForSoftKeyboard()
    }

    fun show() {
        if (!this.connected) {
            return
        }

        if (this.stickerIconEnabled) {
            this.showKeyboard()
        } else {
            this.enableStickerIcon()

            val intent = Intent(this.activity, StoreActivity::class.java)
            this.activity.startActivity(intent)
        }


    }

    fun detail(packageId: Int) {
        if (!this.connected) {
            return
        }

        val intent = Intent(this.activity, DetailActivity::class.java)
        intent.putExtra("packageId", packageId)
        this.activity.startActivity(intent)
    }

    private fun enableStickerIcon() {
        if (this.connected) {
            this.stipopButton.setImageResource(R.mipmap.ic_sticker_active)

            this.stickerIconEnabled = true
        }
    }

    private fun showKeyboard() {
        Keyboard.show(this.activity)
    }


    private fun setSizeForSoftKeyboard() {

        this.rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            this.rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = getUsableScreenHeight()
            var heightDifference = (screenHeight - (r.bottom - r.top))
            val resourceId: Int = this.activity.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                heightDifference -= this.activity.resources.getDimensionPixelSize(resourceId)
            }

            if (heightDifference > 100) {
                keyboardHeight = heightDifference
            }
        }
    }

    private fun getUsableScreenHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val metrics = DisplayMetrics()
            val windowManager = this.activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metrics)
            metrics.heightPixels
        } else {
            this.rootView.rootView.height
        }
    }

}