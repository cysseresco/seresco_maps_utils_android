package seresco.maps.utils.lib.ui.wms

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_mbtiles.*
import kotlinx.android.synthetic.main.bottom_sheet_wms.*
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.ui.DetailBottomSheet
import seresco.maps.utils.lib.utils.wms.ExpandedMBTilesTileProvider
import seresco.maps.utils.lib.utils.wms.OnWmsCallback
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class OfflineWmsBottomSheet(googleMap: GoogleMap): BottomSheetDialogFragment() {

    private var dismissWithAnimation = false
    val PICK_REQUEST_CODE = 0
    val mGoogleMap = googleMap
    var selectedFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_mbtiles, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpListeners()
    }


    private fun setUpViews() {

    }

    private fun setUpListeners() {

        ll_select_mbtile.setOnClickListener {
            var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.type = "*/*"
            chooseFile = Intent.createChooser(chooseFile, "Choose a file")
            startActivityForResult(chooseFile, PICK_REQUEST_CODE)
        }

        b_mbtile.setOnClickListener {
            val tileProvider: TileProvider = ExpandedMBTilesTileProvider(selectedFile, 256, 256)
            mGoogleMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
            dismiss()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val uri: Uri? = data?.data
            Log.e("hey!", uri?.path.toString())
            uri?.let {
                selectedFile = getFile(requireContext(), uri)
                if (selectedFile?.exists() == true) {
                    val uriPathSplitted = uri.path?.split(".")?.toTypedArray()
                    if (uriPathSplitted?.last() == "mbtiles") {
                        setFileStatus(true)
                    } else {
                        setFileStatus(false, hasIncorrectFormat = true)
                    }
                } else {
                    setFileStatus(false)
                }
            }
        }
    }

    private fun setFileStatus(isCorrect: Boolean, hasIncorrectFormat: Boolean = false) {
        if (isCorrect) {
            b_mbtile.isEnabled = true
            ll_select_mbtile.visibility = View.GONE
            ll_mbtile_correct.visibility = View.VISIBLE
            ll_mbtile_incorrect.visibility = View.GONE
        } else {
            b_mbtile.isEnabled = false
            ll_select_mbtile.visibility = View.VISIBLE
            ll_mbtile_incorrect.visibility = View.VISIBLE
            ll_mbtile_correct.visibility = View.GONE
            if (hasIncorrectFormat) {
                tv_mbtile_incorrect.text = "El fichero no tiene el formato correcto"
            } else {
                tv_mbtile_incorrect.text = "No se pudo obtener el fichero"
            }
        }
    }

    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                ins?.let {
                    createFileFromStream(
                        it,
                        destinationFilename
                    )
                }
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
        return destinationFilename
    }

    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dismissWithAnimation = arguments?.getBoolean(DetailBottomSheet.ARG_DISMISS_WITH_ANIMATION) ?: false
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = dismissWithAnimation
    }

    companion object {
        const val TAG = "OfflineWmsBottomSheet"
        const val ARG_DISMISS_WITH_ANIMATION = "dismiss_with_animation"
        fun newInstance(dismissWithAnimation: Boolean, googleMap: GoogleMap): OfflineWmsBottomSheet {
            val modalSimpleListSheet = OfflineWmsBottomSheet(googleMap)
            modalSimpleListSheet.arguments = bundleOf(ARG_DISMISS_WITH_ANIMATION to dismissWithAnimation)
            return modalSimpleListSheet
        }
    }

    interface OfflineFileSelectedListener {
        fun getFileSelected(file: File)
    }
}