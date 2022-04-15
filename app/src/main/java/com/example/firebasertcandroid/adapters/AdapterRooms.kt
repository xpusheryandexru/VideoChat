package com.example.firebasertcandroid.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.ValueCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasertcandroid.databinding.SimpleItemBinding
import com.google.firebase.firestore.DocumentChange


class AdapterRooms(val clickItemValueCallback: ValueCallback<String>): ListAdapter<DocumentChange, AdapterRooms.DocumentChangeHolder>(DocumentChangeDiffCallback())
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentChangeHolder
    {
        return DocumentChangeHolder(
            SimpleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DocumentChangeHolder, position: Int)
    {
        holder.bind(getItem(position))
    }

    inner class DocumentChangeHolder(private val simpleItemBinding: SimpleItemBinding)
        :RecyclerView.ViewHolder(simpleItemBinding.root)
    {
        fun bind(documentChange: DocumentChange)
        {
            val doc=documentChange.document

            val strUri=doc.get("uri")?.toString()

            val text="$strUri \n\n ${documentChange.type}"

            simpleItemBinding.text1.text= text

            simpleItemBinding.root
                .setOnClickListener {

                    clickItemValueCallback.onReceiveValue("$strUri")

                }

        }

    }

}

private class DocumentChangeDiffCallback : DiffUtil.ItemCallback<DocumentChange>()
{

    override fun areItemsTheSame(oldItem: DocumentChange, newItem: DocumentChange): Boolean
    {
        return oldItem.document.id == newItem.document.id
    }

    override fun areContentsTheSame(oldItem: DocumentChange, newItem: DocumentChange): Boolean
    {
        return oldItem == newItem
    }

}
