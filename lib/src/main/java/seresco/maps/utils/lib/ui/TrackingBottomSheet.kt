package seresco.maps.utils.lib.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences
import seresco.maps.utils.lib.utils.tracking.ForegroundOnlyLocationService
import kotlinx.android.synthetic.main.bottom_sheet_tracking.*
import java.lang.RuntimeException

class TrackingBottomSheet(context: Context, trackingListener: TrackingListener): BottomSheetDialogFragment(), Constant {

    private var dismissWithAnimation = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequestCode = 1000
    private var trackingInterval = 10
    private var coordinatesObtained = mutableListOf<MutableList<Double>>()
    private val mTrackingListener = trackingListener
    lateinit var preference: Preferences
    private val mContext = context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_tracking, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = Preferences(mContext)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        setUpViews()
        setUpListeners()
    }

    private fun setUpViews() {
        preference.getCoordinates(COORDS_DATA)?.let {
            coordinatesObtained = it
            setCoordinatesInfo()
        }
    }

    private fun setUpListeners() {
        swTrackingState.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvTrackingState.text = "Tracking automático"
                llTimeButtons.visibility = View.VISIBLE
                tvActionStatus.text = "Inactivo"
                ibAction.setImageResource(R.drawable.ic_precision)
            } else {
                if (tvActionStatus.text != "Detener") {
                    llTimeButtons.visibility = View.GONE
                    tvTrackingState.text = "Tracking manual"
                    tvActionStatus.text = "Actualizar coordenadas"
                    ibAction.setImageResource(R.drawable.ic_precision)
                } else {
                    swTrackingState.isChecked = true
                    Toast.makeText(mContext, "Deten el tracking primero", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ibAction.setOnClickListener {
            if (swTrackingState.isChecked) {
                if (tvActionStatus.text == "Inactivo") {
                    setAutomaticTrack()
                } else {
                    tvActionStatus.text = "Inactivo"
                    ibAction.setImageResource(R.drawable.ic_precision)
                    stopAutomaticTracking()
                }
            } else {
                manualTracking()
            }
        }

        bAccept.setOnClickListener {
            preference.saveCoordinates(COORDS_DATA, coordinatesObtained)
            mTrackingListener.getCoordinates(coordinatesObtained)
            stopAutomaticTracking()
            dismiss()
        }

        tvClean.setOnClickListener {
            mTrackingListener.cleanTrackingRoute()
            coordinatesObtained.clear()
            preference.saveCoordinates(COORDS_DATA, null)
            setCoordinatesInfo()
            stopAutomaticTracking()
           // mTrackingListener.getCoordinates(mutableListOf())
        }

        b5Secs.setOnClickListener {
            trackingInterval = 5
            if (tvActionStatus.text != "Detener") {
                b5Secs.setBackgroundResource(R.drawable.bg_round_button_border)
                b10Secs.setBackgroundResource(R.drawable.bg_round_button)
                b30Secs.setBackgroundResource(R.drawable.bg_round_button)
                b60Secs.setBackgroundResource(R.drawable.bg_round_button)
            } else {
                Toast.makeText(mContext, "Deten el tracking primero", Toast.LENGTH_SHORT).show()
            }
        }

        b10Secs.setOnClickListener {
            trackingInterval = 10
            if (tvActionStatus.text != "Detener") {
                b5Secs.setBackgroundResource(R.drawable.bg_round_button)
                b10Secs.setBackgroundResource(R.drawable.bg_round_button_border)
                b30Secs.setBackgroundResource(R.drawable.bg_round_button)
                b60Secs.setBackgroundResource(R.drawable.bg_round_button)
            } else {
                Toast.makeText(mContext, "Deten el tracking primero", Toast.LENGTH_SHORT).show()
            }
        }

        b30Secs.setOnClickListener {
            trackingInterval = 30
            if (tvActionStatus.text != "Detener") {
                b5Secs.setBackgroundResource(R.drawable.bg_round_button)
                b10Secs.setBackgroundResource(R.drawable.bg_round_button)
                b30Secs.setBackgroundResource(R.drawable.bg_round_button_border)
                b60Secs.setBackgroundResource(R.drawable.bg_round_button)
            } else {
                Toast.makeText(mContext, "Deten el tracking primero", Toast.LENGTH_SHORT).show()
            }
        }

        b60Secs.setOnClickListener {
            trackingInterval = 60
            if (tvActionStatus.text != "Detener") {
                b5Secs.setBackgroundResource(R.drawable.bg_round_button)
                b10Secs.setBackgroundResource(R.drawable.bg_round_button)
                b30Secs.setBackgroundResource(R.drawable.bg_round_button)
                b60Secs.setBackgroundResource(R.drawable.bg_round_button_border)
            } else {
                Toast.makeText(mContext, "Deten el tracking primero", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setCoordinatesInfo() {
        try {
            if (coordinatesObtained.isNotEmpty()) {
                tvLat.text = "Latitude: ${coordinatesObtained.last().first()}"
                tvLng.text = "Longitude: ${coordinatesObtained.last().last()}"
                if (coordinatesObtained.size == 1) {
                    tvPoints.text = "1 Punto Obtenido"
                } else {
                    tvPoints.text = "${coordinatesObtained.size} Puntos Obtenidos"
                }
            } else {
                tvLat.text = "Latitude: -"
                tvLng.text = "Longitude: -"
                tvPoints.text = "0 Puntos Obtenidos"
            }
        } catch (e: RuntimeException) {
            Log.e("hey!!", "almost crash")
        }

        // && tvLat != null && tvLng != null && tvPoints != null
    }

    private fun isLocationServiceActive(): Boolean {
        val lm = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        var isActive = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        if(gpsEnabled && networkEnabled) isActive = true
        return isActive
    }

    @SuppressLint("MissingPermission") // En la función de hasPermission() ya se piden permisos
    private fun manualTracking() {
        if (hasPermissions()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if(location != null) {
                        coordinatesObtained.add(mutableListOf(location.longitude, location.latitude))
                        setCoordinatesInfo()
                        preference.saveCoordinates(COORDS_DATA, coordinatesObtained)
                        mTrackingListener.getCoordinates(coordinatesObtained)
                    }
                }
        } else {
           requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            mContext as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            locationRequestCode
        )
    }

    private fun startAutomaticTracking() {
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val serviceIntent = Intent(mContext, ForegroundOnlyLocationService::class.java)
        (mContext as Activity).startService(serviceIntent)
        (mContext as Activity).registerReceiver(broadcastReceiver, IntentFilter("bgLocationService.receiver"))
        foregroundOnlyLocationService?.subscribeToLocationUpdates(trackingInterval.toLong())
    }

    private fun setAutomaticTrack() {
        if(isLocationServiceActive()) {
            if (hasPermissions()) {
                tvActionStatus.text = "Detener"
                ibAction.setImageResource(R.drawable.ic_stop)
                startAutomaticTracking()
            } else {
                requestPermissions()
            }
        } else {
            openSettingsPopUp()
        }
    }

    private fun stopAutomaticTracking() {
        foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
        NotificationManagerCompat.from(mContext).cancel(null, NOTIFICATION_ID_SERVICE)
    }

    private fun hasPermissions() : Boolean {
        return !(ActivityCompat.checkSelfPermission(
            mContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            mContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dismissWithAnimation = arguments?.getBoolean(DetailBottomSheet.ARG_DISMISS_WITH_ANIMATION) ?: false
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = dismissWithAnimation
    }

    companion object {
        const val TAG = "modalDetailSheet"
        const val ARG_DISMISS_WITH_ANIMATION = "dismiss_with_animation"
        fun newInstance(dismissWithAnimation: Boolean, context: Context, trackingListener: TrackingListener): TrackingBottomSheet {
            val modalSheet = TrackingBottomSheet(context, trackingListener)
            modalSheet.arguments = bundleOf(ARG_DISMISS_WITH_ANIMATION to dismissWithAnimation)
            return modalSheet
        }
    }

    interface TrackingListener {
        fun getCoordinates(coords: MutableList<MutableList<Double>>)
        fun cleanTrackingRoute()
    }

    /////---------new method--------////
    private var foregroundOnlyLocationServiceBound = false

    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    private lateinit var sharedPreferences: SharedPreferences

    // Monitors connection to the while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d("hey! enter", "here")
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            if(foregroundOnlyLocationService != null) Log.d("qwerty not", "null") else Log.d("qwerty  is ", "null")
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    private fun openSettingsPopUp() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Activar ubicación")
        dialog.setMessage(
            "Para poder actualizar el estado del pedido es necesario que active su ubicación"
        )
        dialog.setPositiveButton(
            "Entendido"){ _,_ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            requireActivity().startActivity(myIntent)
        }
        dialog.setNegativeButton(
            "Cancelar"){ dialog,_ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(context, ForegroundOnlyLocationService::class.java)
        activity?.bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                "bgLocationService.receiver")//ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }

    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val lat = java.lang.Double.valueOf(intent.getStringExtra("latutide") ?: "0.0")
            val lng = java.lang.Double.valueOf(intent.getStringExtra("longitude")?: "0.0")
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Receiving data from service
            val lat = java.lang.Double.valueOf(intent.getStringExtra("latutide") ?: "0.0")
            val lng = java.lang.Double.valueOf(intent.getStringExtra("longitude") ?: "0.0")
            Log.e("status coord:", lng.toString())
            coordinatesObtained.add(mutableListOf(lng, lat))
            setCoordinatesInfo()
        }
    }

}

