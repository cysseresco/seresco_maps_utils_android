package seresco.maps.utils.lib.utils.wms

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.model.WMSLayer
import seresco.maps.utils.lib.ui.DetailBottomSheet
import seresco.maps.utils.lib.ui.wms.*
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences

class WebMapServiceUtils(): WmsLayersBottomSheet.OnWmsLayerItemItemCallback, WmsLayersDetailBottomSheet.OnWmsLayerSelectedItemsCallback,
    WmsLayerItemInfoDetailAdapter.WmsItemSelectedItemItemClickListener, Constant {

    private lateinit var mSupportFragmentManager: FragmentManager
    private lateinit var mOnWmsCallback: OnWmsCallback
    private lateinit var mItems: List<WMSItem>
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var preference: Preferences
    private lateinit var context: Context

    constructor(supportFragmentManager: FragmentManager, items: List<WMSItem>, googleMap: GoogleMap , onWmsCallback: OnWmsCallback, context: Context) :
            this() {
        this.mSupportFragmentManager = supportFragmentManager
        this.mItems = items
        this.mGoogleMap = googleMap
        this.mOnWmsCallback = onWmsCallback
        this.context = context
        initPreferences()
    }

    constructor(supportFragmentManager: FragmentManager, googleMap: GoogleMap, context: Context) :
            this() {
        this.mSupportFragmentManager = supportFragmentManager
        this.mGoogleMap = googleMap
        this.context = context
    }

    private fun initPreferences() {
        preference = Preferences(context)
    }

    fun openWmsPanel() {
        val wmsSheet = WmsBottomSheet.newInstance(true, mOnWmsCallback, mItems)
        wmsSheet.show(mSupportFragmentManager, DetailBottomSheet.TAG)
    }

    fun openWmsInfoPanel() {
        val savedLayers = preference.getLayers(CURRENT_WMS_LAYERS_SELECTED)
        val wmsSheet = WmsBottomSheet.newInstance(true, mOnWmsCallback, savedLayers)
        wmsSheet.show(mSupportFragmentManager, DetailBottomSheet.TAG)
    }

    fun openWmsPanel(list: MutableList<WMSItem>) {
        val wmsSheet = WmsLayersBottomSheet.newInstance(true, this, list)
        wmsSheet.show(mSupportFragmentManager, WmsLayersBottomSheet.TAG)
    }

    fun openWmsLayersPanel(list: MutableList<WMSLayer>) {
        val wmsLayersSheet = WmsLayersDetailBottomSheet.newInstance(true, this, list, this)
        wmsLayersSheet.show(mSupportFragmentManager, WmsLayersBottomSheet.TAG)
    }

    fun loadSavedLayers() {
        mGoogleMap.clear()
        val savedLayers = preference.getLayers(CURRENT_WMS_LAYERS_SELECTED)
        savedLayers.forEach {
            val tileOverlay = getWmsSource(it.url)
            mGoogleMap.addTileOverlay(tileOverlay)
        }
    }

    fun openOfflineWmsPanel() {
        val offlineWmsSheet = OfflineWmsBottomSheet.newInstance(true, mGoogleMap)
        offlineWmsSheet.show(mSupportFragmentManager, DetailBottomSheet.TAG)
    }


    private fun getWmsSource(wmsUrl: String): TileOverlayOptions {
        val wmsTileProvider: TileProvider = TileProviderFactory.getOsgeoWmsTileProvider(wmsUrl)
        return TileOverlayOptions().tileProvider(wmsTileProvider)
    }

    fun setWmsLayer(wmsUrl: String) {
        mGoogleMap.clear()
        val tileOverlay = getWmsSource(wmsUrl)
        mGoogleMap.addTileOverlay(tileOverlay)
    }

    override fun onWmsLayerItemCallback(wmsItems: List<WMSItem>) {
        wmsItems.forEach {
            val tileOverlay = getWmsSource(it.url)
            mGoogleMap.addTileOverlay(tileOverlay)
        }
    }

    override fun onWmsLayerSelectedItemsCallback(wmsLayers: List<WMSLayer>) {
//        wmsItems.forEach {
//            val tileOverlay = getWmsSource(it.url)
//            mGoogleMap.addTileOverlay(tileOverlay)
//        }
    }

    override fun onWmsItemSelectedItemClicked(wmsItems: MutableList<WMSItem>) {
        val selectedItems = wmsItems.filter { it.isSelected }
        selectedItems.forEach {
            val tileOverlay = getWmsSource(it.url)
            mGoogleMap.addTileOverlay(tileOverlay)
            preference.saveLayers(CURRENT_WMS_LAYERS_SELECTED, it)
        }
    }

    override fun onWmsItemDeselectedItemClicked(wmsItem: WMSItem) {
        preference.removeLayer(CURRENT_WMS_LAYERS_SELECTED, wmsItem)
        loadSavedLayers()
    }

}

interface OnWmsCallback {
    fun showWmsSelected(wmsItem: WMSItem)
}