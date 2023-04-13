package seresco.maps.utils.lib.utils.tracking

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.ui.ColorsBottomSheet
import seresco.maps.utils.lib.ui.DetailBottomSheet
import seresco.maps.utils.lib.ui.TrackingBottomSheet
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences
import seresco.maps.utils.lib.utils.kml.KMLUtils
import seresco.maps.utils.lib.utils.marker.MarkerUtils

class TrackingUtils(context: Context, onTrackingCallback: OnTrackingCallback, supportFragmentManager: FragmentManager, googleMap: GoogleMap): TrackingBottomSheet.TrackingListener, ColorsBottomSheet.OnColorClicked, Constant {

    private val mContext = context
    private val mOnTrackingCallback = onTrackingCallback
    private val mSupportFragmentManager = supportFragmentManager
    private val mGoogleMap = googleMap

    lateinit var preference: Preferences
    private val markerUtils = MarkerUtils()
    private val kmlUtils = KMLUtils(context, supportFragmentManager, googleMap)

    fun openTrackingPanel(supportFragmentManager: FragmentManager) {
        val trackingSheet = TrackingBottomSheet.newInstance(true, mContext, this)
        trackingSheet.isCancelable = false
        trackingSheet.show(supportFragmentManager, DetailBottomSheet.TAG)
    }

    fun showSavedCoordinates() {
        preference = Preferences(mContext)
        val coordinates = getSavedCoordinates(preference)
       // mGoogleMap.clear()
        coordinates?.let {
            if (coordinates.isEmpty()) {
                return
            }
            val strokeColor = getStrokeColor(preference)
            val lays = kmlUtils.retrieveLinesKml(it, strokeColor,1.0f)
            lays.setOnFeatureClickListener {
                lays.removeLayerFromMap()
                val trackingSheet = ColorsBottomSheet.newInstance(true, this)
                trackingSheet.show(mSupportFragmentManager, DetailBottomSheet.TAG)
            }
            lays.addLayerToMap()
        }
    }

    fun cleanSavedCoordinates() {
        preference = Preferences(mContext)
        val coordinates = getSavedCoordinates(preference)
        coordinates?.let {
            val strokeColor = getStrokeColor(preference)
            val lays = kmlUtils.retrieveLinesKml(it, strokeColor,1.0f)
            lays.addLayerToMap()
            lays.removeLayerFromMap()
        }
    }

    override fun cleanTrackingRoute() {
       /* preference = Preferences(mContext)
        val coordinates = getSavedCoordinates(preference)
        Log.e("hey!", "clean")
        coordinates?.let {
            val strokeColor = getStrokeColor(preference)
            val lays = kmlUtils.retrieveLinesKml(it, strokeColor,1.0f)
            lays.removeLayerFromMap()
        }*/
        mOnTrackingCallback.removeTrackedRoute()
    }

    override fun getCoordinates(coords: MutableList<MutableList<Double>>) {
        mOnTrackingCallback.showTrackCoordinates(coords)
    }

    override fun onColorItemClicked(color: Int) {
        preference = Preferences(mContext)
        preference.saveInt(CURRENT_COLOR_DATA, color)
        showSavedCoordinates()
    }

    private fun getStrokeColor(preferences: Preferences): Int {
        var strokeColor = preferences.getInt(CURRENT_COLOR_DATA)
        if (strokeColor == 0) {
            strokeColor = R.color.black
        }
        return strokeColor
    }

    private fun getSavedCoordinates(preferences: Preferences) : MutableList<MutableList<Double>>? {
        return preferences.getCoordinates(COORDS_DATA)
    }
}

interface OnTrackingCallback {
    fun showTrackCoordinates(coordinates: MutableList<MutableList<Double>>)
    fun removeTrackedRoute()
}