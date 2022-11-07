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
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.ui.DetailBottomSheet
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences
import seresco.maps.utils.lib.utils.wms.OnWmsCallback

class WmsLayersBottomSheet(callback: OnWmsLayerItemItemCallback, items: List<WMSItem>): BottomSheetDialogFragment(), WmsLayerItemAdapter.WmsLayerItemItemClickListener, Constant {

    private var dismissWithAnimation = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var mCallback = callback
    private var mItems = items
    lateinit var preference: Preferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_wms_layers, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = Preferences(requireContext())
        setUpViews()
        setUpListeners()
        displayWmsItems(mItems)
    }

    private fun displayWmsItems(wmsItems: List<WMSItem>) {
        viewManager = LinearLayoutManager(context)
        viewAdapter = WmsLayerItemAdapter(
            wmsItems,
            this
        )
        rv_wms_item_layers.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun setUpViews() {

    }

    private fun setUpListeners() {
        b_wms_layer_accept.setOnClickListener {
            val listOfLayers: List<WMSItem> = preference.getLayers(CURRENT_WMS_LAYERS_SELECTED)
            mCallback.onWmsLayerItemCallback(listOfLayers)
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dismissWithAnimation = arguments?.getBoolean(DetailBottomSheet.ARG_DISMISS_WITH_ANIMATION) ?: false
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = dismissWithAnimation
    }

    companion object {
        const val TAG = "modalDetailSheet"
        const val ARG_DISMISS_WITH_ANIMATION = "dismiss_with_animation"
        fun newInstance(dismissWithAnimation: Boolean, callback: OnWmsLayerItemItemCallback, items: List<WMSItem>): WmsLayersBottomSheet {
            val modalSimpleListSheet = WmsLayersBottomSheet(callback, items)
            modalSimpleListSheet.arguments = bundleOf(ARG_DISMISS_WITH_ANIMATION to dismissWithAnimation)
            return modalSimpleListSheet
        }
    }

//    override fun onWmsLayerItemClicked(wmsItem: WMSItem) {
//        Log.e("hey! clicked", wmsItem.description)
//        val baseUrl = "${wmsItem.url.split("GetCapabilities").toTypedArray().first()}GetMap" +
//                "&layers=${wmsItem.description}" +
//                "&bbox=%f,%f,%f,%f" +
//                "&width=256" +
//                "&height=256" +
//                "&srs=EPSG:900913" +
//                "&format=image/png" +
//                "&transparent=true"
//        val selectedWms = WMSItem(baseUrl, wmsItem.description)
////        mCallback.onWmsLayerItemCallback(selectedWms)
//        dismiss()
//    }

    interface OnWmsLayerItemItemCallback {
        fun onWmsLayerItemCallback(wmsItems: List<WMSItem>)
    }

    override fun onWmsLayerItemClicked(wmsItems: List<WMSItem>, positionItems: List<Int>) {
        TODO("Not yet implemented")
    }
}