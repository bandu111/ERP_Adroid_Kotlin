package com.example.erp.Fragments.Adapter

import AddCustomerBottomSheet
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erp.Bean.Customer
import com.example.erp.CustemView.MoreBottomSheet
import com.example.erp.R

class MyCustomerAdapter (
var list: List<com.example.erp.data.Customer>,
private val fragmentManager: FragmentManager,
    var context: Context
): RecyclerView.Adapter<MyCustomerAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyCustomerAdapter.ViewHolder {
       val view= LayoutInflater.from(context).inflate(R.layout.item_customer,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyCustomerAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.tvName.text=item.company
        holder.tvSub.text=item.name
        holder.tvMeta.text=item.lastFollow
        holder.btnEdit.setOnClickListener {
            MoreBottomSheet.newInstance(item.id!!).show(fragmentManager, "MoreCustomer")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
         val tvName = itemView.findViewById<TextView>(R.id.tvName)
         val tvSub  = itemView.findViewById<TextView>(R.id.tvSub)
         val tvMeta = itemView.findViewById<TextView>(R.id.tvMeta)
         val btnEdit = itemView.findViewById<TextView>(R.id.btnEdit)
    }
}