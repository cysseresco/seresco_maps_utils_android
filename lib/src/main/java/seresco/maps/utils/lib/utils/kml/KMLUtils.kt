package seresco.maps.utils.lib.utils.kml

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import com.google.maps.android.data.kml.KmlLayer
import org.json.JSONObject
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.ui.DetailBottomSheet
import seresco.maps.utils.lib.ui.KmlSettingType
import kotlin.math.roundToInt


/**
 * Grupo de funciones relacionado con *KML*.
 *
 * Esta clase permite la integración de KML de manera rápida, eficiente y con muchas mayores funcionalidades
 *
 * @param map instancia de GoogleMaps.
 * @param resource permite obtener la información (json) de las coordenadas del KML.
 * @param strokeColor color del borde
 * @param fillColor color del bloque
 * @param zIndex grosor del borde
 */
class KMLUtils(context: Context, supportFragmentManager: FragmentManager, googleMap: GoogleMap): DetailBottomSheet.DetailItemClicked {

    private val mContext = context
    private val mSupportFragmentManager = supportFragmentManager
    private val mGoogleMap = googleMap

    private lateinit var currentKmlStyle: KmlPreferenceStyle

    fun retrieveKml(map: GoogleMap, kmlRaw: String, context: Context): KmlLayer {
        return KmlLayer(map, kmlRaw.byteInputStream(), context)
    }

    fun retrieveKml(kmlStyle: KmlPreferenceStyle): GeoJsonLayer {
        currentKmlStyle = kmlStyle
        val layer = GeoJsonLayer(mGoogleMap, kmlStyle.resource, mContext)
        val geoPolygonStyle: GeoJsonPolygonStyle = layer.defaultPolygonStyle
        geoPolygonStyle.strokeColor = ContextCompat.getColor(mContext, kmlStyle.strokeColor)
        geoPolygonStyle.fillColor = ContextCompat.getColor(mContext, kmlStyle.fillColor)
        geoPolygonStyle.zIndex = kmlStyle.borderWidth
        geoPolygonStyle.isClickable = true
        layer.setOnFeatureClickListener {
            val detailSheet = DetailBottomSheet.newInstance(true, this)
            detailSheet.show(mSupportFragmentManager, DetailBottomSheet.TAG)
        }
        return layer
    }

    private fun retrieveKml(kmlStyle: KmlPreferenceStyle, alpha: Float): GeoJsonLayer {
        mGoogleMap.clear()
        currentKmlStyle = kmlStyle
        val layer = GeoJsonLayer(mGoogleMap, kmlStyle.resource, mContext)
        val geoPolygonStyle: GeoJsonPolygonStyle = layer.defaultPolygonStyle
        geoPolygonStyle.strokeColor = ContextCompat.getColor(mContext, kmlStyle.strokeColor)
        val colorWithAlpha = adjustAlpha(ContextCompat.getColor(mContext, kmlStyle.fillColor), alpha)
        geoPolygonStyle.fillColor = colorWithAlpha
        geoPolygonStyle.zIndex = kmlStyle.borderWidth
        geoPolygonStyle.isClickable = true
        layer.setOnFeatureClickListener {
            val detailSheet = DetailBottomSheet.newInstance(true, this)
            detailSheet.show(mSupportFragmentManager, DetailBottomSheet.TAG)
        }
        return layer
    }

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red: Int = Color.red(color)
        val green: Int = Color.green(color)
        val blue: Int = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    fun retrieveLinesKml(coordinates: MutableList<MutableList<Double>>, strokeColor: Int, zIndex: Float): GeoJsonLayer {
        val resource = "{ \"type\": \"FeatureCollection\", \"features\": [ {  \"type\": \"Feature\", \"properties\": {}, \"geometry\": {  \"type\": \"LineString\",  \"coordinates\": $coordinates } } ] }"
        val geoJsonData =  JSONObject(resource)
        val layer = GeoJsonLayer(mGoogleMap, geoJsonData)
        val geoLinesStyle: GeoJsonLineStringStyle = layer.defaultLineStringStyle
        geoLinesStyle.color = ContextCompat.getColor(mContext, strokeColor)
        geoLinesStyle.zIndex = zIndex
        geoLinesStyle.isClickable = true;
        return layer
    }

    override fun onDetailItemClicked(kmlSettingType: KmlSettingType, color: Int) {
        when (kmlSettingType) {
            KmlSettingType.UPDATE_BORDER -> {
                updateBorderLayer(color)
            }
            KmlSettingType.UPDATE_FILL -> {
                updateFillColorLayer(color)
            }
            KmlSettingType.TRANSPARENCY -> {
                updateTransparencyLayer(color)
            }
        }
    }

    private fun updateBorderLayer(color: Int) {
        currentKmlStyle.strokeColor = color
        val layer = retrieveKml(currentKmlStyle)
        layer.addLayerToMap()
    }

    private fun updateFillColorLayer(color: Int) {
        currentKmlStyle.fillColor = color
        val layer = retrieveKml(currentKmlStyle)
        layer.addLayerToMap()
    }

    private fun updateTransparencyLayer(alpha: Int) {
        val alphaValue = (alpha.toFloat()/255)
        val layer = retrieveKml(currentKmlStyle, alphaValue)
        layer.addLayerToMap()
    }

}

data class KmlPreferenceStyle(
    var resource: Int,
    var strokeColor: Int,
    var fillColor: Int,
    var borderWidth: Float)