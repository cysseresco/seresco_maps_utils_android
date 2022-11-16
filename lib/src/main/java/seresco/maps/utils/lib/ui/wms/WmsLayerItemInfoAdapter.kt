package seresco.maps.utils.lib.ui.wms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_wms_layer_info_item.view.*
import kotlinx.android.synthetic.main.adapter_wms_layer_item.view.*
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.model.WMSItem
import seresco.maps.utils.lib.model.WMSLayer
import seresco.maps.utils.lib.utils.Constant
import seresco.maps.utils.lib.utils.Preferences

class WmsLayerItemInfoAdapter(context: Context, private val items: List<WMSLayer>, private val listener: WmsLayerItemInfoDetailAdapter.WmsItemSelectedItemItemClickListener) : RecyclerView.Adapter<WmsLayerItemInfoAdapter.MyViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val mContext = context

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.tv_wms_layer_info_name as TextView
        val recyclerView = itemView.rv_layer_info as RecyclerView
        val header = itemView.rl_wms_layer_header as RelativeLayout
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        viewManager = LinearLayoutManager(mContext)
        viewAdapter = WmsLayerItemInfoDetailAdapter(
            item.items as MutableList<WMSItem>,
            listener
        )
        holder.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        holder.header.setOnClickListener {
            setStatusOfWmsItems(holder)
        }
    }

    private fun setStatusOfWmsItems(holder: MyViewHolder) {
        if (holder.itemView.tv_wms_layer_info_status.text == "+") {
            hideWmsItems(holder)
        } else {
            showWmsItems(holder)
        }
    }

    private fun hideWmsItems(holder: MyViewHolder) {
        holder.itemView.rv_layer_info.visibility = View.GONE
        holder.itemView.tv_wms_layer_info_status.text = "-"
    }

    private fun showWmsItems(holder: MyViewHolder) {
        holder.itemView.rv_layer_info.visibility = View.VISIBLE
        holder.itemView.tv_wms_layer_info_status.text = "+"
    }

    private fun getWmsData(wmsItem: WMSItem): WMSItem {
        val baseUrl = "${wmsItem.url.split("GetCapabilities").toTypedArray().first()}GetMap" +
                "&layers=${wmsItem.description}" +
                "&bbox=%f,%f,%f,%f" +
                "&width=256" +
                "&height=256" +
                "&srs=EPSG:25829" +
                "&format=image/png" +
                "&transparent=true" +
                "&styles=default"

        return WMSItem(baseUrl, wmsItem.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_wms_layer_info_item, parent, false)


        return MyViewHolder(
            view
        )
    }

    interface WmsLayerSelectedItemItemClickListener {
        fun onWmsLayerSelectedItemClicked(wmsItems: List<WMSLayer>)
    }

}