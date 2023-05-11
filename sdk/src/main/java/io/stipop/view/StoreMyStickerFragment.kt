package io.stipop.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Config
import io.stipop.Constants
import io.stipop.R
import io.stipop.Stipop
import io.stipop.adapter.MyLoadStateAdapter
import io.stipop.adapter.PagingMyPackAdapter
import io.stipop.base.BaseFragment
import io.stipop.base.Injection
import io.stipop.custom.DragAndDropHelperCallback
import io.stipop.databinding.FragmentMyStickerBinding
import io.stipop.event.MyPackEventDelegate
import io.stipop.event.PackageDownloadEvent
import io.stipop.event.PackageVisibilityChangeEvent
import io.stipop.models.StickerPackage
import io.stipop.s_auth.SMSFGetMyStickersReRequestDelegate
import io.stipop.view.viewmodel.StoreMyStickerViewModel
import kotlinx.android.synthetic.main.fragment_my_sticker.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class StoreMyStickerFragment : BaseFragment(), MyPackEventDelegate, SMSFGetMyStickersReRequestDelegate {

    companion object {
        fun newInstance() = Bundle().let { StoreMyStickerFragment().apply { arguments = it } }
        private const val LAST_VISIBLE_SETTING: String = "last_visible_setting"
        private const val DEFAULT_VISIBLE = true

        var smsfGetMyStickersReRequestDelegate: SMSFGetMyStickersReRequestDelegate? = null
    }

    private var binding: FragmentMyStickerBinding? = null
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val pagingMyPackAdapter: PagingMyPackAdapter by lazy { PagingMyPackAdapter(PagingMyPackAdapter.ViewType.STORE, this) }
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return try {
            binding = FragmentMyStickerBinding.inflate(inflater, container, false)
            binding!!.root
        } catch(exception: Exception){
            Stipop.trackError(exception)
            binding = FragmentMyStickerBinding.inflate(inflater, container, false)
            binding!!.root
        }
    }

    override fun onDestroyView() {
        StoreMyStickerFragment.smsfGetMyStickersReRequestDelegate = null
        super.onDestroyView()
        binding = null
        Stipop.storeMyStickerViewModel = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            StoreMyStickerFragment.smsfGetMyStickersReRequestDelegate = this
            Stipop.storeMyStickerViewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(
                StoreMyStickerViewModel::class.java
            )

            myStickersRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = pagingMyPackAdapter.withLoadStateFooter(footer = MyLoadStateAdapter { pagingMyPackAdapter.retry() })
            }

            val wantVisibleSticker = savedInstanceState?.getBoolean(LAST_VISIBLE_SETTING) ?: DEFAULT_VISIBLE
            toggleMyStickers(wantVisibleSticker)
            initRequest(wantVisibleSticker)

            itemTouchHelper = ItemTouchHelper(DragAndDropHelperCallback(pagingMyPackAdapter)).apply { attachToRecyclerView(myStickersRecyclerView) }

            Stipop.storeMyStickerViewModel?.packageVisibilityChanged?.observeForever {
                pagingMyPackAdapter.refresh()
                PackageVisibilityChangeEvent.publishEvent(it.second)
            }

            PackageDownloadEvent.liveData.observe(viewLifecycleOwner) {
                pagingMyPackAdapter.refresh()
                binding?.myStickersRecyclerView?.scrollToPosition(0)
            }
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    override fun applyTheme() {
        try {
            stickerVisibleToggleTextView.setTextColor(
                Config.getActiveHiddenStickerTextColor(
                    requireContext()
                )
            )
            stickerVisibleToggleTextView.setBackgroundColor(
                Config.getHiddenStickerBackgroundColor(
                    requireContext()
                )
            )
        } catch(exception: Exception){
            Stipop.trackError(exception)
        }
    }

    private fun toggleMyStickers(wantVisibleSticker: Boolean) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            Stipop.storeMyStickerViewModel?.loadsPackages(wantVisibleSticker)?.collectLatest {
                pagingMyPackAdapter.submitData(it)
            }
        }
    }

    override fun onPackageClick(position: Int, stickerPackage: StickerPackage) {
        //
    }

    override fun onItemClicked(packageId: Int, entrancePoint: String) {
        PackDetailFragment.newInstance(packageId, entrancePoint).showNow(parentFragmentManager, Constants.Tag.DETAIL)
    }

    override fun onItemLongClicked(position: Int) {

    }

    override fun onVisibilityClicked(wantToVisible: Boolean, packageId: Int, position: Int) {
        Stipop.storeMyStickerViewModel?.hideOrRecoverPackage(packageId, position)
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onDragCompleted(fromData: Any, toData: Any) {
        Stipop.storeMyStickerViewModel?.changePackageOrder(fromData as StickerPackage, toData as StickerPackage)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(
            LAST_VISIBLE_SETTING,
            binding?.stickerVisibleToggleTextView?.isSelected ?: true
        )
    }

    private fun initRequest(wantVisibleSticker: Boolean) {
        stickerVisibleToggleTextView.isSelected = wantVisibleSticker

        stickerVisibleToggleTextView.setOnClickListener {
            stickerVisibleToggleTextView.isSelected = !stickerVisibleToggleTextView.isSelected
            when (stickerVisibleToggleTextView.isSelected) {
                true -> {
                    stickerVisibleToggleTextView.text = getString(R.string.sp_view_hidden_stickers)
                    stickerVisibleToggleTextView.setBackgroundColor(
                        Config.getHiddenStickerBackgroundColor(
                            requireContext()
                        )
                    )
                }
                false -> {
                    stickerVisibleToggleTextView.text = getString(R.string.sp_view_active_stickers)
                    stickerVisibleToggleTextView.setBackgroundColor(
                        Config.getActiveStickerBackgroundColor(
                            requireContext()
                        )
                    )
                }
            }
            toggleMyStickers(stickerVisibleToggleTextView.isSelected)
        }
    }

    private fun showEmptyList(show: Boolean) {
        emptyTextView.isVisible = show
        myStickersRecyclerView.isVisible = !show
    }

    private fun setNoResultView() {
        if (pagingMyPackAdapter.itemCount > 0) {
            listLL.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        } else {
            listLL.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        }
    }

    override fun getMyVisibleStickersRetry() {
        pagingMyPackAdapter.retry()
    }
    override fun getMyHiddenStickersRetry() {
        pagingMyPackAdapter.retry()
    }
}