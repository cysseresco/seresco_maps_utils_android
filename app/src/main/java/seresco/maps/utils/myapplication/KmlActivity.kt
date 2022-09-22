package seresco.maps.utils.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_kml.*
import seresco.maps.utils.lib.utils.kml.KMLUtils
import seresco.maps.utils.lib.utils.kml.KmlPreferenceStyle
import seresco.maps.utils.myapplication.databinding.ActivityClusterBinding
import seresco.maps.utils.myapplication.databinding.ActivityKmlBinding
import seresco.maps.utils.myapplication.utils.BaseActivity

class KmlActivity : AppCompatActivity(), BaseActivity, OnMapReadyCallback {

    lateinit var googleMap: GoogleMap
    private lateinit var kmlUtils: KMLUtils

    private val activityKmlBinding by lazy {
        ActivityKmlBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityKmlBinding.root)
        setupMap()
    }

    override fun setupMap() {
        (supportFragmentManager.findFragmentById(R.id.map_kml) as SupportMapFragment?)?.getMapAsync(this)
    }

    override fun setupView() {
        setupInteraction()
        moveCamera()
        initKmlUtils()
    }

    override fun setupInteraction() {
        fab_kml.setOnClickListener {
            val spainKmlStyle = KmlPreferenceStyle(R.raw.geojson_spain, R.color.black, android.R.color.transparent,1.0f)
            val layerSpain = kmlUtils.retrieveKml(spainKmlStyle)
            layerSpain.addLayerToMap()

            val layerKmlStyle = KmlPreferenceStyle(R.raw.geojson_layer, R.color.purple_700, R.color.purple_200, 2.0f)
            val layer = kmlUtils.retrieveKml(layerKmlStyle)
            layer.addLayerToMap()
        }
    }

    override fun moveCamera() {
         googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.51834, 0.87013), 8f))
    }

    private fun initKmlUtils() {
        kmlUtils = KMLUtils(this, supportFragmentManager, googleMap)
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        setupView()
    }
}


