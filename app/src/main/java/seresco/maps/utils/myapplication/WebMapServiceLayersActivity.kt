package seresco.maps.utils.myapplication

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Xml
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_web_map_service_layers.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.model.WMSLayer
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences
import seresco.maps.utils.lib.utils.wms.OnWmsCallback
import seresco.maps.utils.lib.utils.wms.WebMapServiceUtils
import seresco.maps.utils.myapplication.databinding.ActivityWebMapServiceLayersBinding
import seresco.maps.utils.myapplication.utils.BaseActivity
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class WebMapServiceLayersActivity : AppCompatActivity(), BaseActivity, OnMapReadyCallback,
    OnWmsCallback, Constant {

    private lateinit var googleMap: GoogleMap
    private lateinit var wmsUtils: WebMapServiceUtils
    private var currentUrls = 0
    private var wmsLayers: MutableList<WMSLayer> = mutableListOf()
    private val nUrls = 2
    lateinit var preference: Preferences

    companion object {
        private const val SO_URL = "https://desageo.seresco.es/geoserver/Explotacion/wms?request=GetCapabilities"//"https://desageo.seresco.es/geoserver/Explotacion/wms?service=WMS&version=1.1.0&request=GetCapabilities"
//        private const val SO_URL = "https://ideg.xunta.gal//servizos/services/LugaresProtexidos/EspazosNaturaisConservacion/MapServer/WMSServer?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetCapabilities"
//        private const val SO_URL =  "https://www.ign.es/wms-inspire/mapa-raster?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetCapabilities"
        private const val SO_URL_TWO = "https://www.ign.es/wms-inspire/pnoa-ma?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetCapabilities"
        private const val SO_URL_THREE = "http://www.ign.es/wms/pnoa-historico?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetCapabilities"
    }

    private val activityWebMapServiceLayersBinding by lazy {
        ActivityWebMapServiceLayersBinding.inflate(layoutInflater)
    }

    private fun initWmsUtils() {
        val items = arrayListOf<WMSItem>()
        wmsUtils = WebMapServiceUtils(supportFragmentManager, items, googleMap, this, this)
        wmsUtils.loadSavedLayers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityWebMapServiceLayersBinding.root)
        setupMap()
    }

    override fun setupMap() {
        (supportFragmentManager.findFragmentById(R.id.map_wms_layers) as SupportMapFragment?)?.getMapAsync(this)
    }

    override fun setupView() {
        setupInteraction()
        moveCamera()
        initWmsUtils()
        initPreferences()
    }

    private fun initPreferences() {
        preference = Preferences(this)
    }

    override fun setupInteraction() {
        fab_wms_layers.setOnClickListener {
            loadPage()
        }
        fab_wms_inform.setOnClickListener {
            wmsUtils.openWmsInfoPanel()
        }
    }

    override fun moveCamera() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(43.359498, -5.850547), 3f))
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        setupView()
    }

    override fun showWmsSelected(wmsItem: WMSItem) {
        Log.e("hey!!!", wmsItem.description)
    }

    private fun loadPage() {
        Log.e("hey!! list", preference.getLayers(CURRENT_WMS_LAYERS_SELECTED).toString())
        DownloadXmlTask().execute(SO_URL)
        DownloadXmlTask().execute(SO_URL_TWO)
//        DownloadXmlTask().execute(SO_URL_THREE)
    }

    private inner class DownloadXmlTask : AsyncTask<String, Void, MutableList<String>>() {
        override fun doInBackground(vararg urls: String): MutableList<String> {
            return try {
                loadLayersFromXml(urls[0])
            } catch (e: IOException) {
                Log.e("hey! error", e.toString())
                mutableListOf<String>()
            } catch (e: XmlPullParserException) {
                loadXmlFromNetwork(urls[0])
                mutableListOf<String>()
            }
        }

        override fun onPostExecute(result: MutableList<String>) {
            if (result.isNotEmpty()) {
                val wmsList = mutableListOf<WMSItem>()
                Log.e("hey!! datida", result.toString())
                result.forEach {
                    val item = WMSItem(SO_URL, it)
                    wmsList.add(item)
                }
                val wmsLayer = WMSLayer("Mapa Base $currentUrls", wmsList)
                wmsLayers.add(wmsLayer)
                currentUrls += 1
                if (currentUrls == nUrls) {
                    //saveWmsLayersInPreferences(wmsLayers)
                    wmsUtils.openWmsLayersPanel(wmsLayers)
                    currentUrls = 0
                    wmsLayers = mutableListOf()
                }
            }
        }
    }

    private fun saveWmsLayersInPreferences(layers: MutableList<WMSLayer>) {
        layers.forEach { layer ->
            preference.saveWmsLayers(WMS_LAYERS_SELECTED, layer)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadLayersFromXml(urlString: String): MutableList<String> {
        val entries: MutableList<String> = downloadUrl(urlString)?.use { stream ->
            // Instantiate the parser
            StackOverflowXmlParser().parse(stream)
        } as MutableList<String>

        Log.e("hey! entries ", entries.toString())

        return entries

    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(urlString: String): MutableList<String> {
        val entries: MutableList<String> = downloadUrl(urlString)?.use { stream ->
            StackOverflowXmlParser().parseAux(stream)
        } as MutableList<String>
        Log.e("hey!!! entries", entries.toString())

        val wmsList = mutableListOf<WMSItem>()
        entries.forEach {
            val item = WMSItem(SO_URL, it)
            wmsList.add(item)
        }
        wmsUtils.openWmsPanel(wmsList)

        return entries
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {
        val url = URL(urlString)
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            // Starts the query
            connect()
            inputStream
        }
    }

}

class StackOverflowXmlParser {

    private val ns: String? = null


    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<*> {
        inputStream.use { inputStream ->
            Log.e("hey! input", inputStream.toString())
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readWMSCapabilities(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parseAux(inputStream: InputStream): List<*> {
        inputStream.use { inputStream ->
            Log.e("hey! input", inputStream.toString())
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }
    //---

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readWMSCapabilities(parser: XmlPullParser): List<String> {
        val entries = mutableListOf<Entry>()
        val layerNames = mutableListOf<String>()

        parser.require(XmlPullParser.START_TAG, ns, "WMT_MS_Capabilities")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "Capability") {
                entries.add(readCapability(parser, layerNames))
            } else {
                skip(parser)
            }
        }
        Log.e("hey! layer names", layerNames.toString())
        return layerNames
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCapability(parser: XmlPullParser, layerNames: MutableList<String>): Entry {
        parser.require(XmlPullParser.START_TAG, ns, "Capability")
        var title: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            Log.e("xddd parser name -> ", parser.name)
            when (parser.name) {
                "Layer" -> title = readLayerCapability(parser, layerNames)
                else -> skip(parser)
            }
        }
        return Entry(title)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readLayerCapability(parser: XmlPullParser, layerNames: MutableList<String>): String {
        parser.require(XmlPullParser.START_TAG, ns, "Layer")
        var title: String? = null
        var summary: String? = null
        var link: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "Layer" -> title = readInnerLayerCapability(parser, layerNames)
                else -> skip(parser)
            }
        }
        return title ?: "nani"
    }

    private fun readInnerLayerCapability(parser: XmlPullParser, layerNames: MutableList<String>): String {
        parser.require(XmlPullParser.START_TAG, ns, "Layer")
        var title: String? = null
        var summary: String? = null
        var link: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            Log.e("-----parser name -> ", "----------------------" + parser.name)
            when (parser.name) {
                "Name" -> title = readText(parser)
                else -> skip(parser)
            }
        }
        layerNames.add(title ?: "nani")
        Log.e("hey! layer?", layerNames.toString())
        return title ?: "nani"
    }

    ///---

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<String> {
        val entries = mutableListOf<Entry>()
        val layerNames = mutableListOf<String>()

        parser.require(XmlPullParser.START_TAG, ns, "WMS_Capabilities")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "Capability") {
                entries.add(readEntry(parser, layerNames))
            } else {
                skip(parser)
            }
        }
        Log.e("hey!! LAST", layerNames.toString())
        return layerNames
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser, layerNames: MutableList<String>): Entry {
        Log.e("hey! ---------", "read entry -----------")
        parser.require(XmlPullParser.START_TAG, ns, "Capability")
        var title: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "Layer" -> title = readLayer(parser, layerNames)
                else -> skip(parser)
            }
        }
        return Entry(title)
    }


    @Throws(XmlPullParserException::class, IOException::class)
    private fun readLayer(parser: XmlPullParser, layerNames: MutableList<String>): String {
        parser.require(XmlPullParser.START_TAG, ns, "Layer")
        var title: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "Layer" -> title = readInnerLayer(parser, layerNames)
                else -> skip(parser)
            }
        }
        return title ?: "nani"
    }

    private fun readInnerLayer(parser: XmlPullParser, layerNames: MutableList<String>): String {

        parser.require(XmlPullParser.START_TAG, ns, "Layer")
        var title: String? = null
        var summary: String? = null
        var link: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "Layer" -> title = readInnerInnerLayer(parser, layerNames)
                else -> skip(parser)
            }
        }
        return title ?: "nani"
    }

    private fun readInnerInnerLayer(parser: XmlPullParser, layerNames: MutableList<String>): String {

        parser.require(XmlPullParser.START_TAG, ns, "Layer")
        var title: String? = null
        var summary: String? = null
        var link: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "Name" -> title = readText(parser)
                else -> skip(parser)
            }
        }
        layerNames.add(title ?: "nani")
        return title ?: "nani"
    }

    // For the tags title and summary, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            Log.e("hey! ENTER parsertext", parser.text.toString())
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}
data class Entry(val onlineResource: String?)