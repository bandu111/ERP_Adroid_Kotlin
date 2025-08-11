package com.example.erp.Fragments.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.erp.Bean.More_item_Bean
import com.example.erp.R
import com.example.erp.data.CustomerDao

class ItemsAdapter(
    var list: List<More_item_Bean>,
  var  context: Context,
    private val onItemClick: (Int, More_item_Bean) -> Unit // 点击回调
) :RecyclerView.Adapter<ItemsAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemsAdapter.ViewHolder {
      var view=  LayoutInflater.from(context).inflate(R.layout.recycle_more_itemview,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemsAdapter.ViewHolder, position: Int) {
         lateinit var dao: CustomerDao

       var item= list[position]
       holder.tubiao.setImageResource(item.images)
        holder.item_title.text=item.title
        holder.item_submit.text=item.submit

        holder.linerontap.setOnClickListener {
            onItemClick(position, item) // 调用回调
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    class ViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        val tubiao = itemView.findViewById<ImageView>(R.id.tubiao)
        val item_title = itemView.findViewById<TextView>(R.id.item_title)
        val item_submit = itemView.findViewById<TextView>(R.id.item_submit)
        val linerontap = itemView.findViewById<LinearLayout>(R.id.linerontap)
    }
}