package seresco.maps.utils.lib.utils.wms

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.model.WMSLayer
import seresco.maps.utils.lib.ui.DetailBottomSheet
import seresco.maps.utils.lib.ui.wms.*
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences
import kotlin.math.floor
import kotlin.math.sin


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
        val mProvider = tileOverlay.tileProvider
        mProvider?.let {
            clickLis(it)
        }
        mGoogleMap.addTileOverlay(tileOverlay)
    }

    override fun onWmsLayerItemCallback(wmsItems: List<WMSItem>) {
        wmsItems.forEach {
            val tileOverlay = getWmsSource(it.url)
            mGoogleMap.addTileOverlay(tileOverlay)
        }
    }

    fun clickLis(mProvider: TileProvider) {
        Log.e("hey!", "one")
        mGoogleMap.setOnMapClickListener {
            Log.e("hey!", "two ${it}")
            val zoom = mGoogleMap.cameraPosition.zoom.toInt()

            // get tile top-left coordinates in tile coordinate system
            // get tile top-left coordinates in tile coordinate system
            val tileX = getTileX(it, zoom)
            val tileY = getTileY(it, zoom)

            val tile: Tile? = mProvider.getTile(tileX, tileY, zoom)
            if (tile?.data == null) {
                return@setOnMapClickListener
            }
            Log.e("hey!", "three")
            // decode heatmap data into bitmap
            // decode heatmap data into bitmap
            val bitmap = BitmapFactory.decodeByteArray(tile.data, 0, tile.data!!.size)
            val xd = TileOverlayOptions().tileProvider(mProvider)
            if (xd.transparency == 0.0F) {
                Log.e("hey!", "OMG")
            } else {
                Log.e("hey!", "NOT OMG")
            }
            // get tile coordinates in pixels

            // get tile coordinates in pixels
            val tileNorthWest = LatLng(tile2lat(tileY, zoom), tile2long(tileX, zoom))
            val tileNorthWestX = lonToX(tileNorthWest.longitude, zoom).toLong()
            val tileNorthWestY = latToY(tileNorthWest.latitude, zoom).toLong()

            // get "click" point coordinates in pixels
            // get "click" point coordinates in pixels
            val pointNorthWestX = lonToX(it.longitude, zoom).toLong()
            val pointNorthWestY = latToY(it.latitude, zoom).toLong()

            // calculate offset of "click" point within current tile
            // x2 because of hi density tiles 512x512

            // calculate offset of "click" point within current tile
            // x2 because of hi density tiles 512x512
            val dx = 2 * (pointNorthWestX - tileNorthWestX)
            val dy = 2 * (pointNorthWestY - tileNorthWestY)

            // test calculated coordinates and get color of clicked point as Heat Map data
            // test calculated coordinates and get color of clicked point as Heat Map data
            if (dx >= 0 && dx < bitmap.width && dy >= 0 && dy < bitmap.height) {
                // dx, dy - coordinates of current tile of target heatmap
                // pixelColor is color value of target heatmap
                val pixelColor = bitmap.getPixel(dx.toInt(), dy.toInt())
                Log.e("hey!!!", "NANIIIII NO ME LO CREO")
            }
        }
    }

    fun getTileX(latLng: LatLng, zoom: Int): Int {
        var tileX = Math.floor((latLng.longitude + 180) / 360 * (1 shl zoom)).toInt()
        if (tileX < 0) tileX = 0
        if (tileX >= 1 shl zoom) tileX = (1 shl zoom) - 1
        return tileX
    }

    fun getTileY(latLng: LatLng, zoom: Int): Int {
        var tileY = Math.floor(
            (1 - Math.log(
                Math.tan(Math.toRadians(latLng.latitude)) + 1 / Math.cos(
                    Math.toRadians(latLng.latitude)
                )
            ) / Math.PI) / 2 * (1 shl zoom)
        )
            .toInt()
        if (tileY < 0) tileY = 0
        if (tileY >= 1 shl zoom) tileY = (1 shl zoom) - 1
        return tileY
    }

    fun lonToX(lon: Double, zoom: Int): Double {
        val offset = 256 shl zoom - 1
        return Math.floor(offset + offset * lon / 180)
    }

    fun latToY(lat: Double, zoom: Int): Double {
        val offset = 256 shl zoom - 1
        return floor(
            offset - offset / Math.PI * Math.log(
                (1 + sin(Math.toRadians(lat)))
                        / (1 - sin(Math.toRadians(lat)))
            ) / 2
        )
    }

    fun tile2long(x: Int, zoom: Int): Double {
        return (x / Math.pow(2.0, zoom.toDouble()) * 360 - 180)
    }

    fun tile2lat(y: Int, zoom: Int): Double {
        val n = Math.PI - 2 * Math.PI * y / Math.pow(2.0, zoom.toDouble())
        return (180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n))))
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
            Log.e("hey! nana", it.url)
//            val tileOverlay = getWmsSource(it.url)
//            mGoogleMap.addTileOverlay(tileOverlay)
            setWmsLayer(it.url)
//            preference.saveLayers(CURRENT_WMS_LAYERS_SELECTED, it)
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