package seresco.maps.utils.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_wmpgoogle_maps.*
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.utils.wms.OnWmsCallback
import seresco.maps.utils.lib.utils.wms.WebMapServiceUtils
import seresco.maps.utils.myapplication.databinding.ActivityWmpgoogleMapsBinding
import seresco.maps.utils.myapplication.utils.BaseActivity


class WMPGoogleMapsActivity : AppCompatActivity(), BaseActivity, OnMapReadyCallback, OnWmsCallback {

    lateinit var googleMap: GoogleMap
    private lateinit var wmsUtils: WebMapServiceUtils

    private val activityWMSGMapsBinding by lazy {
        ActivityWmpgoogleMapsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityWMSGMapsBinding.root)
        setupMap()
    }

    override fun setupMap() {
        (supportFragmentManager.findFragmentById(R.id.map_wms_gmaps) as SupportMapFragment?)?.getMapAsync(this)
    }

    override fun setupView() {
        setupInteraction()
        moveCamera()
        initWmsUtils()
    }

    private fun initWmsUtils() {
        val items = getWmsItems()
//        wmsUtils = WebMapServiceUtils(supportFragmentManager, items, googleMap, this)
        wmsUtils = WebMapServiceUtils(supportFragmentManager, googleMap, this)
    }

    override fun setupInteraction() {
        fab_wms.setOnClickListener {
            //wmsUtils.openWmsPanel()
            val urlUsa = "https://ahocevar.com/geoserver/wms" +
                    "?service=WMS" +
                    "&version=1.1.1" +
                    "&request=GetMap" +
                    "&layers=topp:states" +
                    "&bbox=%f,%f,%f,%f" +
                    "&width=256" +
                    "&height=256" +
                    "&srs=EPSG:900913" +
                    "&format=image/png" +
                    "&transparent=true"
            wmsUtils.setWmsLayer(urlUsa)
        }
    }

    override fun showWmsSelected(wmsItem: WMSItem) {
        wmsUtils.setWmsLayer(wmsItem.url)
    }

    override fun moveCamera() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(39.0119, -98.4842), 3f))
    }

    private fun getWmsItems(): List<WMSItem> {
        val urlUsa = "https://ahocevar.com/geoserver/wms" +
                "?service=WMS" +
                "&version=1.1.1" +
                "&request=GetMap" +
                "&layers=topp:states" +
                "&bbox=%f,%f,%f,%f" +
                "&width=256" +
                "&height=256" +
                "&srs=EPSG:900913" +
                "&format=image/png" +
                "&transparent=true"
        val urlPopulationDensity = "https://sedac.ciesin.columbia.edu/geoserver/wms" +
                "?service=WMS" +
                "&version=1.1.1" +
                "&request=GetMap" +
                "&layers=gpw-v3:gpw-v3-population-density_2000" +
                "&bbox=%f,%f,%f,%f" +
                "&width=256" +
                "&height=256" +
                "&srs=EPSG:900913" +
                "&format=image/png" +
                "&transparent=true"
        val urlUrban = "https://sedac.ciesin.columbia.edu/geoserver/wms" +
                "?service=WMS" +
                "&version=1.1.1" +
                "&request=GetMap" +
                "&layers=urbanspatial:urbanspatial-urban-extents-viirs-modis-us-2015" +
                "&bbox=%f,%f,%f,%f" +
                "&width=256" +
                "&height=256" +
                "&srs=EPSG:900913" +
                "&format=image/png" +
                "&transparent=true"
        val urlBiomedic = "https://sedac.ciesin.columbia.edu/geoserver/wms" +
            "?service=WMS" +
            "&version=1.1.1" +
            "&request=GetMap" +
            "&layers=anthromes:anthromes-anthropogenic-biomes-world-v1" +
            "&bbox=%f,%f,%f,%f" +
            "&width=256" +
            "&height=256" +
            "&srs=EPSG:900913" +
            "&format=image/png" +
            "&transparent=true"
        val urlGlobalFoodProduction = "https://sedac.ciesin.columbia.edu/geoserver/wms" +
                "?service=WMS" +
                "&version=1.1.1" +
                "&request=GetMap" +
                "&layers=crop-climate:crop-climate-effects-climate-global-food-production" +
                "&bbox=%f,%f,%f,%f" +
                "&width=256" +
                "&height=256" +
                "&srs=EPSG:900913" +
                "&format=image/png" +
                "&transparent=true"
        val urlSeresco2 = "https://ideg.xunta.gal/servizos/services/VISAF/ZonasDemarcadas/MapServer/WmsServer" +
                "?service=WMS" +
                "&version=1.1.1" +
                "&request=GetMap" +
                "&layers=2" +
                "&bbox=%f,%f,%f,%f" +
                "&width=256" +
                "&height=256" +
                "&srs=EPSG:900913" +
                "&format=image/png" +
                "&transparent=true"
        val urlSeresco = "https://ideg.xunta.gal/servizos/services/VISAF/Augas_2/MapServer/WmsServer" +
                "?service=WMS&request=GetMap&width=256&height=256&version=1.1.0&" +
                "layers=2&" +
                "bbox=-704443.6528555937,5087648.603957064,-665307.8943636157,5126784.3624490425" +
                "&srs=EPSG:900913&format=image/png&transparent=true&styles=default"

        val usaLayer = WMSItem(urlUsa, "Estados Unidos")
        val populationDensityLayer = WMSItem(urlPopulationDensity, "Densidad de Población")
        val urbanLayer = WMSItem(urlUrban, "Urbano")
        val biomedicLayer = WMSItem(urlBiomedic, "Biomédico")
        val globalFoodProduction = WMSItem(urlGlobalFoodProduction, "Producción Global de Comida")
        val serescoLayer = WMSItem(urlSeresco2, "Seresco")

        return listOf(usaLayer, populationDensityLayer, urbanLayer, biomedicLayer, globalFoodProduction, serescoLayer)
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        setupView()
    }


}