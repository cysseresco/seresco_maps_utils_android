package seresco.maps.utils.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_tracking.*
import seresco.maps.utils.lib.utils.tracking.OnTrackingCallback
import seresco.maps.utils.lib.utils.tracking.TrackingUtils
import seresco.maps.utils.myapplication.databinding.ActivityKmlBinding
import seresco.maps.utils.myapplication.databinding.ActivityTrackingBinding
import seresco.maps.utils.myapplication.utils.BaseActivity

class TrackingActivity : AppCompatActivity(), OnMapReadyCallback, OnTrackingCallback, BaseActivity {

    lateinit var googleMap: GoogleMap
    private lateinit var trackingUtils: TrackingUtils

    private val activityTrackingBinding by lazy {
        ActivityTrackingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityTrackingBinding.root)
        setupMap()
    }

    override fun setupMap() {
        (supportFragmentManager.findFragmentById(R.id.map_tracking) as SupportMapFragment?)?.getMapAsync(this)
    }

    override fun setupView() {
        setupInteraction()
        moveCamera()
        initTrackingUtils()
    }

    override fun setupInteraction() {
        fab_tracking.setOnClickListener {
            trackingUtils.openTrackingPanel(supportFragmentManager)
        }
    }

    private fun initTrackingUtils() {
        trackingUtils = TrackingUtils(this, this, supportFragmentManager, googleMap)
        trackingUtils.showSavedCoordinates()
    }

    override fun moveCamera() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(43.3612, -5.8469), 20f))
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        setupView()
    }

    override fun showTrackCoordinates(coordinates: MutableList<MutableList<Double>>) {
        trackingUtils.showSavedCoordinates()
    }

    override fun removeTrackedRoute() {
        trackingUtils.cleanSavedCoordinates()
    }
}