package seresco.maps.utils.lib.ui.wms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_wms.*
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.ui.DetailBottomSheet
import seresco.maps.utils.lib.utils.wms.OnWmsCallback

class WmsBottomSheet(callback: OnWmsCallback, items: List<WMSItem>): BottomSheetDialogFragment(), WmsItemAdapter.WmsItemItemClickListener {

    private var dismissWithAnimation = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var mCallback = callback
    private var mItems = items

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_wms, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpListeners()
        displayWmsItems(mItems)
    }

    private fun displayWmsItems(wmsItems: List<WMSItem>) {
        viewManager = LinearLayoutManager(context)
        viewAdapter = WmsItemAdapter(
            wmsItems,
            this
        )
        rv_wms_item.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun setUpViews() {

    }

    private fun setUpListeners() {


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dismissWithAnimation = arguments?.getBoolean(DetailBottomSheet.ARG_DISMISS_WITH_ANIMATION) ?: false
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = dismissWithAnimation
    }

    companion object {
        const val TAG = "modalDetailSheet"
        const val ARG_DISMISS_WITH_ANIMATION = "dismiss_with_animation"
        fun newInstance(dismissWithAnimation: Boolean, callback: OnWmsCallback, items: List<WMSItem>): WmsBottomSheet {
            val modalSimpleListSheet = WmsBottomSheet(callback, items)
            modalSimpleListSheet.arguments = bundleOf(ARG_DISMISS_WITH_ANIMATION to dismissWithAnimation)
            return modalSimpleListSheet
        }
    }

    override fun onWmsItemClicked(wmsItem: WMSItem) {
        mCallback.showWmsSelected(wmsItem)
        dismiss()
    }
}