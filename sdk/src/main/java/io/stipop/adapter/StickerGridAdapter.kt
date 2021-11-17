package io.stipop.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.stipop.models.SPSticker
import io.stipop.models.Sticker
import io.stipop.models.StickerPackage
import io.stipop.viewholder.StickerThumbViewHolder

internal class StickerGridAdapter(
    val delegate: OnStickerClickListener? = null,
    private val dataSet: ArrayList<SPSticker> = ArrayList()
) :
    RecyclerView.Adapter<StickerThumbViewHolder>() {

    interface OnStickerClickListener {
        fun onStickerClick(position: Int, spSticker: SPSticker)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StickerThumbViewHolder {
        return StickerThumbViewHolder.create(parent, delegate)
    }

    override fun onBindViewHolder(holder: StickerThumbViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        dataSet.clear()
        notifyDataSetChanged()
    }

    fun updateDatas(stickerPackage: StickerPackage) {
        val prevCount = itemCount
        dataSet.addAll(stickerPackage.stickers)
        notifyItemRangeInserted(prevCount, itemCount)
    }

    fun updateDatas(stickers: List<SPSticker>) {
        val prevCount = itemCount
        dataSet.addAll(stickers)
        notifyItemRangeInserted(prevCount, itemCount)
    }

    fun updateData(sticker: Sticker) {
        val prevCount = itemCount
        dataSet.add(sticker.toSPSticker())
        notifyItemRangeInserted(prevCount, itemCount)
    }

    fun updateFavorite(stickerId: Int, favoriteYN: String): SPSticker? {
        var target: SPSticker? = null
        run loop@{
            dataSet.forEachIndexed { index, spSticker ->
                if (spSticker.stickerId == stickerId) {
                    spSticker.favoriteYN = favoriteYN
                    dataSet[index] = spSticker
                    notifyItemChanged(index)
                    target = spSticker
                    return@loop
                }
            }
        }
        return target
    }


}