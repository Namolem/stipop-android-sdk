package io.stipop.refactor.present.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import io.stipop.databinding.ItemKeywordBinding
import io.stipop.refactor.domain.entities.SPKeywordItem

class SearchKeywordAdapter() :
    ViewBindingAdapter<SPKeywordItem, ItemKeywordBinding>() {

    class SearchKeywordViewHolder(binding: ItemKeywordBinding) :
        ViewBindingHolder<SPKeywordItem, ItemKeywordBinding>(binding) {
        override fun onBind(item: SPKeywordItem) {
            binding.keyword.text = item.keyword
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingHolder<SPKeywordItem, ItemKeywordBinding> {
        binding = ItemKeywordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchKeywordViewHolder(binding)
    }
}