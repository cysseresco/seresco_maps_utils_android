package seresco.maps.utils.lib.ui.wms

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_wms.*
import kotlinx.android.synthetic.main.bottom_sheet_wms_layers.*
import kotlinx.android.synthetic.main.bottom_sheet_wms_layers_detail.*
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.model.WMSLayer
import seresco.maps.utils.lib.ui.DetailBottomSheet
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences
import seresco.maps.utils.lib.utils.wms.OnWmsCallback

class WmsLayersDetailBottomSheet(callback: OnWmsLayerSelectedItemsCallback, items: List<WMSLayer>, private var listener: WmsLayerItemInfoDetailAdapter.WmsItemSelectedItemItemClickListener): BottomSheetDialogFragment(), WmsLayerItemInfoAdapter.WmsLayerSelectedItemItemClickListener, Constant {

    private var dismissWithAnimation = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var mCallback = callback
    private var mListener = listener
    private var mItems = items
    lateinit var preference: Preferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_wms_layers_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = Preferences(requireContext())
        setUpViews()
        setUpListeners()
        displayWmsItems(mItems)
    }

    private fun displayWmsItems(wmsLayers: List<WMSLayer>) {
        viewManager = LinearLayoutManager(context)
        viewAdapter = WmsLayerItemInfoAdapter(
            requireContext(),
            wmsLayers,
            mListener
        )
        rv_wms_item_layers_detail.apply {
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
        fun newInstance(dismissWithAnimation: Boolean, callback: OnWmsLayerSelectedItemsCallback, items: List<WMSLayer>, listener: WmsLayerItemInfoDetailAdapter.WmsItemSelectedItemItemClickListener): WmsLayersDetailBottomSheet {
            val modalSimpleListSheet = WmsLayersDetailBottomSheet(callback, items, listener)
            modalSimpleListSheet.arguments = bundleOf(ARG_DISMISS_WITH_ANIMATION to dismissWithAnimation)
            return modalSimpleListSheet
        }
    }

    interface OnWmsLayerSelectedItemsCallback {
        fun onWmsLayerSelectedItemsCallback(wmsLayers: List<WMSLayer>)
    }

    override fun onWmsLayerSelectedItemClicked(wmsItems: List<WMSLayer>) {

    }
}