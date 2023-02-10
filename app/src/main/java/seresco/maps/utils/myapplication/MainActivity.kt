package seresco.maps.utils.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import seresco.maps.utils.myapplication.databinding.ActivityMainBinding
import seresco.maps.utils.myapplication.databinding.ActivityTrackingBinding

class MainActivity : AppCompatActivity() {

    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        setupView()
    }

    private fun setupView() {
        setupInteraction()
    }

    private fun setupInteraction() {
        b_tracking.setOnClickListener {
            val trackingIntent = Intent(this, TrackingActivity::class.java)
            startActivity(trackingIntent)
        }
        b_kml.setOnClickListener {
            val kmlIntent = Intent(this, KmlActivity::class.java)
            startActivity(kmlIntent)
        }
        b_cluster.setOnClickListener {
            val clusterIntent = Intent(this, ClusterActivity::class.java)
            startActivity(clusterIntent)
        }
        b_wms_native.setOnClickListener {
            val wmsGoogleIntent = Intent(this, WMPGoogleMapsActivity::class.java)
            startActivity(wmsGoogleIntent)
        }
        b_wms_layers.setOnClickListener {
            val wmsLayersIntent = Intent(this, WebMapServiceLayersActivity::class.java)
            startActivity(wmsLayersIntent)
        }
        b_visor.setOnClickListener {
            val visorIntent = Intent(this, VisorActivity::class.java)
            startActivity(visorIntent)
        }
    }
}