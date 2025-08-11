package com.example.erp.Fragments.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.erp.Bean.Employee
import com.example.erp.R
import de.hdodenhof.circleimageview.CircleImageView

class RenShiAdapter(
   val list:  MutableList<Employee>,
   val context: Context
): RecyclerView.Adapter<RenShiAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RenShiAdapter.ViewHolder {
       var view= LayoutInflater.from(context).inflate(R.layout.recyclerview_renshi_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RenShiAdapter.ViewHolder, position: Int) {

       var item= list[position]
        holder.Names.text=  item.name
        holder.namestate.text=  item.state
        holder.textstatename1.text=  item.department
        holder.textstatename2.text=  item.position
        holder.phones.text=  item.phoneNumber
        holder.timess.text=  item.lastFollow
        holder.touxiang.setImageResource(item.image)
    }
    fun updateData(newData: MutableList<Employee>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
      return  list.size
    }

    class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val touxiang = itemView.findViewById<CircleImageView>(R.id.touxiang)
        val Names  = itemView.findViewById<TextView>(R.id.Names)
        val namestate = itemView.findViewById<TextView>(R.id.namestate)
        val textstatename1 = itemView.findViewById<TextView>(R.id.textstatename1)
        val textstatename2 = itemView.findViewById<TextView>(R.id.textstatename2)
        val phones = itemView.findViewById<TextView>(R.id.phones)
        val timess = itemView.findViewById<TextView>(R.id.times)

    }
}