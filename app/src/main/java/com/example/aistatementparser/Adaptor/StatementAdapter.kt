package com.example.aistatementparser.Adaptor

import android.graphics.Color
import android.os.Build.VERSION_CODES.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.example.aistatementparser.Model.TransactionDto
import com.example.aistatementparser.databinding.ItemTransactionBinding
import com.google.android.material.animation.Positioning

class StatementAdapter(
    private val onCategoryClick: (String) -> Unit): RecyclerView.Adapter<StatementAdapter.ViewHolder>() {

    private val list = mutableListOf<TransactionDto>()

    // Called from Fragment when ViewModel data changes
    fun submitList(newList: List<TransactionDto>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemTransactionBinding): RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder{
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        with(holder.binding) {
            tvDescription.text = item.Description
            tvDate.text = item.Date
            tvCategory.text = "Category: ${item.Category}"

            tvAmount.text =
                if (item.Type == "DEBIT") "− ₹${item.Amount}"
                else "+ ₹${item.Amount}"

            tvAmount.setTextColor(
                if (item.Type == "DEBIT") Color.RED
                else Color.GREEN
            )
            // 🔥 CATEGORY CLICK
            tvCategory.setOnClickListener {
                onCategoryClick(item.Category ?: "Uncategorized")
            }
        }
    }
}