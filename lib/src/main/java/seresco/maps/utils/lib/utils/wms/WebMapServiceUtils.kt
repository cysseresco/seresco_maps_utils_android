package seresco.maps.utils.lib.utils.wms

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.ui.DetailBottomSheet
import seresco.maps.utils.lib.ui.wms.OfflineWmsBottomSheet
import seresco.maps.utils.lib.ui.wms.WmsBottomSheet
import seresco.maps.utils.lib.ui.wms.WmsLayersBottomSheet

class WebMapServiceUtils(): WmsLayersBottomSheet.OnWmsLayerItemItemCallback {

    private lateinit var mSupportFragmentManager: FragmentManager
    private lateinit var mOnWmsCallback: OnWmsCallback
    private lateinit var mItems: List<WMSItem>
    private lateinit var mGoogleMap: GoogleMap

    constructor(supportFragmentManager: FragmentManager, items: List<WMSItem>, googleMap: GoogleMap , onWmsCallback: OnWmsCallback) :
            this() {
        this.mSupportFragmentManager = supportFragmentManager
        this.mItems = items
        this.mGoogleMap = googleMap
        this.mOnWmsCallback = onWmsCallback
    }

    constructor(supportFragmentManager: FragmentManager, googleMap: GoogleMap) :
            this() {
        this.mSupportFragmentManager = supportFragmentManager
        this.mGoogleMap = googleMap
    }

    fun openWmsPanel() {
        val wmsSheet = WmsBottomSheet.newInstance(true, mOnWmsCallback, mItems)
        wmsSheet.show(mSupportFragmentManager, DetailBottomSheet.TAG)
    }

    fun openWmsPanel(list: MutableList<WMSItem>) {
        val wmsSheet = WmsLayersBottomSheet.newInstance(true, this, list)
        wmsSheet.show(mSupportFragmentManager, WmsLayersBottomSheet.TAG)
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
//        mGoogleMap.clear()
        val tileOverlay = getWmsSource(wmsUrl)
        mGoogleMap.addTileOverlay(tileOverlay)
    }

    override fun onWmsLayerItemCallback(wmsItems: List<WMSItem>) {
        wmsItems.forEach {
            val tileOverlay = getWmsSource(it.url)
            mGoogleMap.addTileOverlay(tileOverlay)
        }
    }

}

interface OnWmsCallback {
    fun showWmsSelected(wmsItem: WMSItem)
}