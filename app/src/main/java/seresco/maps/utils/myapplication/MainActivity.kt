package seresco.maps.utils.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
    }
}