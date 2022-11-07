package seresco.maps.utils.lib.ui.wms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_wms_layer_item.view.*
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences

class WmsLayerItemAdapter(private val items: List<WMSItem>, private val listener: WmsLayerItemItemClickListener) : RecyclerView.Adapter<WmsLayerItemAdapter.MyViewHolder>(), Constant {

    lateinit var preference: Preferences
    var selectedItems: List<Int> = arrayListOf<Int>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val description = itemView.tv_wms_layer_name as TextView
        val eyeImageButton = itemView.ib_wms_layer as ImageButton
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = items[position]

        holder.description.text = item.description

        if (selectedItems.contains(position)) {
            holder.eyeImageButton.setImageResource(R.drawable.ic_show)
        } else {
            holder.eyeImageButton.setImageResource(R.drawable.ic_hide)
        }

        holder.itemView.setOnClickListener {
            preference.saveLayers(CURRENT_WMS_LAYERS_SELECTED, getWmsData(item))
            preference.saveLayersIndex(CURRENT_WMS_LAYERS_SELECTED_POSITION, position)
            selectedItems = preference.getLayersIndex(CURRENT_WMS_LAYERS_SELECTED_POSITION)
            if (selectedItems.contains(position)) {
                holder.eyeImageButton.setImageResource(R.drawable.ic_show)
            } else {
                holder.eyeImageButton.setImageResource(R.drawable.ic_hide)
            }
//            listener.onWmsLayerItemClicked()
        }

    }

//    private fun setStatusOfItem(holder: MyViewHolder) {
//
//    }

    private fun getWmsData(wmsItem: WMSItem): WMSItem {
        val baseUrl = "${wmsItem.url.split("GetCapabilities").toTypedArray().first()}GetMap" +
                "&layers=${wmsItem.description}" +
                "&bbox=%f,%f,%f,%f" +
                "&width=256" +
                "&height=256" +
                "&srs=EPSG:900913" +
//                "&srs=EPSG:23031" +
                "&format=image/png" +
                "&transparent=true"
//                "&styles=default" +
//                "&version=1.1.1"

        return WMSItem(baseUrl, wmsItem.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_wms_layer_item, parent, false)
        preference = Preferences(view.context)
        selectedItems = preference.getLayersIndex(CURRENT_WMS_LAYERS_SELECTED_POSITION)

        return MyViewHolder(
            view
        )
    }

    interface WmsLayerItemItemClickListener {
        fun onWmsLayerItemClicked(wmsItems: List<WMSItem>, positionItems: List<Int>)
    }
}