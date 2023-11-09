package com.example.myapplication.ui.theme.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.theme.transaction.CarbonCreditItem
import com.example.myapplication.databinding.ItemCarbonCreditBinding

class CarbonCreditAdapter(private val itemList: List<CarbonCreditItem>) :
    RecyclerView.Adapter<CarbonCreditAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onBuyClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCarbonCreditBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(private val binding: ItemCarbonCreditBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonBuy.setOnClickListener {
                listener?.onBuyClick(adapterPosition)
            }
        }

        fun bind(item: CarbonCreditItem) {
            binding.textViewSellerID.text = item.username
            binding.textViewItemCount.text = item.판매물품개수.toString()
            binding.textViewItemPrice.text = item.판매물품가격.toString()
        }
    }
}