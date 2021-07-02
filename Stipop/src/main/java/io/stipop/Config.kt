package io.stipop

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.math.roundToInt

class Config {
    companion object {

        const val baseUrl = "https://messenger.stipop.io/v1"

        lateinit var apikey: String
        private var stickerIconNormalName = "ic_sticker_normal"

        var useLightMode = true

        var themeBackgroundColor = "#ffffff"
        var themeGroupedContentBackgroundColor = "#f7f8f9"
        var themeMainColor = "#FF5D1E"

        var themeIconColor = "#414141"
        var themeIconTintColor = "#FF5D1E"

        var fontFamily = "system"
        var fontWeight = "regular"
        var fontCharacter = 0
        var fontFace:Typeface? = null

        var searchbarRadius = 10
        var searchNumOfColumns = 3

        var searchTagsHidden = false

        private var searchbarIconName = "ic_sticker_normal"
        private var searchbarDeleteIconName = ""

        var storeListType = ""

        private var storeTrendingUseBackgroundColor = false
        private var storeTrendingBackgroundColor = "#EEEEEE"
        private var storeTrendingOpacity = 0.0

        private var storeDownloadIconName = ""
        private var storeCompleteIconName = ""

        var storeRecommendedTagShow = false

        private var orderIconName = ""
        private var hideIconName = ""

        private var keyboardStoreIconName = ""
        var keyboardNumOfColumns = 3

        lateinit var allowPremium: String
        var pngPrice: Double = 0.0
        var gifPrice: Double = 0.0

        private var detailBackIconName = ""
        private var detailCloseIconName = ""
        var detailNumOfColumns = 3

        var showPreview = false
        var previewPadding = 44

        private var previewFavoritesOnIconName = ""
        private var previewFavoritesOffIconName = ""
        private var previewCloseIconName = ""

        private const val LIGHT_KEY = "light"
        private const val DARK_KEY = "dark"

        fun configure(context: Context) {

            val jsonString = getJsonDataFromAsset(context) ?: return

            try {
                val json = JSONObject(jsonString)
                parse(context, json)
            } catch (e: JSONException) {
                e.printStackTrace()

                println("")
                println("")
                println("==========================================")
                println("Stipop configuration check-out failed.")
                println("==========================================")
                println("")
                println("")
            }
        }

        private fun getJsonDataFromAsset(context: Context): String? {
            val jsonString: String
            try {
                jsonString =
                    context.assets.open("Stipop.json").bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

        private fun parse(context: Context, json: JSONObject) {
            apikey = Utils.getString(json, "api_key")

            val theme = json.optJSONObject("Theme")
            useLightMode = Utils.getBoolen(theme, "useLightMode", true)

            val backgroundColor = theme?.optJSONObject("backgroundColor")
            val groupedContentBackgroundColor = theme?.optJSONObject("groupedContentBackgroundColor")
            val mainColor = theme?.optJSONObject("mainColor")

            val iconColor = theme?.optJSONObject("iconColor")
            val normalColor = iconColor?.optJSONObject("normalColor")
            val tintColor = iconColor?.optJSONObject("tintColor")

            val font = theme?.optJSONObject("font")

            // font
            fontFamily = Utils.getString(font, "family", "system").lowercase()
            fontWeight = Utils.getString(font, "weight", "regular")
            fontCharacter = Utils.getInt(font, "character", 0)

            try {
                fontFace = Typeface.createFromAsset(context.assets, fontFamily)
            } catch (e:Exception) {}

            if (fontFace == null) {
                try {
                    fontFace = Typeface.createFromAsset(context.assets, "$fontFamily.ttf")
                } catch (e:Exception) {}

                if (fontFace == null) {
                    try {
                        fontFace = Typeface.createFromAsset(context.assets, "$fontFamily.otf")
                    } catch (e:Exception) {}
                }
            }



            stickerIconNormalName = Utils.getString(json, "StickerIcon", "ic_sticker_normal")

            val search = json.optJSONObject("Search")

            searchbarRadius = Utils.getInt(search, "searchbarRadius", 10)
            searchNumOfColumns = Utils.getInt(search, "numOfColumns", 3)

            searchbarIconName = Utils.getString(search, "searchbarIcon", "icon_search")
            searchbarDeleteIconName = Utils.getString(search, "searchbarDeleteIcon", "icon_erase")

            val searchTags = search?.optJSONObject("searchTags")
            searchTagsHidden = Utils.getBoolen(searchTags, "hidden", false)

            val liteStore = json.optJSONObject("LiteStore")
            storeListType = Utils.getString(liteStore, "listType", "horizontal")

            val trending = liteStore?.optJSONObject("trending")
            storeTrendingUseBackgroundColor = Utils.getBoolen(trending, "useBackgroundColor", false)
            storeTrendingBackgroundColor = Utils.getString(trending, "backgroundColor", "#eeeeee")
            storeTrendingOpacity = Utils.getDouble(trending, "opacity", 0.7)

            storeDownloadIconName = Utils.getString(liteStore, "downloadIcon", "ic_download")
            storeCompleteIconName = Utils.getString(liteStore, "completeIcon", "ic_downloaded")

            storeRecommendedTagShow = Utils.getString(liteStore, "bottomOfSearch", "recommendedTags") == "recommendedTags"

            val mySticker = json.optJSONObject("MySticker")

            orderIconName = Utils.getString(mySticker, "orderIcon", "ic_move")
            hideIconName = Utils.getString(mySticker, "hideIcon", "ic_hide")

            val keyboard = json.optJSONObject("Keyboard")
            keyboardNumOfColumns = Utils.getInt(keyboard, "numOfColumns", 3)
            keyboardStoreIconName = Utils.getString(keyboard, "liteStoreIcon", "ic_store")

            val storePolicy = json.optJSONObject("StorePolicy")
            allowPremium = Utils.getString(storePolicy, "allowPremium", "N")

            val price = storePolicy?.optJSONObject("price")
            pngPrice = Utils.getDouble(price, "png", 0.99)
            gifPrice = Utils.getDouble(price, "gif", 1.99)

            val sticker = json.optJSONObject("Sticker")

            detailBackIconName = Utils.getString(sticker, "backIcon", "ic_back")
            detailCloseIconName = Utils.getString(sticker, "closeIcon", "ic_close")
            detailNumOfColumns = Utils.getInt(sticker, "numOfColumns", 3)

            val send = json.getJSONObject("Send")
            showPreview = Utils.getBoolen(send, "preview")
            previewPadding = Utils.getInt(send, "previewPadding", 100)

            val previewFavoritesOnIcon = send.getJSONObject("favoritesOnIcon")
            val previewFavoritesOffIcon = send.getJSONObject("favoritesOffIcon")
            val previewCloseIcon = send.getJSONObject("closeIcon")

            if (useLightMode) {
                themeBackgroundColor = Utils.getString(backgroundColor, LIGHT_KEY, "#FFFFFF")
                themeGroupedContentBackgroundColor = Utils.getString(groupedContentBackgroundColor, LIGHT_KEY, "#F7F8F9")
                themeMainColor = Utils.getString(mainColor, LIGHT_KEY, "#FF501E")
                themeIconColor = Utils.getString(normalColor, LIGHT_KEY, "#414141")
                themeIconTintColor = Utils.getString(normalColor, LIGHT_KEY, this.themeMainColor)

                previewFavoritesOnIconName = Utils.getString(previewFavoritesOnIcon, LIGHT_KEY, "ic_favorites_on")
                previewFavoritesOffIconName = Utils.getString(previewFavoritesOffIcon, LIGHT_KEY, "ic_favorites_off")
                previewCloseIconName = Utils.getString(previewCloseIcon, LIGHT_KEY, "ic_cancel")
            } else {
                themeBackgroundColor = Utils.getString(backgroundColor, DARK_KEY, "#171B1C")
                themeGroupedContentBackgroundColor = Utils.getString(groupedContentBackgroundColor, DARK_KEY, "#2E363A")
                themeMainColor = Utils.getString(mainColor, DARK_KEY, "#FF8558")
                themeIconColor = Utils.getString(normalColor, DARK_KEY, "#646F7C")
                themeIconTintColor = Utils.getString(normalColor, DARK_KEY, this.themeMainColor)

                previewFavoritesOnIconName = Utils.getString(previewFavoritesOnIcon, DARK_KEY, "ic_favorites_on")
                previewFavoritesOffIconName = Utils.getString(previewFavoritesOffIcon, DARK_KEY, "ic_favorites_off")
                previewCloseIconName = Utils.getString(previewCloseIcon, DARK_KEY, "ic_cancel")
            }
        }

        fun getKeyboardStoreResourceId(context: Context): Int {
            return if (keyboardStoreIconName.isNotEmpty()) {
                Utils.getResource(keyboardStoreIconName, context)
            } else {
                if (useLightMode) {
                    R.mipmap.ic_store
                } else {
                    R.mipmap.ic_store_dark
                }
            }
        }

        fun getSearchbarResourceId(context: Context): Int {
            return if (searchbarIconName.isNotEmpty()) {
                Utils.getResource(searchbarIconName, context)
            } else {
                if (useLightMode) {
                    R.mipmap.icon_search
                } else {
                    R.mipmap.icon_search_dark
                }
            }
        }

        fun getEraseResourceId(context: Context): Int {
            return if (searchbarDeleteIconName.isNotEmpty()) {
                Utils.getResource(searchbarDeleteIconName, context)
            } else {
                if (useLightMode) {
                    R.mipmap.icon_erase
                } else {
                    R.mipmap.icon_erase_dark
                }
            }
        }

        fun getDownloadIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_download
            if (storeDownloadIconName.isNotEmpty()) {
                imageId = Utils.getResource(storeDownloadIconName, context)
            }
            return imageId
        }

        fun getCompleteIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_downloaded
            if (storeCompleteIconName.isNotEmpty()) {
                imageId = Utils.getResource(storeCompleteIconName, context)
            }
            return imageId
        }

        fun getOrderIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_move
            if (orderIconName.isNotEmpty()) {
                imageId = Utils.getResource(orderIconName, context)
            }
            return imageId
        }

        fun getAddIconResourceId(): Int {
            var imageId = R.mipmap.add_3
            if (!useLightMode) {
                imageId = R.mipmap.ic_add_dark
            }
            return imageId
        }

        fun getHideIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_hide
            if (hideIconName.isNotEmpty()) {
                imageId = Utils.getResource(hideIconName, context)
            }
            return imageId
        }

        fun getBackIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_back
            if (detailBackIconName.isNotEmpty()) {
                imageId = Utils.getResource(detailBackIconName, context)
            }
            return imageId
        }

        fun getCloseIconResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_close
            if (detailCloseIconName.isNotEmpty()) {
                imageId = Utils.getResource(detailCloseIconName, context)
            }
            return imageId
        }

        fun getPreviewFavoriteResourceId(context: Context, favorite: Boolean): Int {
            var imageId = R.mipmap.ic_favorites_off
            if (previewFavoritesOffIconName.isNotEmpty()) {
                imageId = Utils.getResource(previewFavoritesOffIconName, context)
            }
            if (favorite) {
                imageId = R.mipmap.ic_favorites_on
                if (previewFavoritesOnIconName.isNotEmpty()) {
                    imageId = Utils.getResource(previewFavoritesOnIconName, context)
                }
            }
            return imageId
        }

        fun getPreviewCloseResourceId(context: Context): Int {
            var imageId = R.mipmap.ic_cancel
            if (previewCloseIconName.isNotEmpty()) {
                imageId = Utils.getResource(previewCloseIconName, context)
            }
            return imageId
        }

        fun getErrorImage(): Int {
            var imageId = R.mipmap.error
            if (!useLightMode) {
                imageId = R.mipmap.error_dark
            }
            return imageId
        }

        fun getUnderLineColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_f7f8f9)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_2e363a)
            }
            return color
        }

        fun getStoreNavigationTextColor(context: Context, selected: Boolean): Int {
            return if (selected) {
                if (!useLightMode) {
                    ContextCompat.getColor(context, R.color.c_f3f4f5)
                } else {
                    ContextCompat.getColor(context, R.color.c_374553)
                }
            } else {
                if (!useLightMode) {
                    ContextCompat.getColor(context, R.color.c_646f7c)
                } else {
                    ContextCompat.getColor(context, R.color.c_c6c8cf)
                }
            }
        }

        fun getTitleTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_646f7c)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_c6c8cf)
            }
            return color
        }

        fun getAllStickerPackageNameTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_374553)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_f7f8f9)
            }
            return color
        }

        fun getMyStickerHiddenPackageNameTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_000000)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_646f7c)
            }
            return color
        }

        fun getMyStickerHiddenArtistNameTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_8f8f8f)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_646f7c)
            }
            return color
        }

        fun getActiveStickerBackgroundColor(context: Context): Int {
            var color = Color.parseColor(themeMainColor)
            if (useLightMode) {
                val mainColor = themeMainColor.replace("#", "")

                color = Color.parseColor("#33$mainColor")
            }
            return color
        }

        fun getHiddenStickerBackgroundColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_eaebee)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_2e363a)
            }
            return color
        }

        fun getActiveHiddenStickerTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_374553)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_ffffff)
            }
            return color
        }

        fun getMovingBackgroundColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_f7f8f9)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_25292a)
            }
            return color
        }

        fun getAlertBackgroundColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_ffffff)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_4a4a4a)
            }
            return color
        }

        fun getAlertTitleTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_121212)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_d3d3d3)
            }
            return color
        }

        fun getAlertContentsTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_5f5f5f)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_e1e1e1)
            }
            return color
        }

        fun getAlertButtonTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_2d8cbf)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_5f97f6)
            }
            return color
        }

        fun getDetailPackageNameTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_000000)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_f7f8f9)
            }
            return color
        }

        fun getSearchTitleTextColor(context: Context): Int {
            var color = ContextCompat.getColor(context, R.color.c_374553)
            if (!useLightMode) {
                color = ContextCompat.getColor(context, R.color.c_c6c8cf)
            }
            return color
        }

        fun setStoreTrendingBackground(context: Context, drawable: GradientDrawable): Int {
            var color = ContextCompat.getColor(context, R.color.c_eeeeee)

            if (storeTrendingUseBackgroundColor) {
                color = Color.parseColor(storeTrendingBackgroundColor)
            }

            drawable.setColor(color)
            drawable.alpha = (storeTrendingOpacity * 255).roundToInt()

            return color
        }

    }
}