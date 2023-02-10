package seresco.maps.utils.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_visor.*
import kotlinx.android.synthetic.main.activity_wmpgoogle_maps.fab_wms
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.model.WMSLayer
import seresco.maps.utils.lib.utils.wms.OnWmsCallback
import seresco.maps.utils.lib.utils.wms.WebMapServiceUtils
import seresco.maps.utils.myapplication.databinding.ActivityVisorBinding
import seresco.maps.utils.myapplication.databinding.ActivityWmpgoogleMapsBinding
import seresco.maps.utils.myapplication.utils.BaseActivity

class VisorActivity : AppCompatActivity(), BaseActivity, OnMapReadyCallback, OnWmsCallback {

    lateinit var googleMap: GoogleMap
    private lateinit var wmsUtils: WebMapServiceUtils
    var isTopographyStyleSelected = false

    private val activityVisorBinding by lazy {
        ActivityVisorBinding.inflate(layoutInflater)
    }

    fun getIgnUrl(): String {
         return "https://www.ign.es/wms-inspire/pnoa-ma" +
                    "?service=WMS" +
                    "&version=1.1.1" +
                    "&request=GetMap" +
                    "&layers=OI.OrthoimageCoverage" +
                    "&bbox=%f,%f,%f,%f" +
                    "&width=256" +
                    "&height=256" +
                    "&srs=EPSG:3857" +
                    "&format=image/png" +
                    "&transparent=true"
    }

    fun getIgnTopographyUrl(): String {
        return "https://www.ign.es/wms-inspire/mapa-raster" +
                "?service=WMS" +
                "&version=1.1.1" +
                "&request=GetMap" +
                "&layers=mtn_rasterizado" +
                "&bbox=%f,%f,%f,%f" +
                "&width=256" +
                "&height=256" +
                "&srs=EPSG:3857" +
                "&format=image/png" +
                "&transparent=true"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityVisorBinding.root)
        setupMap()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    override fun setupMap() {
        (supportFragmentManager.findFragmentById(R.id.map_wms_gmaps) as SupportMapFragment?)?.getMapAsync(this)
    }

    override fun setupView() {
        setupInteraction()
        moveCamera()
        initWmsUtils()
        initWms()
    }

    private fun initWmsUtils() {
        wmsUtils = WebMapServiceUtils(supportFragmentManager, googleMap, this)
    }

    private fun initWms() {
        wmsUtils.setWmsLayer(getIgnUrl())
    }

    override fun setupInteraction() {
        fab_wms.setOnClickListener {
            val urlSeresco1 = "https://desageo.seresco.es/geoserver/Explotacion/wms" +
                    "?service=WMS" +
                    "&version=1.1.1" +
                    "&request=GetMap" +
                    "&layers=Explotacion:DGC" +
                    "&bbox=%f,%f,%f,%f" +
                    "&width=256" +
                    "&height=256" +
                    "&srs=EPSG:3857" +
                    "&format=image/png" +
                    "&transparent=true"
            val urlSeresco2 = "https://desageo.seresco.es/geoserver/wms" +
                    "?service=WMS" +
                    "&version=1.3.0" +
                    "&request=GetMap" +
                    "&layers=Explotacion:Recinto" +
                    "&bbox=%f,%f,%f,%f" +
                    "&width=256" +
                    "&height=256" +
                    "&srs=EPSG:3857" +
                    "&format=image/png" +
                    "&transparent=true"

            val urlSeresco3 = "https://desageo.seresco.es/geoserver/wms" +
                    "?service=WMS" +
                    "&version=1.3.0" +
                    "&request=GetMap" +
                    "&layers=Explotacion:DGC-CULTIVOS" +
                    "&bbox=%f,%f,%f,%f" +
                    "&width=256" +
                    "&height=256" +
                    "&srs=EPSG:3857" +
                    "&format=image/png" +
                    "&transparent=true"

            val wmsItemsDGC = WMSItem(urlSeresco1, "DGC", false)
            val wmsItemsRecinto = WMSItem(urlSeresco2, "Recinto", false)
            val wmsItemsCultivos = WMSItem(urlSeresco3, "Cultivos", false)
            val items = arrayListOf<WMSItem>()
            items.add(wmsItemsDGC)
            items.add(wmsItemsRecinto)
            items.add(wmsItemsCultivos)
            val wmsLayer = WMSLayer("Seresco", items)
            val wmsLayerList = arrayListOf<WMSLayer>()
            wmsLayerList.add(wmsLayer)
            wmsUtils.openWmsLayersPanel(wmsLayerList)
        }

        fab_style.setOnClickListener {
            if (isTopographyStyleSelected) {
                wmsUtils.setWmsLayer(getIgnUrl())
            } else {
                wmsUtils.setWmsLayer(getIgnTopographyUrl())
            }
            isTopographyStyleSelected = !isTopographyStyleSelected
        }
    }

    override fun showWmsSelected(wmsItem: WMSItem) {
        wmsUtils.setWmsLayer(wmsItem.url)
    }

    override fun moveCamera() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(42.906051557807295,-8.566638641059399), 13f))
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        setupView()
    }


}