package seresco.maps.utils.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_cluster.*
import seresco.maps.utils.lib.utils.cluster.ClusterUtils
import seresco.maps.utils.myapplication.databinding.ActivityClusterBinding
import seresco.maps.utils.myapplication.utils.BaseActivity

class ClusterActivity : AppCompatActivity(), BaseActivity, OnMapReadyCallback {

    lateinit var googleMap: GoogleMap
    private lateinit var clusterUtils: ClusterUtils

    private val activityClusterBinding by lazy {
        ActivityClusterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityClusterBinding.root)
        setupMap()
    }

    private fun initClusterUtils() {
        clusterUtils = ClusterUtils()
    }

    override fun setupMap() {
        (supportFragmentManager.findFragmentById(R.id.map_cluster) as SupportMapFragment?)?.getMapAsync(this)
    }

    override fun setupView() {
        setupInteraction()
        moveCamera()
        initClusterUtils()
    }

    override fun setupInteraction() {
        fab_cluster.setOnClickListener {
            val markers = clusterUtils.retrieveMarkers(raw)
            val clusterManager = clusterUtils.retrieveCluster(googleMap, this, supportFragmentManager, R.drawable.ic_pin, true)
            clusterManager.addItems(markers)
        }
    }

    override fun moveCamera() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.51834, 0.87013), 8f))
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        setupView()
    }

    private var raw = "[\n" +
            "  {\n" +
            "    \"lat\": 41.51834,\n" +
            "    \"lng\": 0.87013,\n" +
            "    \"snippet\": \"https://cdn01.segre.com/uploads/imagenes/bajacalidad/2021/01/15/_altat3h3745706_075eb995.jpg?71de88fb5abe502568f6584703613003\",\n" +
            "    \"title\": \"Les Borges Blanques\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"lat\": 41.422702,\n" +
            "    \"lng\": 1.021196,\n" +
            "    \"title\": \"Tarrés\",\n" +
            "    \"snippet\": \"http://www.cerespain.com/images/tarres2.jpg\"\n" +
            "  },\n" +
            "    {\n" +
            "    \"lat\": 41.606063,\n" +
            "    \"lng\": 0.879895,\n" +
            "    \"title\": \"Miralcamp\",\n" +
            "    \"snippet\": \"https://www.rutadelvidelleida.cat/wp-content/uploads/pobladecervoles1.jpg\"\n" +
            "  },\n" +
            "    {\n" +
            "    \"lat\": 41.551069,\n" +
            "    \"lng\": 0.88901,\n" +
            "    \"title\": \"Puiggròs\",\n" +
            "    \"snippet\": \"https://www.rutadelvidelleida.cat/wp-content/uploads/pobladecervoles1.jpg\"\n" +
            "  },\n" +
            "    {\n" +
            "    \"lat\": 41.382993,\n" +
            "    \"lng\": 0.946906,\n" +
            "    \"title\": \"El Vilosell\",\n" +
            "    \"snippet\": \"https://vilosellwinehotel.com/wp-content/uploads/2016/11/EL_VILOSELL.jpg\"\n" +
            "  },\n" +
            "    {\n" +
            "    \"lat\": 41.36679,\n" +
            "    \"lng\": 0.916169,\n" +
            "    \"title\": \"La Pobla de Cérvoles\",\n" +
            "    \"snippet\": \"https://www.rutadelvidelleida.cat/wp-content/uploads/pobladecervoles1.jpg\"\n" +
            "  }\n" +
            "]"
}