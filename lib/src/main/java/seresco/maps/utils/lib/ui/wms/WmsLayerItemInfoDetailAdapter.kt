package seresco.maps.utils.lib.ui.wms

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_wms_layer_info_item_detail.view.*
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.model.WMSLayer
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences

class WmsLayerItemInfoDetailAdapter(private val wmsItems: MutableList<WMSItem>, private val listener: WmsItemSelectedItemItemClickListener) : RecyclerView.Adapter<WmsLayerItemInfoDetailAdapter.MyViewHolder>(), Constant {

//    private val items = wmsLayer.items

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox = itemView.cb_layer_item as CheckBox
    }

    override fun getItemCount(): Int {
        return wmsItems.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = wmsItems[position]
        val preferences = Preferences(holder.checkBox.context)
        val selectedWmsItems = preferences.getLayers(CURRENT_WMS_LAYERS_SELECTED)
        if (selectedWmsItems.map { it.description == item.description }.any { it }) {
            item.isSelected = true
        }
        holder.checkBox.text = item.description
        holder.checkBox.isChecked = item.isSelected
        holder.checkBox.setOnClickListener {
            item.url = getWmsUrl(item)
            item.isSelected = holder.checkBox.isChecked
            if (holder.checkBox.isChecked) {
                listener.onWmsItemSelectedItemClicked(wmsItems)
            } else {
                listener.onWmsItemDeselectedItemClicked(item)
            }
        }
    }

    private fun getWmsUrl(wmsItem: WMSItem): String {
        return wmsItem.url
//        return "${wmsItem.url.split("GetCapabilities").toTypedArray().first()}GetMap" +
//                "&layers=${wmsItem.description}" +
//                "&bbox=%f,%f,%f,%f" +
//                "&width=256" +
//                "&height=256" +
//                "&srs=EPSG:3857" +
//                "&format=image/png" +
//                "&transparent=true" //+
               // "&styles=default"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_wms_layer_info_item_detail, parent, false)
        return MyViewHolder(
            view
        )
    }

    interface WmsItemSelectedItemItemClickListener {
        fun onWmsItemSelectedItemClicked(wmsItems: MutableList<WMSItem>)
        fun onWmsItemDeselectedItemClicked(wmsItem: WMSItem)
    }

}