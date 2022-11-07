package seresco.maps.utils.lib.ui.wms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_wms_item.view.*
import seresco.maps.utils.lib.R
import seresco.maps.utils.lib.databinding.AdapterWmsItemBinding
import seresco.maps.utils.lib.databinding.AdapterWmsItemBinding.inflate
import seresco.maps.utils.lib.model.WMSItem

class WmsItemAdapter(private val items: List<WMSItem>, private val listener: WmsItemItemClickListener) : RecyclerView.Adapter<WmsItemAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val description = itemView.tv_wms_description as TextView
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = items[position]

        holder.description.text = item.description

        holder.itemView.setOnClickListener {
            listener.onWmsItemClicked(item)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_wms_item, parent, false)
        return MyViewHolder(
            view
        )
    }

    interface WmsItemItemClickListener {
        fun onWmsItemClicked(wmsItem: WMSItem)
    }
}