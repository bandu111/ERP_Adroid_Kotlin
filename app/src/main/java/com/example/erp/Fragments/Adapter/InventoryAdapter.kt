package com.example.erp.Fragments.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erp.CustemView.EdInventoryButtomSheep
import com.example.erp.R
import com.example.erp.data.InventoryItem

class InventoryAdapter(
   var _list: List<InventoryItem>,
   var context: Context,
   private val fragmentManager: FragmentManager,
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InventoryAdapter.ViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.item_product,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryAdapter.ViewHolder, position: Int) {
        var item=_list[position]

        holder.item_titles.text=item.name
        holder.item_code.text=item.sku
        holder.item_quantity.text=item.quantity.toString()
        holder.item_tai.text=item.unit
      if (item.quantity.toInt()<5){
          holder.buhuo.visibility= View.VISIBLE
      }

        holder.more.setOnClickListener {

            EdInventoryButtomSheep.newInstance(item.id!!).show(fragmentManager, "arg_customer_id")
        }
    }

    override fun getItemCount(): Int {
        return  _list.size
    }
    class  ViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        val item_titles = itemView.findViewById<TextView>(R.id.item_titles)
        val item_code = itemView.findViewById<TextView>(R.id.item_code)
        val item_quantity = itemView.findViewById<TextView>(R.id.item_quantity)
        val item_tai = itemView.findViewById<TextView>(R.id.item_tai)
        val buhuo = itemView.findViewById<TextView>(R.id.buhuo)
        val more = itemView.findViewById<TextView>(R.id.more)
    }
}