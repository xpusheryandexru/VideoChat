package com.example.firebasertcandroid.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasertcandroid.databinding.ItemLogBinding
import com.example.firebasertcandroid.components.EntryLog

class AdapterLog: ListAdapter<EntryLog, AdapterLog.EntryLogHolder>(PlantDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryLogHolder
    {
        return EntryLogHolder(
            ItemLogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EntryLogHolder, position: Int)
    {
        holder.bind(
            getItem(position)
        )
    }

    class EntryLogHolder(
        private val binding: ItemLogBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {

        fun bind(item: EntryLog)
        {
            binding.apply {
                entryLog = item
                executePendingBindings()
            }
        }

    }

}

private class PlantDiffCallback : DiffUtil.ItemCallback<EntryLog>()
{

        override fun areItemsTheSame(oldItem: EntryLog, newItem: EntryLog): Boolean
        {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: EntryLog, newItem: EntryLog): Boolean
        {
            return oldItem == newItem
        }
    }
